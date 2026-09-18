---

description: "Task list for Pages (004-pages)"
---

# Tasks: Pages

**Input**: Design documents from `/specs/004-pages/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: No automated test suite exists in this project and none was requested (plan.md
Technical Context). Verification is manual, via `quickstart.md`'s scenario checklist — referenced
directly from the relevant task below rather than duplicated as separate test tasks.

**Organization**: Tasks are grouped by user story (spec.md), in priority order (US1/US2 = P1/P2,
US3 = P2, US4 = P3).

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: Which user story this task belongs to (US1, US2, US3, US4)
- File paths are relative to the repo root

## Path Conventions

New/modified files live under `site/src/jsMain/kotlin/com/anjo/anjosite/` (pages, one new
component) plus one new static resource under `site/src/jsMain/resources/public/` and one
`site/build.gradle.kts` addition — all per plan.md's Structure Decision.

---

## Phase 1: Setup

No setup tasks. This feature adds/modifies files in already-existing packages (`pages/`,
`components/widgets/`) and needs zero new Gradle dependencies (plan.md Technical Context,
research.md §1-§6 — dynamic routing, `fetch`, `JSON.parse`, `mediaPrint`, and `TextInput`/`Button`
are all already available). Proceed directly to Phase 2.

---

## Phase 2: Foundational

No foundational/blocking tasks. Each user story below touches a disjoint set of files and reuses
the seven already-built Phase 2 components — nothing must be built before story work can start.
Proceed directly to Phase 3.

---

## Phase 3: User Story 1 - Home lands and reaches Projects (Priority: P1) 🎯 MVP

**Goal**: Real Home hero content with working CTAs, a real Projects grid from in-source data, and
a real, statically-exported detail route per project (FR-001, FR-002, FR-005, FR-006).

**Independent Test**: Load `/`, confirm real hero + `Terminal`; follow "view projects" to
`/projects`, confirm a real `ProjectCard` grid; open a card, confirm `/projects/{slug}` renders
full detail and survives a direct reload (quickstart.md scenarios 1-6).

### Implementation for User Story 1

- [X] T001 [US1] Create `ProjectEntry` data class (`BilingualString` title/descriptions/alt per
      data-model.md's bilingual-content pattern, analyze finding C1) + `toSummary(lang)` mapper +
      `projects: List<ProjectEntry>` in-source data, and replace the placeholder body of
      `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Projects.kt` with a real `ProjectCard` grid
      built by mapping `projects` through `toSummary(LocalLang.current)` (data-model.md
      `ProjectEntry`, FR-005/FR-006/FR-013). Handle the empty-list case without a broken layout.
- [X] T002 [US1] Create the dynamic detail route in
      `site/src/jsMain/kotlin/com/anjo/anjosite/pages/projects/Slug.kt` (`@Page("{}")`, research.md
      §1): read the slug via `rememberPageContext().route.params["slug"]`, look it up in the
      `projects` list from T001, and render the project's `fullDescription(LocalLang.current)`,
      tags (via the existing `Tag` component, each `href` pointing at `/projects` — research.md
      §7), and any linked assets. On an unmatched slug, call `router.navigateTo("/404")`
      (research.md §2). Depends on T001 (imports its `projects` list).
- [X] T003 [US1] Add one `addExtraRoute("/projects/{slug}")` call per project slug in
      `kobweb.app.export` in `site/build.gradle.kts` (the public API; the underlying `extraRoutes`
      property itself is `internal`, research.md §1) — without this, `kobweb export` silently skips
      every `/projects/{slug}` page, failing SC-006's per-project static-file requirement (analyze
      finding F3). Depends on T001 for the concrete slug values.
- [X] T004 [P] [US1] Replace the placeholder body of
      `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Index.kt` with real Home content: a
      `Terminal` boot sequence (real bilingual hero lines), kicker/heading/lede, and the two CTAs
      ("view projects" → `/projects`, "read cv" → `/cv`) (FR-001, FR-002). Independent of
      T001-T003 (different file).
- [X] T005 [US1] Verify `quickstart.md` scenarios 1-6 (Home hero, grid, detail view, direct
      reload of a detail URL, unknown-slug redirect, empty-grid state). Depends on T001-T004.

**Checkpoint**: Home and Projects (grid + detail) are real and statically exported. This alone
delivers the site's primary visitor journey.

---

## Phase 4: User Story 2 - About and the printable CV (Priority: P2)

**Goal**: A real `/about` route (doesn't exist today) and a real, print-legible `/cv` route, each
with its own dedicated data (FR-003, FR-004, FR-012).

**Independent Test**: Load `/about`, confirm real bio + `TimelineEntry` list; load `/cv`, confirm
real CV content and a clean browser print preview with no nav/footer chrome (quickstart.md
scenarios 7-9).

### Implementation for User Story 2

- [X] T006 [P] [US2] Add `BilingualTimelineItem` + `resolve(lang)` to
      `site/src/jsMain/kotlin/com/anjo/anjosite/Lang.kt` (data-model.md, analyze finding C1,
      shared with T007). Create `site/src/jsMain/kotlin/com/anjo/anjosite/pages/About.kt` (new
      route — wire it up with `@Page`/`@Layout`/`@InitRoute` like every other page): `AboutContent`
      data (bilingual bio + `List<BilingualTimelineItem>`), rendering the bio and one
      `TimelineEntry` per item resolved via `.resolve(LocalLang.current)` (data-model.md
      `AboutContent`, FR-003/FR-013). `NavHeader.kt` already links to `/about` — no nav change
      needed.
- [X] T007 [P] [US2] Create `CvSection`/`CvContent` data (using `BilingualTimelineItem` from T006)
      and replace the placeholder body of
      `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Cv.kt` with real content grouped into
      sections, each rendering its entries via `TimelineEntry` after `.resolve(LocalLang.current)`
      (data-model.md `CvContent`, FR-012/FR-013 — its own dedicated data, not reused from
      Projects/About).
- [X] T008 [P] [US2] Add a `mediaPrint { Modifier.display(DisplayStyle.None) }` rule to
      `NavHeaderStyle` in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/sections/NavHeader.kt` and to
      `FooterStyle` in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/sections/Footer.kt` (research.md §5,
      FR-017/SC-004). Independent file set from T006/T007.
- [X] T009 [US2] Verify `quickstart.md` scenarios 7-9 (About content, CV content, CV print
      preview). Depends on T006-T008.

Note: T007 depends on T006 for `BilingualTimelineItem`/`resolve(lang)` (added to `Lang.kt` as
part of T006) — no longer fully independent of it, unlike T008 which remains independent of both.

**Checkpoint**: About and CV are real; CV prints cleanly.

---

## Phase 5: User Story 3 - Trophies (Priority: P2)

**Goal**: A real `/trophies` route that fetches a static `trophies.json` and renders it through
`StatRow`/`GameCover`/`TrophyRow`, with defined loading and error states (FR-007, FR-008, FR-009,
FR-016).

**Independent Test**: With `trophies.json` reachable, confirm real rendered stats/games/trophies;
with the request blocked, confirm a loading skeleton then a clear error state, never blank
(quickstart.md scenarios 10-11).

### Implementation for User Story 3

- [X] T010 [P] [US3] Author a static placeholder
      `site/src/jsMain/resources/public/trophies.json` matching `TrophiesData`'s shape (stats with
      stable `key`s, games, trophies) — hand-written content, not generated (FR-008; the real PSN
      automation is Phase 4/ROADMAP F025, out of scope here).
- [X] T011 [P] [US3] Create `TrophiesStat`/`TrophiesData`/`TrophiesFetchState` + the page-owned
      `trophiesStatLabels: Map<String, BilingualString>` (data-model.md, analyze finding C2), and
      replace the placeholder body of
      `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Trophies.kt` with: a `fetch("/trophies.json")`
      call (awaited via `kotlinx-coroutines`'s `Promise.await()`) in a `LaunchedEffect`, manual
      `JSON.parse<dynamic>(...)` field mapping (research.md §3/§4), and three render branches —
      loading skeleton (reusing each component's placeholder mode), error/empty state, and the
      real render mapping each stat via `toStatItem(LocalLang.current)` into `StatRow`, and games/
      trophies as-is (untranslated proper nouns, research.md §8) into `GameCover`/`TrophyRow`
      (data-model.md `TrophiesFetchState`, FR-007, FR-009, FR-013, FR-016). Independent of T010 for
      authoring, but needed together for manual testing.
- [X] T012 [US3] Verify `quickstart.md` scenarios 10-11 (real data render, blocked-request
      loading/error states). Depends on T010-T011.

**Checkpoint**: Trophies renders real fetched data and never shows a blank page.

---

## Phase 6: User Story 4 - Contact via mailto (Priority: P3)

**Goal**: A single-field `ContactPrompt` component (the one new shared component this phase
introduces, FR-014 exception) wired into `/contact`, building a `mailto:` link client-side
(FR-010, FR-011).

**Independent Test**: Type a message and send — confirm a correctly addressed `mailto:` link with
fixed subject and the message as body; leave it empty — confirm send is blocked with a visible
reason (quickstart.md scenarios 12-13).

### Implementation for User Story 4

- [X] T013 [P] [US4] Create `ContactMessage` + `ContactPrompt` composable in
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/ContactPrompt.kt`, built from
      Silk's `TextInput`/`Button` (research.md §6): owns its own message + empty-validation state,
      builds and navigates to the `mailto:` URI (recipient/subject supplied by the caller,
      `encodeURIComponent`-escaped) on a valid send (data-model.md `ContactPrompt`, FR-010,
      FR-011). Send button label and "message required" text are internal `BilingualString`
      constants read via `LocalLang.current` (data-model.md, analyze finding B1, FR-011/FR-013) —
      not parameters. Independent of every other task this phase (new file, no other file depends
      on it yet).
