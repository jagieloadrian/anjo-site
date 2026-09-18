# Phase 0 Research: PSN Trophies Data Automation

All decisions below are grounded in `psn-api`'s actual TypeScript source (fetched directly from
`github.com/achievements-app/psn-api`, `main` branch, September 2026) — not guesswork. Exact
field names and types are quoted from the real `.model.ts` files.

## 1. Runtime: plain Node.js script, no TypeScript toolchain

**Decision**: A single ESM JavaScript file (`scripts/refresh-trophies/index.mjs`), Node.js 20+
(the package's own stated minimum), with its own small `scripts/refresh-trophies/package.json`
declaring `psn-api` as the only dependency.

**Rationale**: Constitution Principle VIII (YAGNI on Dependencies) applies to this script too even
though it's outside the Gradle/Kotlin dependency graph — a ~150-line script doesn't justify adding
a TypeScript build step, a bundler, or a test framework. `psn-api` ships pre-built ESM with zero
production dependencies of its own, so `npm install` + `node index.mjs` is the entire toolchain.
Isolating it under `scripts/refresh-trophies/` (its own `package.json`) keeps the root of the repo
free of any Node artifacts, matching how `.github/workflows/` was kept isolated from `site/` in
Phase 5.

**Alternatives considered**: TypeScript (rejected — no type errors are worth a build step for one
script); a Kotlin/JS or Kotlin JVM script reusing the site's toolchain (rejected — `psn-api` is a
JS-only library with no JVM/Kotlin equivalent; wrapping it via Kotlin/JS interop would be far more
code than the problem justifies).

## 2. Auth flow

**Decision**: `exchangeNpssoForAccessCode(npsso)` → `exchangeAccessCodeForAuthTokens(accessCode)` →
`{ accessToken }`, exactly as the package's own README documents. Both calls throw on an invalid or
expired NPSSO; the script lets that exception propagate to a top-level catch that logs a
non-secret error message and exits non-zero **before any file write is attempted** — satisfying
FR-005 (no partial/corrupt `trophies.json` on failure) and FR-007 (NPSSO never logged: the error
object's message is logged, not the token itself, and NPSSO is never interpolated into any log
line by this script).

## 3. Account resolution: use `"me"`, not `makeUniversalSearch`

**Decision**: Every `psn-api` call in this script passes the literal string `"me"` as the
`accountId` parameter, not a resolved numeric ID.

**Rationale**: Confirmed directly in `psn-api`'s README example (`getUserTitles({ accessToken },
"me")`) and its docs: "When querying the titles associated with the authenticating account, the
numeric accountId can be substituted with `me`." Since the NPSSO *is* the maintainer's own PSN
session, `"me"` always resolves to the correct account with zero extra API calls and zero risk of
matching the wrong account by username. This **supersedes** FR-002's original framing (resolving
the online ID `Sirdiether18` to an account identifier) — no `makeUniversalSearch()` call is needed
at all. `Sirdiether18` remains purely a display/link value already hardcoded in `Trophies.kt`
(unchanged by this phase).

**Alternatives considered**: `makeUniversalSearch()` by username (rejected — strictly more code and
an extra network call for a resolution `"me"` already gives for free, given the credential is the
account owner's own).

## 4. Fetching stats (`level`, `platinums`, `total`, `gold`, `silver`, `bronze`)

**Decision**: One call, `getUserTrophyProfileSummary(auth, "me")`, returns exactly what's needed:

```ts
interface UserTrophyProfileSummaryResponse {
  trophyLevel: string;                 // → stats "level"
  earnedTrophies: TrophyCounts;        // { bronze, silver, gold, platinum } → stats gold/silver/bronze/platinums
}
// TrophyCounts.platinum here is a real earned COUNT (unlike the definedTrophies use of the same
// type, where platinum is 0|1 meaning "title has a platinum defined").
```

`total` (new stat) = `bronze + silver + gold + platinum` from that same response — no extra call.

**Rationale**: This is the account-wide, PSN-computed total — exactly what FR-003's `total`/
`gold`/`silver`/`bronze`/`platinums` keys need, with no manual summation across titles required.

## 5. Fetching `games` count and the 6 most-recently-updated titles

**Decision**: `getUserTitles(auth, "me")` (default page). `stats.games` = the response's own
`totalItemCount` field (the account's full title count — not just the page returned). The 6 games
shown in `games` are the 6 entries with the most recent `lastUpdatedDateTime`, sorted **client-side**
by this script (not assumed from API response order, even though psn-api's docs suggest that's
already the default order — sorting explicitly removes that assumption as a failure mode).

`TrophyTitle.lastUpdatedDateTime` (ISO 8601): "the date the most recent trophy was earned for the
title" — confirmed in `trophy-title.model.ts`. This is the field the 006 clarification session
settled on (correcting an earlier, incorrect assumption of a `lastPlayedDateTime` field, which does
not exist in this API).

## 6. `games` list field mapping (`GameCoverImage`)

| `GameCoverImage` field | Source | Notes |
|---|---|---|
| `imageUrl` | `TrophyTitle.trophyTitleIconUrl` | Always present per the type (non-nullable `string`) |
| `alt` | `` `${trophyTitleName} cover` `` | Matches the existing placeholder's alt-text convention |
| `name` | `TrophyTitle.trophyTitleName` | — |
| `percentText` | See below | — |
| `muted` | `definedTrophies.platinum === 0` | See below |

Inspecting the current placeholder `trophies.json` (Phase 3) shows the intended convention
directly: titles *with* a platinum trophy get non-muted tiles reading "`<percent>% · platinum
<status>`"; titles *without* one (e.g. older multiplayer-only titles) get muted tiles reading
"`<percent>% · trophies <status>`". This script reproduces that exact split using
`definedTrophies.platinum` (a real `0 | 1` flag per `TrophyCounts`'s doc comment — "1 if the group
contains a platinum trophy"):

- `definedTrophies.platinum === 1` → `muted: false`,
  `percentText: "${progress}% · platinum ${earnedTrophies.platinum ? "earned" : "locked"}"`
- `definedTrophies.platinum === 0` → `muted: true`,
  `percentText: "${progress}% · ${earnedTrophies.bronze + earnedTrophies.silver + earnedTrophies.gold} trophies"`

This is a cosmetic formatting choice (the field is opaque display text — confirmed in
`GameCover.kt`, which only renders it as `Text(cover.percentText)` and toggles a CSS class off
`muted`), not a functional ambiguity, so it's resolved here rather than via another clarification
round.

## 7. `completion` stat

**Decision**: The mean of `TrophyTitle.progress` (each title's own 0–100 percent-complete field)
across the maintainer's full title list, rounded to the nearest whole percent. Since `progress` is
per-page, this requires paging through `getUserTitles` with `{ limit, offset }` until
`totalItemCount` is covered — acceptable for a once-nightly job with no latency SC.

**Rationale**: `spec.md`'s own Assumptions explicitly deferred "how each field is computed from the
API's raw response" to this research step. No endpoint returns a single "overall completion %"
figure directly (`getUserTrophyProfileSummary.progress` is progress-to-*next-level*, a different
metric), so an average of per-title completion is the most direct, honest reading of "completion"
as a portfolio-page stat.

**Alternatives considered**: `getUserTrophyProfileSummary.progress` (rejected — measures level
progression, not game completion, would be a misleading label under `statLabels["completion"]`).

## 8. `trophies` list (10 most-recently-earned, across games)

**Decision**: Scan only the same 6 most-recently-updated titles already fetched for the `games`
list (§5) — no separate, broader scan. For each of those 6 titles, call both:

- `getTitleTrophies(auth, npCommunicationId, "all", { npServiceName })` → trophy names/icons
  (`TitleThinTrophy`: has `trophyName`, lacks `earned`/`earnedDateTime`)
- `getUserTrophiesEarnedForTitle(auth, npCommunicationId, "all", { npServiceName })` → earned
  status (`UserThinTrophy`: has `earned`/`earnedDateTime`/`trophyRare`/`trophyEarnedRate`, lacks
  `trophyName`)

merge the two lists by `trophyId` (exactly the pattern in psn-api's own "Build a user's complete
trophy list" example), keep only `earned === true`, pool all games' earned trophies together, sort
by `earnedDateTime` descending, take the first 10.

`npServiceName` per title: `undefined` for PS5 titles, `"trophy"` otherwise — read directly off
each title's own `npServiceName` field (`"trophy"` or `"trophy2"`), per `getTitleTrophies`'s
documented parameter contract.

**Rationale**: Recently-earned trophies are, almost by definition, concentrated in
recently-updated titles — `lastUpdatedDateTime` on a title only advances when a trophy is earned
in it. Reusing the same 6-title pool (already fetched for §5) avoids a second, broader per-title
scan across the account's entire library, which would mean dozens-to-hundreds of extra API calls
for a nightly job with no latency requirement to justify that cost.

`// ponytail: scans only the top-6-most-recent titles for candidate trophies — upgrade to a wider`
`// scan (e.g. top 15) only if the 10-trophy list frequently comes up short in practice.`

