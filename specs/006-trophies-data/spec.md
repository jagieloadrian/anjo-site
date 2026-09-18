# Feature Specification: PSN Trophies Data Automation

**Feature Branch**: `feature-6/trophies-data`

**Created**: 2026-09-18

**Status**: Draft

**Input**: User description: "Phase 4 — Data (trophies): replace `trophies.yml`'s placeholder
data step (added in Phase 5/005-ci-deploy, blocked on ROADMAP F025) with a real script that
fetches the maintainer's PlayStation Network trophy data via `psn-api` and regenerates
`trophies.json` to match the exact shape `Trophies.kt` already parses. Maps to ROADMAP.md F025."

## Clarifications

### Session 2026-09-18

- Q: How many games/trophies should appear in `trophies.json`, and what stats are needed? → A:
  A limited recent-N subset (not the full account history) — 6 most-recently-active games, 10
  most-recently-earned trophies — plus full trophy-tier totals: overall trophy count, and separate
  platinum/gold/silver/bronze counts (not just platinums).
- Q: New `stats` key names for the tier totals? → A: `total`, `gold`, `silver`, `bronze` (matches
  existing lowercase-noun style of `level`/`games`/`completion`/`platinums`).
- Q: Exact recent-N limits for `games` and `trophies` lists? → A: 6 games / 10 trophies (matches
  the current placeholder's shape).
- Q: Sort key for "most-recently-active" games? → A: `lastUpdatedDateTime` (the PSN titles API's
  own field and default sort order — corrected during `/speckit-plan` research from the originally
  stated `lastPlayedDateTime`, which does not exist on the API response), not a derived
  last-trophy-earned-in-that-game date.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Trophy data refreshes automatically, for real (Priority: P1)

As the site maintainer, the nightly scheduled job (already scaffolded in Phase 5) now actually
fetches my real PlayStation trophy data instead of running a documented no-op — the Trophies page
shows my real stats, games, and recent trophies without me ever touching the file by hand.

**Why this priority**: This is the entire point of the phase — everything else (secret wiring,
scheduling, commit-back) already exists from Phase 5 and is just waiting on this real fetch step.

**Independent Test**: Run the fetch script locally with a real `NPSSO` value, confirm it produces
a `trophies.json` that the Trophies page renders with real data and no crash.

**Acceptance Scenarios**:

1. **Given** a valid `NPSSO` session token, **When** the script runs, **Then** it writes a
   `trophies.json` whose `stats`, `games`, and `trophies` reflect the maintainer's real PSN
   account data.
2. **Given** the generated `trophies.json`, **When** the site is built and the Trophies page is
   opened, **Then** it renders real stats/games/trophies with no error and no crash.
3. **Given** the nightly workflow runs on schedule (or via manual trigger), **When** it completes
   successfully, **Then** the refreshed file is committed to `main` automatically, with no manual
   step.

---

### User Story 2 - A failed fetch never corrupts the site (Priority: P2)

As the site maintainer, if the PSN fetch fails for any reason (expired session token, PSN outage,
rate limiting), the site keeps showing the last successful data instead of breaking or going
blank.

**Why this priority**: A trophy page that silently breaks (crashes, shows garbage, or wipes real
data because of a transient PSN hiccup) is worse than the current placeholder — this protects
against that regression once the automation goes live. Depends on User Story 1 existing but is a
distinct failure-handling guarantee worth its own verification.

**Independent Test**: Force the script to fail (e.g. an invalid session token) and confirm
`trophies.json` is left byte-for-byte unchanged and the workflow run is clearly marked failed.

**Acceptance Scenarios**:

1. **Given** an invalid or expired `NPSSO` value, **When** the script runs, **Then** it exits with
   a failure and writes no file changes at all.
2. **Given** a failed script run, **When** the workflow's existing commit-back step evaluates
   whether `trophies.json` changed, **Then** it finds no change and commits nothing.
3. **Given** a failed run, **When** the maintainer checks the workflow run, **Then** it's clearly
   marked as failed (not a silent success).

---

### User Story 3 - Maintainer can verify output before trusting the nightly job (Priority: P3)

As the site maintainer, I can run the fetch script on my own machine with my own session token to
see exactly what it would produce, before ever letting the nightly schedule touch the live file.

**Why this priority**: Lowest priority because it's a one-time trust-building step, not an
ongoing operational need — once the nightly job has proven itself, this matters much less. Still
independently valuable and testable on its own.

**Independent Test**: Run the script locally with a real `NPSSO`, inspect the resulting
`trophies.json` directly, without needing to touch GitHub Actions at all.

**Acceptance Scenarios**:

1. **Given** the script and a real `NPSSO` value, **When** run locally (outside any CI
   environment), **Then** it produces the same `trophies.json` shape as the CI run would.

---

### Edge Cases

- What happens when the PSN account has zero trophies for one or more tiers, or a game with no
  earned trophies yet? All eight fixed `stats` keys (`level`, `platinums`, `games`, `completion`,
  `total`, `gold`, `silver`, `bronze`) must always be present, even if a value is zero or not yet
  earned.
- What happens when a trophy or game name contains characters that break JSON encoding (quotes,
  emoji, non-Latin scripts)? Output must remain valid, parseable JSON regardless of the PSN
  account's actual trophy/game name content.
- What happens if PSN rate-limits or throttles the request mid-fetch? Treated identically to any
  other fetch failure (User Story 2) — no partial file, a clearly failed run.
- What happens if the `NPSSO` secret is still the Phase 5 placeholder value (never replaced with a
  real token) when the schedule fires? The run must fail clearly (not crash confusingly or hang),
  consistent with User Story 2.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST fetch the maintainer's real PlayStation Network trophy data (trophy
  titles/summary and recent trophy earns) using the existing `NPSSO` credential.