- [X] T014 [US4] Replace the placeholder body of
      `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Contact.kt` with `ContactPrompt`, passing
      the site owner's fixed email and subject line. Depends on T013.
- [X] T015 [US4] Verify `quickstart.md` scenarios 12-13 (successful send, blocked empty send).
      Depends on T013-T014.

**Checkpoint**: Contact sends real `mailto:` links; the FR-014 exception is the only new shared
component this phase introduced.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Verify the requirements that cut across every route, now that all four stories exist.

- [X] T016 Verify `quickstart.md` cross-cutting checks 14-16: mobile check
      (`docs/handoff/mobile-check.html` at 390/430/768) across all six routes, bilingual toggle
      across all real copy (not just nav labels), and a full
      `kobweb export -PkobwebExportLayout=STATIC` run confirming a static file exists for each of
      the six routes plus every project slug registered in T003's `extraRoutes` (SC-001, SC-005,
      SC-006). Depends on Phases 3-6 all being complete.
- [X] T017 Confirm zero new Gradle dependencies were introduced (`git diff site/build.gradle.kts
      gradle/libs.versions.toml` shows only the `addExtraRoute(...)` calls from T003 — constitution
      Principle VIII, plan.md Constraints).

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)** / **Foundational (Phase 2)**: Empty — no blockers, proceed straight to
  Phase 3.
