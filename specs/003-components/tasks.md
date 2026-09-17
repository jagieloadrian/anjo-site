---

description: "Task list for Shared UI Components (003-components)"
---

# Tasks: Shared UI Components

**Input**: Design documents from `/specs/003-components/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: No automated test suite exists in this project and none was requested (plan.md
Technical Context). Verification is manual, via `quickstart.md`'s scenario checklist — referenced
directly from the relevant task below rather than duplicated as separate test tasks.

**Organization**: Tasks are grouped by user story (spec.md), in priority order.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3)
- File paths are relative to the repo root

## Path Conventions

All new files live in `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/` (plan.md's
Structure Decision — existing package, no new package taxonomy).

---

## Phase 1: Setup

No setup tasks. This feature adds files to an already-existing package
(`components/widgets/`, already home to `IconButton.kt`) and needs zero new Gradle dependencies
(plan.md Technical Context, research.md §1/§5 — `LaunchedEffect`, `delay`, and
`kotlinx.browser.window` are already transitively available). Proceed directly to Phase 2.

---

## Phase 2: Foundational

No foundational/blocking tasks. Each of the seven components is self-contained (plan.md's
Structure Decision: one file per component, no shared base or utility file), and the design
tokens they all depend on (`SiteTheme.kt`) already exist unchanged. There is nothing that must be
built before user story work can start — proceed directly to Phase 3.

---

## Phase 3: User Story 1 - Content-bearing components render consistently (Priority: P1) 🎯 MVP

**Goal**: `ProjectCard`, `StatRow`, `GameCover`, `TrophyRow`, and `Tag` each render their content
correctly and (except `StatRow`) are each a self-contained clickable container, per FR-001,
FR-004, FR-006, FR-007, FR-008, FR-009, FR-010–FR-015.

**Independent Test**: `quickstart.md` scenarios 1-6 — render each component against
representative sample data with no Phase 3 (site pages) or Phase 4 (JSON data) work in place.

### Implementation for User Story 1

- [X] T001 [P] [US1] Create `TagVariant` enum + `Tag` composable in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/Tag.kt` — always-clickable
      `Link`-wrapped pill (data-model.md `TagVariant`, research.md §3), 48px touch target under
      720px (research.md §2), `clamp()` typography, tokens from `SiteTheme.kt` only
- [X] T002 [P] [US1] Create `StatItem` + `StatRow` composable in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/StatRow.kt` — static
      label/value row, renders without error when `value` is `""` (data-model.md `StatItem`),
      `clamp()` typography, tokens from `SiteTheme.kt` only
- [X] T003 [P] [US1] Create `ProjectSummary` + `ProjectCard` composable in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/ProjectCard.kt` — whole-card
      `Link` (research.md §3), nullable `coverImageUrl` with CSS placeholder fallback (research.md
      §4), required `coverImageAlt`, 48px touch target under 720px, `clamp()` typography, tokens
      from `SiteTheme.kt` only
- [X] T004 [P] [US1] Create `GameCoverImage` + `GameCover` composable in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/GameCover.kt` — same
      clickable/placeholder/alt pattern as `ProjectCard` (data-model.md `GameCoverImage`),
      independent of `TrophyRow` (no shared entity), 48px touch target under 720px (research.md
      §2), `clamp()` typography, tokens from `SiteTheme.kt` only
- [X] T005 [P] [US1] Create `TrophyEntry` + `TrophyTier` enum + `TrophyRow` composable in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/TrophyRow.kt` — whole-row
      `Link`, tier→icon mapping owned internally, nullable `earnedAt` renders a locked/unearned
      state (data-model.md `TrophyEntry`), 48px touch target under 720px (research.md §2),
      `clamp()` typography, tokens from `SiteTheme.kt` only
- [ ] T006 [US1] Manually verify `quickstart.md` scenarios 1-6 (depends on T001-T005): render each
      of the five components with sample data on a temporary placeholder-page call site, confirm
      content visibility, placeholder fallback, and whole-component clickability. Partial:
      compiled successfully against real sample data (T001-T005 verified this way); the visual
      checks themselves need `kobweb run` + a browser, not done in this session.

**Checkpoint**: User Story 1 fully functional and testable independently — Phase 3 pages will
have every content-bearing component they need.

---

## Phase 4: User Story 2 - Boot-typing terminal intro (Priority: P2)

**Goal**: `Terminal` types its supplied text character by character, and both bypasses and
live-reacts to `prefers-reduced-motion` (FR-002, FR-003).

**Independent Test**: `quickstart.md` scenarios 7-9 — normal motion, reduced-motion from start,
and reduced-motion toggled mid-animation.

### Implementation for User Story 2

