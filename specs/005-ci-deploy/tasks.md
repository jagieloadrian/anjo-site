---

description: "Task list for CI/Deploy (005-ci-deploy)"
---

# Tasks: CI/Deploy

**Input**: Design documents from `/specs/005-ci-deploy/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: No automated test suite exists in this project (unchanged from every prior phase,
per plan.md Technical Context). Verification is manual, via `quickstart.md` — no test tasks below.

**Organization**: Tasks are grouped by user story (spec.md) to enable independent implementation
and testing of each story.

## Format: `[ID] [P?] [Story?] [MANUAL?] Description`

- **[P]**: Can run in parallel (different files/accounts, no dependency on an incomplete task)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- **[MANUAL]**: Requires a human with GitHub account/browser access (repo settings, secrets UI,
  Pages settings, or viewing a live URL/Action run) — a coding agent running `/speckit-implement`
  cannot complete these on its own and should hand them back to the user rather than attempt them

---

## Phase 1: Setup

- [X] T001 Create `.github/workflows/` directory at the repository root (does not exist yet)

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Secrets and configuration both later workflows depend on

**⚠️ CRITICAL**: No user story work can begin until this phase is complete

- [ ] T002 [MANUAL] Generate an SSH deploy-key pair for cross-repo publishing (local `ssh-keygen`
      is scriptable, but registering the public half as a write-enabled Deploy Key on
      `jagieloadrian/jagieloadrian.github.io` requires the GitHub UI/API on that separate repo) —
      research.md §4
- [ ] T003 [MANUAL] Add the private half of T002's key as the `DEPLOY_KEY` secret in the
      `anjo-site` repo's GitHub Actions secrets settings (depends on T002; research.md §4,
      data-model.md)
- [ ] T004 [P] [MANUAL] Confirm the `NPSSO` repo secret already exists in `anjo-site`'s GitHub
      Actions secrets settings (from Phase 4/F025 work), or add a placeholder value now
      (research.md §7, data-model.md)
- [X] T005 [P] Verify `site/.kobweb/conf.yaml` still has no `basePath` set — must stay unset/root,
      matching the Phase 0 user-page decision (spec.md Clarifications)

**Checkpoint**: Secrets and directory ready — user story implementation can now begin

---

## Phase 3: User Story 1 - Automatic publish on push (Priority: P1) 🎯 MVP

**Goal**: A push to `main` rebuilds and republishes the site automatically, end to end.

**Independent Test**: Push a small, visible content change to `main` and confirm the live site
reflects it without running any local command.

### Implementation for User Story 1

- [X] T006 [US1] Create `.github/workflows/deploy.yml` with `on: push: branches: [main]` +
      `workflow_dispatch` triggers and a `concurrency: { group: deploy, cancel-in-progress: true }`
      block (research.md §5)
- [X] T007 [US1] Add build steps to `.github/workflows/deploy.yml`: `actions/checkout@v4`,
      `actions/setup-java@v4` (`distribution: temurin`, `java-version: '21'`),
      `gradle/actions/setup-gradle`, then run
      `./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC` (depends on T006;
      research.md §1/§2/§3)
- [X] T008 [US1] Add the publish step to `.github/workflows/deploy.yml`:
      `peaceiris/actions-gh-pages@v4` with `external_repository:
      jagieloadrian/jagieloadrian.github.io`, `publish_branch: main`, `publish_dir:
      site/.kobweb/site`, `keep_files: false`, `deploy_key: ${{ secrets.DEPLOY_KEY }}`
      (depends on T007, T003; research.md §4)

**Checkpoint**: Push-to-publish pipeline exists and runs end to end — User Story 1 deliverable

---

## Phase 4: User Story 2 - Correct publish destination (Priority: P2)

**Goal**: The published site is reachable at the root-domain URL with no broken links/assets.

**Independent Test**: After a deploy, open the root-domain URL and click through every nav
destination and the project-detail pages.

### Implementation for User Story 2

- [ ] T009 [US2] [MANUAL] One-time step: enable GitHub Pages on
      `jagieloadrian/jagieloadrian.github.io` (Settings → Pages → source: `main` branch, root)
      after T008's first successful run (depends on T008; research.md §8, spec.md Clarifications)
- [ ] T010 [P] [US2] [MANUAL] Verify `https://jagieloadrian.github.io/` loads with no broken
      CSS/font/JS/image reference (depends on T009; spec.md SC-002, quickstart.md §2.5)
- [ ] T011 [P] [US2] [MANUAL] Click through all seven top-level routes plus one project detail
      page at the live URL, confirm none 404 (depends on T009; spec.md User Story 2 Acceptance
      Scenario 2, quickstart.md §2.5)
- [ ] T012 [US2] [MANUAL] Push a deliberately broken change (e.g. a Kotlin compile error), then
      on GitHub confirm the workflow run fails and the commit shows a failed check, and confirm
      the previously published site at the live URL remains untouched (depends on T008;
      spec.md SC-003/SC-006, quickstart.md §2.6)

**Checkpoint**: Destination correctness and failure-safety proven — User Story 2 deliverable

---

## Phase 5: User Story 3 - Automatic trophy data refresh (Priority: P3)

**Goal**: A nightly, secret-wired workflow exists and runs safely, independent of whether ROADMAP
F025's real data source has shipped yet.

**Independent Test**: Trigger the scheduled workflow (or wait for its first scheduled run) and
confirm it completes and its logs contain no credential value in plain text.

