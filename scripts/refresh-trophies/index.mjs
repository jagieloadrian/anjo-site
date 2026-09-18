import { writeFile } from "node:fs/promises";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import {
  exchangeAccessCodeForAuthTokens,
  exchangeNpssoForAccessCode,
  getTitleTrophies,
  getUserTitles,
  getUserTrophiesEarnedForTitle,
  getUserTrophyProfileSummary,
} from "psn-api";

const __dirname = dirname(fileURLToPath(import.meta.url));
const OUTPUT_PATH = resolve(
  __dirname,
  "../../site/src/jsMain/resources/public/trophies.json",
);

const RECENT_GAMES_COUNT = 10;
const RECENT_TROPHIES_COUNT = 10;
const TITLES_PAGE_SIZE = 200;

export class RefreshTrophiesError extends Error {
  constructor(stage, cause) {
    super(`${stage} failed: ${cause.message}`, { cause });
    this.name = "RefreshTrophiesError";
    this.stage = stage;
  }
}

async function fetchAllTitles(authorization) {
  const titles = [];
  let offset = 0;
  let totalItemCount = Infinity;

  while (titles.length < totalItemCount) {
    console.log(`Fetching titles page at offset ${offset}...`);
    const page = await getUserTitles(authorization, "me", {
      limit: TITLES_PAGE_SIZE,
      offset,
    });
    totalItemCount = page.totalItemCount;
    titles.push(...page.trophyTitles);
    if (page.trophyTitles.length === 0) break;
    offset += page.trophyTitles.length;
  }

  console.log(`Fetched ${titles.length}/${totalItemCount} titles`);
  return { titles, totalItemCount };
}

export function buildStats(allTitles, totalItemCount, summary) {
  const { bronze, silver, gold, platinum } = summary.earnedTrophies;
  const total = bronze + silver + gold + platinum;
  const avgCompletion = allTitles.length
    ? Math.round(
        allTitles.reduce((sum, title) => sum + title.progress, 0) /
          allTitles.length,
      )
    : 0;

  return [
    { key: "level", value: String(summary.trophyLevel) },
    { key: "games", value: String(totalItemCount) },
    { key: "completion", value: `${avgCompletion}%` },
    { key: "platinums", value: String(platinum) },
    { key: "total", value: String(total) },
    { key: "gold", value: String(gold) },
    { key: "silver", value: String(silver) },
    { key: "bronze", value: String(bronze) },
  ];
}

export function pickRecentTitles(allTitles) {
  return [...allTitles]
    .sort(
      (a, b) =>
        new Date(b.lastUpdatedDateTime) - new Date(a.lastUpdatedDateTime),
    )
    .slice(0, RECENT_GAMES_COUNT);
}

export function buildGames(recentTitles) {
  return recentTitles.map((title) => {
    const hasPlatinum = title.definedTrophies.platinum === 1;
    const percentText = hasPlatinum
      ? `${title.progress}% · platinum ${
          title.earnedTrophies.platinum === 1 ? "earned" : "locked"
        }`
      : `${title.progress}% · ${
          title.earnedTrophies.bronze +
          title.earnedTrophies.silver +
          title.earnedTrophies.gold
        } trophies`;

    return {
      imageUrl: title.trophyTitleIconUrl,
      alt: `${title.trophyTitleName} cover`,
      name: title.trophyTitleName,
      percentText,
      muted: !hasPlatinum,
    };
  });
}

export function mapEarnedTrophy(title, earned, detail) {
  return {
    tier: earned.trophyType.toUpperCase(),
    name: detail?.trophyName ?? "Unknown Trophy",
    gameName: title.trophyTitleName,
    rarityPercent: earned.trophyEarnedRate ?? "0",
    earnedAt: earned.earnedDateTime ?? null,
    iconUrl: detail?.trophyIconUrl ?? null,
  };
}

export function rankTrophies(earnedPool) {
  return [...earnedPool]
    .sort((a, b) => new Date(b.earnedAt ?? 0) - new Date(a.earnedAt ?? 0))
    .slice(0, RECENT_TROPHIES_COUNT);
}

async function fetchRecentTrophies(authorization, recentTitles) {
  const earnedPool = [];

  for (const [i, title] of recentTitles.entries()) {
    console.log(
      `Fetching trophies for "${title.trophyTitleName}" (${i + 1}/${recentTitles.length})...`,
    );
    const npServiceName =
      title.npServiceName === "trophy2" ? undefined : "trophy";

    let titleTrophies, userTrophies;
    try {
      [titleTrophies, userTrophies] = await Promise.all([
        getTitleTrophies(authorization, title.npCommunicationId, "all", {
          npServiceName,
        }),
        getUserTrophiesEarnedForTitle(
          authorization,
          "me",
          title.npCommunicationId,
          "all",
          { npServiceName },
        ),
      ]);
    } catch (error) {
      throw new RefreshTrophiesError(
        `fetch trophies for "${title.trophyTitleName}"`,
        error,
      );
    }

    const trophyById = new Map(
      titleTrophies.trophies.map((trophy) => [trophy.trophyId, trophy]),
    );

    for (const earned of userTrophies.trophies) {
      if (!earned.earned) continue;
      earnedPool.push(
        mapEarnedTrophy(title, earned, trophyById.get(earned.trophyId)),
      );
    }
  }

  return rankTrophies(earnedPool);
}

async function main() {
  const npsso = process.env.NPSSO;
  if (!npsso) {
    throw new Error("NPSSO environment variable is not set");
  }

  console.log("Exchanging NPSSO for auth tokens...");
  let authorization;
  try {
    const accessCode = await exchangeNpssoForAccessCode(npsso);
    authorization = await exchangeAccessCodeForAuthTokens(accessCode);
  } catch (error) {
    throw new RefreshTrophiesError("authenticate with PSN", error);
  }

  let allTitles, totalItemCount, summary;
  try {
    [{ titles: allTitles, totalItemCount }, summary] = await Promise.all([
      fetchAllTitles(authorization),
      getUserTrophyProfileSummary(authorization, "me"),
    ]);
  } catch (error) {
    throw new RefreshTrophiesError("fetch titles or trophy summary", error);
  }

  const recentTitles = pickRecentTitles(allTitles);
  console.log(`Selected ${recentTitles.length} most recently played titles`);

  const stats = buildStats(allTitles, totalItemCount, summary);
  const games = buildGames(recentTitles);
  const trophies = await fetchRecentTrophies(authorization, recentTitles);

  const output = { stats, games, trophies };
  try {
    await writeFile(OUTPUT_PATH, `${JSON.stringify(output, null, 2)}\n`, "utf-8");
  } catch (error) {
    throw new RefreshTrophiesError(`write ${OUTPUT_PATH}`, error);
  }
  console.log(`Wrote ${OUTPUT_PATH}`);
}

const isMainModule = process.argv[1] === fileURLToPath(import.meta.url);
if (isMainModule) {
  main().catch((error) => {
    console.error(`refresh-trophies failed: ${error.message}`);
    if (error.cause) console.error(`caused by: ${error.cause.message}`);
    process.exit(1);
  });
}