- **FR-002**: The system MUST identify the maintainer's PSN account to the API using the literal
  account identifier `"me"` — valid because the `NPSSO` credential itself already scopes every
  call to the authenticated account, so no separate online-ID-to-accountId resolution step is
  needed. The PSN online ID `Sirdiether18` remains purely a display/link value already hardcoded
  in `Trophies.kt`, untouched by this phase.
- **FR-003**: The system MUST write the fetched data to `trophies.json` in the structure the
  Trophies page parser expects — eight fixed `stats` entries (`level`, `games`, `completion`,
  `platinums`, `total`, `gold`, `silver`, `bronze`, where `total`/`gold`/`silver`/`bronze` are the
  account-wide trophy counts for that tier and `total` is the sum across all tiers), a `games` list
  limited to the 6 games with the most recent `lastUpdatedDateTime`, and a `trophies` list limited
  to the 10 most-recently-earned trophies, using the exact four-value tier vocabulary
  (`BRONZE`/`SILVER`/`GOLD`/`PLATINUM`).
- **FR-004**: The system MUST replace the Phase 5 placeholder step in the existing nightly
  workflow with this real fetch — the workflow's scheduling, secret-wiring, and commit-back
  behavior from Phase 5 remain unchanged.
- **FR-005**: On any fetch failure, the system MUST make no changes to `trophies.json` and MUST
  exit in a way that leaves the workflow run clearly marked as failed.
- **FR-006**: The fetch mechanism MUST be runnable by the maintainer outside of CI, using their
  own locally-supplied credential, producing the same output shape as a CI run would.
- **FR-007**: The credential used to authenticate MUST be supplied only via the existing secret
  mechanism (or an equivalent local-only input for manual runs) — never hardcoded, logged, or
  written into the output file or the client bundle.
- **FR-008**: Since `total`/`gold`/`silver`/`bronze` are new `stats` keys the existing Trophies page
  does not yet render with a friendly label, the system MUST also add minimal label entries for
  these four keys to the page's existing stat-label mapping, so they display consistently with the
  other stats — no other change to that page's structure, styling, or layout.

### Key Entities

- **Trophy Fetch Script**: The process that authenticates to PSN and produces `trophies.json`.
  Input: a PSN session credential (`NPSSO`) only — the account is identified as `"me"` (FR-002),
  no separate online-ID input needed. Output: a `trophies.json` file matching the existing fixed
  schema, or a failed run with the file left untouched.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: After a successful run, the Trophies page shows real PSN data (not placeholder
  em-dashes) with zero rendering errors.
- **SC-002**: 100% of failed fetch attempts (tested with a deliberately invalid credential) result
  in zero changes to the previously published `trophies.json`.
- **SC-003**: The maintainer can independently verify the fetch output on their own machine
  without needing a GitHub Actions run to see the result.
- **SC-004**: Across all recorded runs (successful or failed), the PSN session credential appears
  zero times in a log, in git history, or in the published site bundle.

## Assumptions

- FR-008's stat-label addition is a deliberate, narrow exception to this phase's original
  data-only framing (the seeding prompt excluded any `Trophies.kt` change) — the maintainer
  explicitly requested tier-count stats during clarification, and the only way to render them
  with proper labels is this one small, additive map update. No other page/component change is
  in scope.

- The maintainer's PSN online ID is `Sirdiether18`, per the value already hardcoded in
  `Trophies.kt`'s kicker text and its `psnprofiles.com/Sirdiether18` link — confirmed directly
  with the maintainer during spec drafting.
- The `NPSSO` GitHub Actions secret already exists (added during Phase 5/005-ci-deploy) but
  currently holds a placeholder value; replacing it with a real PSN session token is a one-time
  manual step the maintainer performs outside this repository's code, the same way the Phase 5
  deploy key was — not something this phase's automation can do for itself, since `NPSSO` is
  obtained by logging into playstation.com in a browser.
- `NPSSO` session tokens expire periodically (a known PSN/`psn-api` characteristic) — this phase
  does not attempt automatic token refresh; when the token expires, the nightly job simply fails
  per FR-005/User Story 2 until the maintainer manually replaces the secret. Automatic token
  refresh, if ever needed, is out of scope here.
- The exact set of PSN API fields available (e.g. whether a numeric "completion %" or a specific
  "level" figure is directly provided by `psn-api` vs. needing to be derived) is an implementation
  detail for `/speckit-plan` to research — this spec only fixes the *output shape*, not how each
  field is computed from the API's raw response.