- **User Story 1 (Phase 3)**: No dependency on other stories. **MVP** — the site's primary
  visitor journey (Home → Projects → detail).
- **User Story 2 (Phase 4)**: No dependency on US1/US3/US4; independently testable.
- **User Story 3 (Phase 5)**: No dependency on US1/US2/US4; independently testable.
- **User Story 4 (Phase 6)**: No dependency on US1/US2/US3; independently testable.
- **Polish (Phase 7)**: Depends on all four stories being complete (T016 checks every route).

### Within Each User Story

- US1: T002 and T003 both depend on T001 (need the `projects` list/slugs); T004 is independent;
  T005 depends on T001-T004.
- US2: T007 depends on T006 (`BilingualTimelineItem` added to `Lang.kt` by T006); T008 is
  independent of both; T009 depends on all three.
- US3: T010 and T011 can be authored in parallel; T012 (manual verification) needs both.
- US4: T014 depends on T013; T015 depends on both.

### Parallel Opportunities

- T004 (Home) can run in parallel with T001 (Projects data/grid) — US1's own internal
  parallelism.
- T006 (About + shared `BilingualTimelineItem`) and T008 (print-hiding CSS) can run in parallel;
  T007 (CV content) starts once T006's `Lang.kt` addition lands.
- T010, T011 (trophies.json placeholder, Trophies.kt fetch/render) can run in parallel within US3.
- T013 (ContactPrompt) has no dependency on anything else this phase.
- Across stories: T001/T004 (US1), T006/T007/T008 (US2), T010/T011 (US3), and T013 (US4) touch
  entirely disjoint files — all four stories can proceed fully in parallel if staffed.