### Implementation for User Story 3

- [X] T013 [P] [US3] Create `.github/workflows/trophies.yml` with `schedule` (nightly cron) +
      `workflow_dispatch` triggers (research.md §7)
- [X] T014 [US3] Add the data step to `.github/workflows/trophies.yml`: read `NPSSO` into an
      environment variable (never a log line or command argument), running a placeholder command
      (e.g. `echo "blocked on ROADMAP F025 — psn-api script not yet implemented" && exit 0`) until
      the real script exists (depends on T013, T004; research.md §7, constitution Principle VI)
- [X] T015 [US3] Add a conditional commit-back step to `.github/workflows/trophies.yml`: if
      `site/src/jsMain/resources/public/trophies.json` changed, commit it to `main` (depends on
      T014; research.md §7)
- [ ] T016 [US3] [MANUAL] Trigger `.github/workflows/trophies.yml` manually via
      `workflow_dispatch`. On GitHub: read the run's logs end-to-end, confirm `NPSSO` never
      appears in plain text (depends on T015; spec.md SC-005 — logs, quickstart.md §3). Locally:
      diff `site/src/jsMain/resources/public/trophies.json`'s git blob hash from before the run to
      after — confirm it is unchanged, proving T014's placeholder step is a true no-op and the
      fail-safe path (spec.md FR-009) holds even with no real data source wired yet
- [X] T017 [P] [US3] Grep for a literal `NPSSO` or `DEPLOY_KEY` value — as opposed to the
      `${{ secrets.* }}` reference syntax, which is expected and fine — in two separate places,
      confirming zero matches in each (depends on T015; spec.md SC-005 — git history and
      published bundle):
      1. Tracked git history: `git log -p -- .github/workflows/trophies.yml
         site/src/jsMain/resources/public/trophies.json`
      2. The most recent local static export (build output, gitignored by `site/.gitignore` —
         never part of git history, so it needs its own plain-filesystem check): `grep -r
         'NPSSO\|DEPLOY_KEY' site/.kobweb/site/` (run after a local `kobwebExport`, or skip with a
         note if no local export exists yet)

**Checkpoint**: All three user stories independently functional

---

## Phase 6: Polish & Cross-Cutting Concerns

- [ ] T018 Run `quickstart.md` in full as one end-to-end pass, confirming every check listed
      there (depends on T012, T016, T017)
- [ ] T019 [P] Mark ROADMAP.md F027/F028/F029 as complete (`[x]`) once T018 passes (depends on
      T018)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies
- **Foundational (Phase 2)**: Depends on Setup — BLOCKS all user stories
- **User Story 1 (Phase 3)**: Depends on Foundational (needs T003's deploy key)
- **User Story 2 (Phase 4)**: Depends on User Story 1 (T008's first successful run) — not
  independent of US1's artifact, but its own verification value is independently testable
- **User Story 3 (Phase 5)**: Depends on Foundational only (T004's `NPSSO` secret) — fully
  independent of US1/US2, could be built in parallel with them
- **Polish (Phase 6)**: Depends on all three user stories being complete

### Parallel Opportunities

- T004 and T005 can run in parallel with T002/T003 (different secrets/files)
- T010 and T011 can run in parallel once T009 is done
- T013 (new file) can be built in parallel with the entire US1/US2 chain — no shared file, no
  shared dependency beyond Foundational
- T017 can run in parallel with T016 in principle (different check, same prerequisite T015), and
  needs no live trigger of its own
- T019 has no other Phase 6 task to conflict with

### Manual handoff points (for whoever runs `/speckit-implement`)

T002, T003, T004, T009, T010, T011, T012, T016 all require a human with GitHub account/browser
access — an implementing agent should complete every non-`[MANUAL]` task it can, then stop and
hand these back explicitly rather than guess at UI steps it cannot perform.

---

## Parallel Example: Foundational + User Story 3

```bash
# These two chains touch no common file and share only the Foundational checkpoint:
Task: "Generate deploy key, add DEPLOY_KEY secret (T002-T003)" → feeds User Story 1/2
Task: "Confirm NPSSO secret (T004)" → feeds User Story 3, independently
Task: "Create .github/workflows/trophies.yml (T013-T017)" → can proceed alongside deploy.yml work
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational (deploy key + secrets — CRITICAL, blocks US1; mostly `[MANUAL]`)
3. Complete Phase 3: User Story 1 — a push now rebuilds and republishes automatically
4. **STOP and VALIDATE**: push a real change, confirm the Action runs (destination correctness
   verification is US2's job, not required to call US1 "done" as a pipeline)

### Incremental Delivery

1. Setup + Foundational → secrets/directory ready
2. User Story 1 → pipeline exists and runs (MVP)
3. User Story 2 → destination proven correct, failure-safety proven
4. User Story 3 → nightly trophy scaffold proven, independent of US1/US2
5. Polish → one full `quickstart.md` pass, ROADMAP updated

---

## Notes

- [P] tasks touch different files/accounts and share no incomplete dependency
- US2 is not file-independent from US1 (both edit/rely on `deploy.yml`) — it is still
  independently *testable* as its own verification pass, per spec.md
- US3 is fully independent of US1/US2 — only Foundational (T004) blocks it
- `[MANUAL]` tasks require a human with GitHub account/browser access — see the "Manual handoff
  points" note above
- Commit after each task or logical group, per this repo's usual one-atomic-commit convention
