import assert from "node:assert/strict";
import { test } from "node:test";
import {
  buildGames,
  buildStats,
  mapEarnedTrophy,
  pickRecentTitles,
  rankTrophies,
} from "./index.mjs";

function title(overrides = {}) {
  return {
    npCommunicationId: "NPWR00000_00",
    npServiceName: "trophy",
    trophyTitleName: "Test Game",
    trophyTitleIconUrl: "https://example.com/icon.png",
    trophyTitlePlatform: "PS4",
    lastUpdatedDateTime: "2024-01-01T00:00:00Z",
    progress: 50,
    definedTrophies: { bronze: 10, silver: 5, gold: 2, platinum: 1 },
    earnedTrophies: { bronze: 5, silver: 2, gold: 1, platinum: 0 },
    ...overrides,
  };
}

test("buildStats sums tier counts into total and formats completion as a percent", () => {
  const titles = [title({ progress: 40 }), title({ progress: 60 })];
  const summary = {
    trophyLevel: "459",
    earnedTrophies: { bronze: 4767, silver: 1620, gold: 719, platinum: 152 },
  };

  const stats = buildStats(titles, 346, summary);

  assert.deepEqual(stats, [
    { key: "level", value: "459" },
    { key: "games", value: "346" },
    { key: "completion", value: "50%" },
    { key: "platinums", value: "152" },
    { key: "total", value: "7258" },
    { key: "gold", value: "719" },
    { key: "silver", value: "1620" },
    { key: "bronze", value: "4767" },
  ]);
});

test("buildStats handles an empty title list without dividing by zero", () => {
  const summary = {
    trophyLevel: "1",
    earnedTrophies: { bronze: 0, silver: 0, gold: 0, platinum: 0 },
  };

  const stats = buildStats([], 0, summary);

  assert.equal(stats.find((s) => s.key === "completion").value, "0%");
  assert.equal(stats.find((s) => s.key === "total").value, "0");
});

test("pickRecentTitles sorts by lastUpdatedDateTime descending and caps at 10", () => {
  const titles = Array.from({ length: 15 }, (_, i) =>
    title({
      trophyTitleName: `Game ${i}`,
      lastUpdatedDateTime: new Date(2024, 0, i + 1).toISOString(),
    }),
  );

  const recent = pickRecentTitles(titles);

  assert.equal(recent.length, 10);
  assert.equal(recent[0].trophyTitleName, "Game 14");
  assert.equal(recent[9].trophyTitleName, "Game 5");
});

test("buildGames: title with a defined platinum is not muted and reports platinum status", () => {
  const [game] = buildGames([
    title({
      definedTrophies: { bronze: 10, silver: 5, gold: 2, platinum: 1 },
      earnedTrophies: { bronze: 10, silver: 5, gold: 2, platinum: 1 },
      progress: 100,
    }),
  ]);

  assert.equal(game.muted, false);
  assert.equal(game.percentText, "100% · platinum earned");
});

test("buildGames: title without a defined platinum is muted and reports a trophy count", () => {
  const [game] = buildGames([
    title({
      definedTrophies: { bronze: 10, silver: 5, gold: 0, platinum: 0 },
      earnedTrophies: { bronze: 3, silver: 1, gold: 0, platinum: 0 },
      progress: 27,
    }),
  ]);

  assert.equal(game.muted, true);
  assert.equal(game.percentText, "27% · 4 trophies");
});

test("mapEarnedTrophy uppercases tier and falls back when trophy detail is missing", () => {
  const entry = mapEarnedTrophy(
    title({ trophyTitleName: "Elden Ring" }),
    {
      trophyType: "platinum",
      trophyEarnedRate: "4.2",
      earnedDateTime: "2024-03-12T00:00:00Z",
    },
    undefined,
  );

  assert.deepEqual(entry, {
    tier: "PLATINUM",
    name: "Unknown Trophy",
    gameName: "Elden Ring",
    rarityPercent: "4.2",
    earnedAt: "2024-03-12T00:00:00Z",
    iconUrl: null,
  });
});

test("mapEarnedTrophy passes through the trophy detail's icon URL when present", () => {
  const entry = mapEarnedTrophy(
    title({ trophyTitleName: "Elden Ring" }),
    { trophyType: "gold", trophyEarnedRate: "12.5", earnedDateTime: "2024-05-01T00:00:00Z" },
    { trophyName: "Shardbearer", trophyIconUrl: "https://example.com/icon.png" },
  );

  assert.equal(entry.iconUrl, "https://example.com/icon.png");
});

test("rankTrophies sorts newest-earned first and caps at 10", () => {
  const pool = Array.from({ length: 12 }, (_, i) => ({
    tier: "BRONZE",
    name: `Trophy ${i}`,
    gameName: "Test Game",
    rarityPercent: "50",
    earnedAt: new Date(2024, 0, i + 1).toISOString(),
  }));

  const ranked = rankTrophies(pool);

  assert.equal(ranked.length, 10);
  assert.equal(ranked[0].name, "Trophy 11");
  assert.equal(ranked[9].name, "Trophy 2");
});
