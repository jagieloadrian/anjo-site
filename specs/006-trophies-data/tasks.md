# Tasks: PSN Trophies Data Automation

**Input**: Design documents from `/specs/006-trophies-data/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: No automated test suite in this project (unchanged from every prior phase) — verification
is `quickstart.md`'s manual scenario checklist, tracked below as `[MANUAL]` tasks.

**Organization**: Tasks are grouped by user story (spec.md P1/P2/P3). `[MANUAL]` marks tasks that
need a human with a real PSN account/NPSSO token — an automated agent cannot perform these.

## Phase 1: Setup (Shared Infrastructure)

- [X] T001 Create `scripts/refresh-trophies/package.json` (`"type": "module"`, single dependency
  `psn-api`) per plan.md's Project Structure.
- [X] T002 Run `npm install` in `scripts/refresh-trophies/` to generate
  `scripts/refresh-trophies/package-lock.json` (depends on T001).

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: The auth + fail-safe skeleton every user story builds on.

- [X] T003 Create `scripts/refresh-trophies/index.mjs`: read `NPSSO` from `process.env`, call
  `exchangeNpssoForAccessCode` → `exchangeAccessCodeForAuthTokens` (research.md §2), wrap the
  entire script body in one top-level `try/catch` that on error logs only `error.message` (never
  the NPSSO value) and calls `process.exit(1)` — no file write happens anywhere before the final,
  single write at the very end (FR-005, FR-007). Depends on T002.

**Checkpoint**: Script authenticates or fails loudly; no data fetched or written yet.

---

## Phase 3: User Story 1 - Trophy data refreshes automatically, for real (Priority: P1) 🎯 MVP

**Goal**: The nightly job writes real PSN stats/games/trophies into `trophies.json`.

**Independent Test**: Run the script locally with a real `NPSSO`; confirm `trophies.json` has real
data and the Trophies page renders it without crashing (quickstart.md steps 1-4).

### Implementation for User Story 1

- [X] T004 [US1] In `scripts/refresh-trophies/index.mjs`, fetch all pages of `getUserTitles(auth,
  "me")` (research.md §7 pagination), and compute: `stats.games` = first page's `totalItemCount`;
  `stats.completion` = mean of `progress` across every fetched title, rounded, `"${n}%"`. Depends
  on T003.
- [X] T005 [US1] In the same file, sort the full title list by `lastUpdatedDateTime` descending and
  keep the top 10 as `recentTitles` (data-model.md `games` mapping — corrected from an initial 6
  to match the live page's 10 game tiles). Depends on T004.
- [X] T006 [US1] In the same file, call `getUserTrophyProfileSummary(auth, "me")` and set
  `stats.level`, `stats.platinums`, `stats.gold`, `stats.silver`, `stats.bronze` directly from
  `trophyLevel`/`earnedTrophies`, and `stats.total` as their sum (research.md §4, data-model.md
  `stats` table). Depends on T003.
- [X] T007 [US1] In the same file, map `recentTitles` (T005) to the `games` array of
  `GameCoverImage` objects per data-model.md's `imageUrl`/`alt`/`name`/`percentText`/`muted` rules
  (research.md §6). Depends on T005.
- [X] T008 [US1] In the same file, for each of the 10 `recentTitles`, call `getTitleTrophies` and
  `getUserTrophiesEarnedForTitle` (correct `npServiceName` per title), merge by `trophyId`, keep
  only `earned === true`, pool across all 10 titles, sort by `earnedDateTime` descending, take the
  top 10, and map to the `trophies` array of `TrophyEntry` objects (`tier` uppercased) per
  data-model.md (research.md §8). Depends on T005.
- [X] T009 [US1] In the same file, assemble `{ stats, games, trophies }` from T004/T006/T007/T008
  and write it as formatted JSON to `site/src/jsMain/resources/public/trophies.json` — this MUST
  be the only write in the script, and MUST happen only after every fetch above has succeeded.
  Depends on T006, T007, T008.
- [X] T010 [US1] Replace the placeholder `run:` step in `.github/workflows/trophies.yml` with
  `actions/setup-node@v4` (`node-version: 20`) followed by `cd scripts/refresh-trophies && npm ci`
  and `node index.mjs`, keeping the existing `NPSSO` env wiring and the existing conditional
  commit-back step unchanged (research.md §10). Depends on T009.
- [ ] T011 [MANUAL] [US1] Trigger the `trophies.yml` workflow via `workflow_dispatch` on GitHub and
  confirm a real `chore: refresh trophies.json` commit lands on `main` (quickstart.md step 6).
  Depends on T010 and requires the real `NPSSO` secret already be in place (T015).

**Checkpoint**: User Story 1 fully functional — nightly job produces real data end-to-end.

---

## Phase 4: User Story 2 - A failed fetch never corrupts the site (Priority: P2)

**Goal**: Any fetch failure leaves `trophies.json` untouched and the run clearly failed.

**Independent Test**: Run the script with an invalid `NPSSO`; confirm non-zero exit and zero file
changes (quickstart.md step 5).

### Implementation for User Story 2

- [X] T012 [US2] Review `scripts/refresh-trophies/index.mjs` (T003-T009) and confirm no
  intermediate/partial write path exists anywhere before the single T009 write — this is a
  verification pass, not new code; fix if any early-return or partial-write path is found. Depends
  on T009.
- [X] T013 [MANUAL] [US2] Run quickstart.md step 5 locally
  (`NPSSO=not-a-real-token node index.mjs`) and confirm a non-zero exit code and that
  `site/src/jsMain/resources/public/trophies.json` is byte-for-byte unchanged (`git status` shows
  no diff). Depends on T012.

**Checkpoint**: User Stories 1 AND 2 both verified.

---

## Phase 5: User Story 3 - Maintainer can verify output before trusting the nightly job (Priority: P3)

**Goal**: The script is runnable and verifiable entirely on the maintainer's own machine.

**Independent Test**: Local run with a real `NPSSO` produces the same shape a CI run would, with no
GitHub Actions run needed (quickstart.md steps 1-4).

### Implementation for User Story 3

- [ ] T014 [MANUAL] [US3] Run quickstart.md steps 1-4 end to end with a real `NPSSO`: install
  deps, run the script, inspect the output JSON shape, run `kobwebExport`, and confirm the
  Trophies page renders real data with no console error. Depends on T009 (script must exist and be
  complete).

**Checkpoint**: All three user stories independently verified.

---

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T015 [MANUAL] Replace the `NPSSO` GitHub Actions secret's Phase 5 placeholder value with a
  real PSN session token (spec.md Assumptions) — required before T011 can succeed.
- [X] T016 [P] Add `total`/`gold`/`silver`/`bronze` entries to `statLabels` (and `statColors` if a
  color is wanted) in
  `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Trophies.kt`, per FR-008 — no other change to
  that file.
- [X] T017 [P] Mark **F025** done (`- [x]`) in `ROADMAP.md`, matching the convention already used
  for Faza 0-2 items.
- [ ] T018 [MANUAL] Verify SC-004 (NPSSO never leaks): after T011's real workflow run, grep the
  workflow's own run log in the GitHub Actions UI for the literal NPSSO value, `grep -r` the
  gitignored `site/.kobweb/site/` build output, and `git log -p -- site/src/jsMain/resources/public/trophies.json`
  — confirm zero matches in all three, mirroring 005-ci-deploy's equivalent deploy-key check.
  Depends on T011.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies.
- **Foundational (Phase 2)**: Depends on Setup — blocks all user stories.
- **User Story 1 (Phase 3)**: Depends on Foundational. All of T004-T009 touch the same file
  (`index.mjs`) so they run strictly sequentially, not in parallel, despite being one story.
  **T011 specifically also depends on T015 (Phase 6)** — a real `NPSSO` secret must be in place
  before a real `workflow_dispatch` run can succeed, even though T015's task number is higher.
  Do T015 before attempting T011.
- **User Story 2 (Phase 4)**: Depends on T009 (reviews the finished script) — not a rewrite, a
  verification pass.
- **User Story 3 (Phase 5)**: Depends on T009 (the finished script) — purely a manual run, no code.
- **Polish (Phase 6)**: T015 independent; T016/T017 depend on nothing but are logically last;
  T018 depends on T011 having already run.

### Parallel Opportunities

- T016 and T017 (different files, no shared dependency) can run in parallel.
- T015 (a manual secret-replacement step) has no code dependency and can happen any time before
  T011 — in practice, do it early since T011 blocks on it.

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Phase 1 (Setup) → Phase 2 (Foundational) → Phase 3 (US1).
2. **STOP and VALIDATE**: run quickstart.md steps 1-4 locally.
3. This alone (plus T015's manual secret swap) makes the nightly job real — US2/US3 are
   verification/trust-building passes over the same already-complete script, not new features.

### Incremental Delivery

1. Setup + Foundational → script authenticates or fails cleanly.
2. US1 → real data written locally and by CI.
3. US2 → failure path explicitly verified.
4. US3 → local-verification workflow explicitly verified.
5. Polish → `Trophies.kt` label map, `ROADMAP.md`, real secret in place.