---

## Parallel Example: Starting all four stories at once

```bash
# Launch together:
Task: "Create ProjectEntry + projects list, real grid in pages/Projects.kt"
Task: "Real Home content in pages/Index.kt"
Task: "Create About.kt (new route) + BilingualTimelineItem in Lang.kt"
Task: "Add mediaPrint hide rules to NavHeader.kt/Footer.kt"
Task: "Author static trophies.json placeholder"
Task: "Fetch + render logic in pages/Trophies.kt"
Task: "Create ContactPrompt.kt"

# Starts once the above "About.kt" task lands its Lang.kt addition:
Task: "Real CV content in pages/Cv.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 3 (US1) — real Home + Projects grid + detail route + `extraRoutes`.
2. **STOP and VALIDATE**: run `quickstart.md` scenarios 1-6, including the direct-reload check on
   a `/projects/{slug}` URL (proves the static-export registration actually worked).
3. This alone delivers the site's primary evidence-of-work journey — the other three stories add
   background (About/CV), personality (Trophies), and conversion (Contact) on top.

### Incremental Delivery

1. Phase 3 (US1) → Home + Projects real, statically exported.
2. Phase 4 (US2) → About + CV real.
3. Phase 5 (US3) → Trophies real, fetch-driven.
4. Phase 6 (US4) → Contact real, `ContactPrompt` built.
5. Phase 7 → mobile/bilingual/full-export cross-cutting check (T016) + dependency check (T017).

---

## Notes

- T002/T003 (US1), T007→T006 (US2), and T014→T013 (US4) are the in-story file dependencies; every
  other task pair across this whole feature touches disjoint files.
- `ContactPrompt.kt` (T013) is the one new shared component this phase introduces — everything
  else reuses the seven Phase 2 components as-is.
- Commit after each task or logical group of parallel ones.
- Stop at any Phase checkpoint to validate that story independently before continuing.

---

## Phase 8: Convergence

**Purpose**: Close gaps found by `/speckit-converge` between spec.md/plan.md and the current
codebase (post-implementation ad hoc changes drifted from the original phase artifacts).

- [X] T018 CRITICAL: Restore bilingual send-button label and a visible "message required"
      indication in `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/ContactPrompt.kt`
      per FR-011/FR-013 (contradicts) — currently `send()` silently returns on an empty message
      with no user-facing indication, and every string in the component ("send", "type your
      message, hit enter", "opening your mail client — mailto:…") is hardcoded English with no
      `BilingualString`/`LocalLang` usage at all (regression from commit 7ab09f9, never restored).
- [X] T019 Reconcile Projects' runtime `projects.json` fetch
      (`site/src/jsMain/kotlin/com/anjo/anjosite/pages/Projects.kt`) with FR-006, which requires
      an in-source `List` for this phase and explicitly defers external JSON sourcing to Phase 4
      per FR-006 (contradicts) — either revert to an in-source list, or run a spec amendment
      (`/speckit-specify`/`/speckit-clarify`) updating FR-006 and plan.md to accept the pulled-
      forward JSON sourcing.
- [X] T020 Reconcile the new shared component
      `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/LinkCell.kt` (used by Home and
      Contact) against FR-014's single-exception rule (only `ContactPrompt` is authorized as a new
      shared component this phase) per FR-014 (contradicts) — either fold `LinkCell` into an
      existing component, or amend FR-014 to name it as a second authorized exception.
- [X] T021 Update plan.md's Technical Context/Storage section (or scope the change under Phase 4)
      to account for `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Index.kt`'s Home "Stack" band
      now fetching `stack.json` at runtime — plan.md currently states trophies.json is the one
      fetched artifact for this phase per plan.md (contradicts).
- [X] T022 Document `site/src/jsMain/kotlin/com/anjo/anjosite/Theme.kt` (dark/light theme toggle)
      and the NavHeader brand hover/glitch effects as an explicit out-of-band addition in spec.md's
      Assumptions, since neither is described by any FR in this spec (unrequested).
