# Phase 1 Data Model: PSN Trophies Data Automation

No database, no new Kotlin data class — the "data model" here is the exact `trophies.json` output
contract (already fixed by `Trophies.kt`'s parser, extended per the 006 clarification session) and
the `psn-api` → JSON field mapping the fetch script implements. See `research.md` for full
rationale on each mapping decision.

## `trophies.json` output contract

```json
{
  "stats": [
    { "key": "level", "value": "string" },
    { "key": "games", "value": "string" },
    { "key": "completion", "value": "string" },
    { "key": "platinums", "value": "string" },
    { "key": "total", "value": "string" },
    { "key": "gold", "value": "string" },
    { "key": "silver", "value": "string" },
    { "key": "bronze", "value": "string" }
  ],
  "games": [
    { "imageUrl": "string", "alt": "string", "name": "string", "percentText": "string", "muted": "boolean" }
  ],
  "trophies": [
    { "tier": "BRONZE|SILVER|GOLD|PLATINUM", "name": "string", "gameName": "string", "rarityPercent": "string", "earnedAt": "string|null" }
  ]
}
```

- `stats`: always exactly 8 entries, all 8 keys always present (FR-003, Edge Cases).
- `games`: always exactly 6 entries (or fewer only if the account has fewer than 6 titles total).
- `trophies`: up to 10 entries (may be fewer if the top-6 recently-updated titles collectively have
  fewer than 10 earned trophies — see research.md §8).

## `stats` field mapping

| key | Source (`psn-api`) | Computation |
|---|---|---|
| `level` | `UserTrophyProfileSummaryResponse.trophyLevel` | direct |
| `games` | `UserTitlesResponse.totalItemCount` | direct |
| `completion` | `TrophyTitle.progress` across all titles (paged) | mean, rounded, `"${n}%"` |
| `platinums` | `UserTrophyProfileSummaryResponse.earnedTrophies.platinum` | direct |
| `total` | `earnedTrophies.bronze + silver + gold + platinum` | sum |
| `gold` | `earnedTrophies.gold` | direct |
| `silver` | `earnedTrophies.silver` | direct |
| `bronze` | `earnedTrophies.bronze` | direct |

## `games` entity mapping (`GameCoverImage`)

Source: the 6 `TrophyTitle` entries with the most recent `lastUpdatedDateTime` (client-sorted).

| field | Source | Rule |
|---|---|---|
| `imageUrl` | `trophyTitleIconUrl` | direct |
| `alt` | `trophyTitleName` | `"${trophyTitleName} cover"` |
| `name` | `trophyTitleName` | direct |
| `percentText` | `progress`, `definedTrophies`, `earnedTrophies` | see research.md §6 |
| `muted` | `definedTrophies.platinum` | `=== 0` |

## `trophies` entity mapping (`TrophyEntry`)

Source: earned trophies (`earned === true`) merged from `getTitleTrophies` +
`getUserTrophiesEarnedForTitle` across the same 6 titles above, globally sorted by
`earnedDateTime` desc, top 10.

| field | Source | Rule |
|---|---|---|
| `tier` | `trophyType` | `.toUpperCase()` |
| `name` | `trophyName` | direct |
| `gameName` | title's `trophyTitleName` | direct |
| `rarityPercent` | `trophyEarnedRate` | direct (already a percent string) |
| `earnedAt` | `earnedDateTime` | direct (ISO 8601 or `null`) |

## Trophy Fetch Script (process, not a persisted entity)

- **Input**: `NPSSO` (env var), fixed output path
  `site/src/jsMain/resources/public/trophies.json`.
- **Output**: overwrites the output path with the contract above, or exits non-zero with the file
  untouched (FR-005).
- **No intermediate state persisted** — the script is stateless between runs; each run re-derives
  the full output from PSN's current data.