**Field mapping** (`TrophyEntry`):

| `TrophyEntry` field | Source |
|---|---|
| `tier` | `trophyType.toUpperCase()` (psn-api: lowercase `"bronze"\|"silver"\|"gold"\|"platinum"` → schema's `BRONZE`/`SILVER`/`GOLD`/`PLATINUM`) |
| `name` | merged `trophyName` |
| `gameName` | the title's `trophyTitleName` |
| `rarityPercent` | merged `trophyEarnedRate` (already a percentage string, e.g. `"4.2"`) |
| `earnedAt` | merged `earnedDateTime` (ISO 8601 string) |

## 9. Failure/rate-limit handling

**Decision**: No manual retry/backoff logic. Any thrown error (bad NPSSO, PSN 5xx, PSN 429) is
allowed to propagate to a single top-level `try/catch` that logs the error's `message` only, then
`process.exit(1)` — no partial `trophies.json` write. GitHub Actions' own run-failure marking
(already relied on by the existing `trophies.yml` scaffold) is the sole failure-visibility
mechanism, consistent with Phase 5 research.md §6's decision not to build custom notification
tooling.

**Rationale**: Constitution Principle VIII — a hand-rolled retry/backoff layer is unjustified
complexity for a job that already fails safe (leaves the last-good file untouched, per FR-005) and
runs again in 24 hours regardless.

## 10. CI wiring

**Decision**: `trophies.yml` gains one new step, `actions/setup-node@v4` (`node-version: 20`),
before a `run:` step doing `cd scripts/refresh-trophies && npm ci` then
`node index.mjs`, replacing the placeholder `echo ... && exit 0` step. `NPSSO` is passed as an env
var to the script's process (already read into env in the Phase 5 scaffold — unchanged). The
existing conditional commit-back step needs no change: it already only commits if
`trophies.json` actually differs, which is exactly true after either a successful fetch that
changed nothing (no earned trophies since last run) or a failed fetch (no file write at all).

## 11. Local/manual run (User Story 3)

**Decision**: `NPSSO=<token> node scripts/refresh-trophies/index.mjs` from the repo root, writing
directly to `site/src/jsMain/resources/public/trophies.json` — the same relative output path the
CI run uses, so `git diff` after a local run shows exactly what CI would have produced. No CLI
flags/config needed (YAGNI) — the script always reads `NPSSO` from `process.env` and always writes
to the one, fixed output path.
