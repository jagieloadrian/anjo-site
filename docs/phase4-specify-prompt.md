Phase 4 — Data (trophies): replace `trophies.yml`'s placeholder data step (added in Phase 5/
005-ci-deploy, `[MANUAL] blocked on ROADMAP F025`) with a real script that fetches the maintainer's
PlayStation Network trophy data via `psn-api` and regenerates
`site/src/jsMain/resources/public/trophies.json` to match the exact shape `Trophies.kt` already
parses — no page/component changes, this phase is data-sourcing only.

Scope (maps to ROADMAP.md F025):

1. Fetch script (F025)
   A Node/TypeScript script (new, e.g. `scripts/refresh-trophies/`) using the `psn-api` npm
   package: authenticate with the `NPSSO` secret (already wired as a GitHub Actions secret in
   Phase 5, currently holding a placeholder value — this phase's job is to make it a real PSN
   session token, not to re-plumb the secret itself), resolve the maintainer's PSN online ID
   (`Sirdiether18`, per the existing hardcoded kicker text in `Trophies.kt` and the
   `psnprofiles.com/Sirdiether18` link already in that file) to an `accountId`, fetch trophy
   titles/summary and recent trophy earns, and write
   `site/src/jsMain/resources/public/trophies.json` matching this exact schema (from
   `Trophies.kt`'s `parseTrophiesData`, `GameCoverImage`, `TrophyEntry`, `TrophyTier`):
   ```json
   {
     "stats": [{ "key": "level" | "platinums" | "games" | "completion", "value": "string" }],
     "games": [{ "imageUrl": "string|null", "alt": "string", "name": "string", "percentText": "string", "muted": boolean }],
     "trophies": [{ "tier": "BRONZE"|"SILVER"|"GOLD"|"PLATINUM", "name": "string", "gameName": "string", "rarityPercent": "string", "earnedAt": "string|null" }]
   }
   ```
   The four `stats` keys, and the `TrophyTier` enum's exact casing, are load-bearing — `Trophies.kt`
   does `TrophyTier.valueOf(it.tier as String)`, which throws on any other casing/value.

2. Wire it into `trophies.yml` (replaces the Phase 5 placeholder step)
   The workflow already has schedule + `workflow_dispatch` triggers, `NPSSO` read into an env var,
   and a conditional commit-back step (Phase 5). This phase adds a Node setup step
   (`actions/setup-node`) and replaces the placeholder `echo "blocked on ROADMAP F025..."` run
   step with installing dependencies and running the new script. The existing conditional
   commit-back step should need no changes — it already only commits if
   `trophies.json` actually changed.

3. Fail-safe behavior (carries over from Phase 5's FR-009, still applies)
   If the PSN API call fails (bad/expired NPSSO, PSN outage, rate limit) the script must exit
   non-zero and leave `trophies.json` untouched — the existing conditional commit-back step
   already guarantees no partial/corrupt commit happens as long as the script fails loudly instead
   of writing a partial file first.

4. Local/manual testing
   The maintainer needs to be able to run the script locally with their own `NPSSO` (obtained by
   logging into playstation.com in a browser and reading it from a cookie — out of scope to
   automate, standard psn-api setup step) to verify the output shape before trusting the nightly
   job.

Out of scope for this phase: ROADMAP F026 (`projects.json`/`skills.json` as static data instead of
hardcoded strings) — `projects.json` and `stack.json` already exist from Phase 3/004-pages per
`specs/SUMMARY.md`; no "skills.json" concept currently exists in the codebase, so F026 may already
be effectively done or may need its own follow-up — not this phase's concern. Any change to
`Trophies.kt`, `TrophyRow.kt`, `GameCover.kt`, `StatRow.kt`, or the JSON schema itself (schema is a
fixed contract this phase must match, not redesign). The deploy workflow (`deploy.yml`) and
GitHub Pages publish mechanism (Phase 5, already working and verified live).

Definition of done: running the script locally with a real `NPSSO` produces a `trophies.json` that
`kobweb export`'s existing `Trophies.kt` renders correctly (real stats/games/trophies, no
`TrophyTier.valueOf` crash); the nightly workflow runs for real via `workflow_dispatch` and either
commits a real refreshed `trophies.json` or fails loudly with no partial write; the live site at
`https://jagieloadrian.github.io/trophies` reflects the refreshed data after the next deploy.