- [X] T007 [US2] Create `TerminalLine` + `TerminalLineStyle` enum + `Terminal` composable in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/Terminal.kt` — `LaunchedEffect`
      char-by-char reveal with mock-ported timings (research.md §5), `matchMedia` +
      `"change"`-listener-driven reduced-motion state gating the effect (research.md §1),
      `clamp()` typography, tokens from `SiteTheme.kt` only
- [ ] T008 [US2] Manually verify `quickstart.md` scenarios 7-9 (depends on T007): normal-motion
      typing, devtools reduced-motion emulation from load, and toggling emulation mid-animation.
      Not done — needs a browser + devtools; T007 compiled and reviewed but its actual runtime
      typing/reduced-motion behavior is unverified.

**Checkpoint**: User Stories 1 AND 2 both work independently.

---

## Phase 5: User Story 3 - Chronological history entry (Priority: P3)

**Goal**: `TimelineEntry` displays a date/title/description as one scannable entry (FR-005).

**Independent Test**: `quickstart.md` scenario 10.

### Implementation for User Story 3

- [X] T009 [US3] Create `TimelineItem` + `TimelineEntry` composable in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/TimelineEntry.kt` — static
      display, `clamp()` typography, tokens from `SiteTheme.kt` only
- [ ] T010 [US3] Manually verify `quickstart.md` scenario 10 (depends on T009). Not done — needs a
      browser; T009 compiled and reviewed only.

**Checkpoint**: All three user stories independently functional — all seven components exist.

---

## Phase 6: Polish & Cross-Cutting Concerns

**Purpose**: Verify the requirements that cut across every component, now that all seven exist.

- [ ] T011 Manually verify `quickstart.md` cross-cutting checks 11-16 (depends on T001-T005, T007,
      T009). Partial: check 13 (zero hardcoded colors/spacing outside `SiteTheme.kt`) confirmed by
      source grep — no `Color.rgb`/hex literals outside `SiteTheme.kt` in any of the 7 files; check
      14 (compile-time `alt`/`href` enforcement) confirmed — both are non-nullable constructor
      params, verified by the scratch-page compile. Checks 11 (719px touch target), 12 (fluid
      scaling as rendered), 15 (screen-reader accessible name), 16 (bilingual visual render) need
      `kobweb run` + a browser — not done.
- [X] T012 Confirm zero new Gradle dependencies were introduced (`git diff site/build.gradle.kts
      gradle/libs.versions.toml` — constitution Principle VIII, plan.md Constraints)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)** / **Foundational (Phase 2)**: Empty — no blockers, proceed straight to
  Phase 3.
- **User Story 1 (Phase 3)**: No dependencies on other stories. **MVP** — ships all
  content-bearing components Phase 3 (site) pages will use most.
- **User Story 2 (Phase 4)**: No dependency on US1; independently testable.
- **User Story 3 (Phase 5)**: No dependency on US1/US2; independently testable.
- **Polish (Phase 6)**: Depends on whichever of Phase 3/4/5 were completed.

### Within Each User Story

- Component-creation tasks (T001-T005, T007, T009) have no dependencies on each other within a
  story — each is a standalone new file.
- Each story's verification task depends on that story's component task(s) being done.

### Parallel Opportunities

- T001, T002, T003, T004, T005 (all of US1) can run fully in parallel — five independent files,
  no shared state, no shared new infrastructure.
- US1, US2, US3 component tasks (T001-T005, T007, T009) can all run in parallel across stories if
  staffed, since none of the seven components references another.

---

## Parallel Example: User Story 1

```bash
Task: "Create TagVariant enum + Tag composable in .../components/widgets/Tag.kt"
Task: "Create StatItem + StatRow composable in .../components/widgets/StatRow.kt"
Task: "Create ProjectSummary + ProjectCard composable in .../components/widgets/ProjectCard.kt"
Task: "Create GameCoverImage + GameCover composable in .../components/widgets/GameCover.kt"
Task: "Create TrophyEntry + TrophyTier + TrophyRow composable in .../components/widgets/TrophyRow.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 3 (US1) — five content-bearing components.
2. **STOP and VALIDATE**: run `quickstart.md` scenarios 1-6 (T006).
3. That alone unblocks every Phase 3 (site) page except ones needing `Terminal`/`TimelineEntry`.

### Incremental Delivery

1. Phase 3 (US1) → validate (T006) → five components ready.
2. Phase 4 (US2) → validate (T008) → `Terminal` ready.
3. Phase 5 (US3) → validate (T010) → `TimelineEntry` ready.
4. Phase 6 → full cross-cutting pass (T011) + dependency check (T012).

---

## Notes

- All 12 tasks are additive (new files) or verification-only — no existing file is modified by
  this feature (`SiteTheme.kt`/`Lang.kt` are reused as-is, per plan.md).
- Commit after each component task (or logical group of parallel ones).
- Stop at any Phase checkpoint to validate that story independently before continuing.
