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

async function fetchAllTitles(authorization) {
  const titles = [];
  let offset = 0;
  let totalItemCount = Infinity;

  while (titles.length < totalItemCount) {
    const page = await getUserTitles(authorization, "me", {
      limit: TITLES_PAGE_SIZE,
      offset,
    });
    totalItemCount = page.totalItemCount;
    titles.push(...page.trophyTitles);
    if (page.trophyTitles.length === 0) break;
    offset += page.trophyTitles.length;
  }

  return { titles, totalItemCount };
}

function buildStats(allTitles, totalItemCount, summary) {
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

function pickRecentTitles(allTitles) {
  return [...allTitles]
    .sort(
      (a, b) =>
        new Date(b.lastUpdatedDateTime) - new Date(a.lastUpdatedDateTime),
    )
    .slice(0, RECENT_GAMES_COUNT);
}

function buildGames(recentTitles) {
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

async function fetchRecentTrophies(authorization, recentTitles) {
  const earnedPool = [];

  for (const title of recentTitles) {
    const npServiceName =
      title.npServiceName === "trophy2" ? undefined : "trophy";

    const [titleTrophies, userTrophies] = await Promise.all([
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

    const trophyById = new Map(
      titleTrophies.trophies.map((trophy) => [trophy.trophyId, trophy]),
    );

    for (const earned of userTrophies.trophies) {
      if (!earned.earned) continue;
      const detail = trophyById.get(earned.trophyId);
      earnedPool.push({
        tier: earned.trophyType.toUpperCase(),
        name: detail?.trophyName ?? "Unknown Trophy",
        gameName: title.trophyTitleName,
        rarityPercent: earned.trophyEarnedRate ?? "0",
        earnedAt: earned.earnedDateTime ?? null,
      });
    }
  }

  return earnedPool
    .sort((a, b) => new Date(b.earnedAt ?? 0) - new Date(a.earnedAt ?? 0))
    .slice(0, RECENT_TROPHIES_COUNT);
}

async function main() {
  const npsso = process.env.NPSSO;
  if (!npsso) {
    throw new Error("NPSSO environment variable is not set");
  }

  const accessCode = await exchangeNpssoForAccessCode(npsso);
  const authorization = await exchangeAccessCodeForAuthTokens(accessCode);

  const [{ titles: allTitles, totalItemCount }, summary] = await Promise.all([
    fetchAllTitles(authorization),
    getUserTrophyProfileSummary(authorization, "me"),
  ]);

  const recentTitles = pickRecentTitles(allTitles);

  const stats = buildStats(allTitles, totalItemCount, summary);
  const games = buildGames(recentTitles);
  const trophies = await fetchRecentTrophies(authorization, recentTitles);

  // Single write, only after every fetch above has succeeded (FR-005).
  const output = { stats, games, trophies };
  await writeFile(OUTPUT_PATH, `${JSON.stringify(output, null, 2)}\n`, "utf-8");
  console.log(`Wrote ${OUTPUT_PATH}`);
}

main().catch((error) => {
  console.error(`refresh-trophies failed: ${error.message}`);
  process.exit(1);
});
