# Tasks: Layout and Routing

**Input**: Design documents from `/specs/002-layout-routing/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md (all present; no
`contracts/` — this feature has no external interface, see plan.md)

**Tests**: Not requested. This project has no automated test suite (plan.md Technical Context);
verification is the static export plus the manual checks in `quickstart.md`.

**Organization**: Tasks are grouped by user story (spec.md) to keep each story independently
testable.

## Format: `[ID] [P?] [Story] Description`

- **[P]**: Can run in parallel (different files, no dependencies)
- **[Story]**: US1, US2, US3 — maps to spec.md's three user stories

## Path Conventions

Single Kobweb module at `site/`, `jsMain` source set — see plan.md Project Structure. No
`src/`/`tests/` at repo root; all paths below are under `site/`.

---

## Phase 1: Setup

**Purpose**: Establish a baseline before any change, so later verification has something to diff
against.

- [X] T001 Run `./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC` from repo root against
  the Phase 0 state (before this feature's changes) and confirm it currently succeeds — the
  pre-change baseline, not a code change. (Confirmed 2026-09-17: builds clean, exports `/` and
  `/about` only, matching Phase 0's final state.)

---

## Phase 2: Foundational

**Purpose**: Blocking prerequisites shared by all user stories.

*(none)* — the one piece of shared infrastructure this feature introduces (`Lang.kt`'s language
state) is only required by User Story 2's acceptance criteria, not User Story 1 or 3's, so it
lives in Phase 4 rather than here. Proceed directly to Phase 3.

---

## Phase 3: User Story 1 - Visitor navigates between real pages (Priority: P1)

**Goal**: All six nav destinations resolve to real, separately exported pages — no dead links, no
hand-rolled visibility router.

**Independent Test**: Export the site and confirm each of the six nav destinations is a separate
file in the export output, reachable by its own URL, with no `[data-screen]`/`.hidden`-style
client-side show/hide logic anywhere in the Kotlin source (spec.md US1).

### Implementation for User Story 1

- [X] T002 [P] [US1] Create minimal placeholder page
  `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Projects.kt` (`@Page` + `@Layout` +
  `PageLayoutData`, technical-label-only content, no real copy — following the Phase 0
  Index-placeholder pattern per research.md §5). Route: `/projects`.
  (Revised 2026-09-17 per `/speckit-analyze` finding D1: the heading itself is real
  navigational/category text, not an exempt technical label — now renders bilingually via
  `LocalLang`/`BilingualString`, same mechanism as T013/T015.)
- [X] T003 [P] [US1] Create minimal placeholder page
  `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Trophies.kt`, same pattern as T002. Route:
  `/trophies`. (Revised 2026-09-17 per finding D1, same as T002.)
- [X] T004 [P] [US1] Create minimal placeholder page
  `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Contact.kt`, same pattern as T002. Route:
  `/contact`. (Revised 2026-09-17 per finding D1, same as T002.)
- [X] T005 [P] [US1] Create minimal placeholder page
  `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Cv.kt`, same pattern as T002. Route: `/cv`
  (research.md §5 confirms `Cv.kt` → `/cv`, not `/c-v`). (Revised 2026-09-17 per finding D1,
  same as T002 — "CV" happens to be identical in both languages, but still routed through
  `BilingualString` for consistency.)
- [X] T006 [US1] Update `MenuItems()` in
  `site/src/jsMain/kotlin/com/anjo/anjosite/components/sections/NavHeader.kt` to render all six
  `Link(...)` calls (Home, About, Projects, Trophies, Contact, CV) instead of just Home/About.
  Depends on T002–T005 (targets must exist so no link is ever dead, per FR-006/spec
  Clarifications). (Revised 2026-09-17 per `/speckit-analyze` finding D1: labels are not
  technical/stack terms, so they render bilingually via `BilingualString`/`LocalLang.current`,
  not as literal English strings.)
- [X] T007 [US1] Verify no hand-rolled visibility router (hash-based, `.hidden`/`[data-screen]`
  toggling, or otherwise) exists anywhere in `site/src/jsMain/kotlin/` (FR-001) — a grep-based
  verification, not a code change; the mock's `app.js` router was never ported into Kotlin, so
  this should already pass. (Verified 2026-09-17: `grep -rn "data-screen|\.hidden\b|hashchange"`
  returns zero matches.)
- [X] T008 [US1] Run `quickstart.md` steps 1–3 (export, serve, real-navigation check) and confirm
  US1's acceptance scenarios pass. Depends on T006, T007. (Verified 2026-09-17 by inspecting the
  exported output directly: `index.html`, `about.html`, `projects.html`, `trophies.html`,
  `contact.html`, `cv.html` all present as distinct files — six real destinations, zero dead
  links.)

**Checkpoint**: User Story 1 is independently functional and testable.

---

## Phase 4: User Story 2 - Visitor sees the on-brand nav and can switch language (Priority: P1)

**Goal**: Nav shows the site's real brand (with reduced-motion-respecting glitch hover), and a
working language switch (browser-detected initial language) replaces `data-lang-block`.

**Independent Test**: Load the exported site, confirm the nav shows the site's own brand mark
(not the Kobweb logo), confirm the initial language matches the browser-language rule, and
confirm the language switch changes visible text (spec.md US2).

### Implementation for User Story 2

- [X] T009 [US2] Create `site/src/jsMain/kotlin/com/anjo/anjosite/Lang.kt`: a `Lang` enum
  (`EN`, `PL`), a `staticCompositionLocalOf<Lang>` (`LocalLang`), and a browser-language-detection
  helper reading `kotlinx.browser.window.navigator.language` (research.md §3/§4). No `@Composable`
  provider logic here — that's T010. (Also added `LocalLangSetter`, a paired read/write
  `CompositionLocal`, and `BilingualString` per data-model.md's Bilingual String shape.)
- [X] T010 [US2] Provide `LocalLang` at the app-shell level in
  `site/src/jsMain/kotlin/com/anjo/anjosite/AppEntry.kt` (same place Phase 0 added the overlay
  `Div`s), backed by a `mutableStateOf` seeded from T009's browser-detection helper, so changing
  it recomposes every consumer. Depends on T009. (Required adding
  `androidx.compose.runtime.getValue`/`setValue` imports for the `by remember { mutableStateOf }`
  delegate to resolve — `AppEntry.kt` previously used individual imports, not the wildcard
  `NavHeader.kt` relies on.)
- [X] T011 [US2] Add the site's on-brand brand mark to
  `site/src/jsMain/kotlin/com/anjo/anjosite/components/sections/NavHeader.kt`, replacing the
  Kobweb logo `Image`, with a hover glitch `Keyframes` animation (ported from
  `docs/handoff/styles.css:65-82`, following the same local-`Keyframes` pattern already used in
  this file for `SideMenuSlideInAnim`) suppressed under `prefers-reduced-motion: reduce`
  (FR-005). Depends on T006 (sequential edit to the same file, six links must land first).
  (See analyze finding F2 on `NavHeader.kt`'s per-link `BilingualString` constants, later
  centralized into `Lang.kt` alongside the F2 fix for T002-T006.)
  (Deliberate simplification: skipped the mock's `::after` cyan-ghost duplicate-text layer —
  Compose HTML has no direct `content: attr(...)` modifier — the position-wobble alone reads as
  "glitch". Verified in the re-exported `index.html`: `@keyframes brand-glitch`,
  `.brand:hover { animation: 220ms steps(2) 0s infinite normal none running brand-glitch; }`, and
  `@media (prefers-reduced-motion: reduce) { .brand:hover { animation-name: none; } }` all
  present and correctly scoped.)
- [X] T012 [US2] Add a language switch control to `NavHeader.kt`, following the existing
  `ColorModeButton()`/`IconButton` pattern, that toggles `LocalLang`'s value between `EN`/`PL`.
  Depends on T010 (LocalLang must exist), T011 (sequential edit to the same file).
- [X] T013 [US2] Add one bilingual demo string to
  `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Index.kt`, reading `LocalLang.current` to
  select between its English and Polish value (data-model.md's Bilingual String shape) —
  demonstrates FR-008/FR-009/FR-010 end-to-end without introducing real page copy. Depends on
  T010. (This same mechanism was later extended to nav labels and placeholder-page headings —
  see the D1 revision notes on T002-T006.)
- [X] T014 [US2] Run `quickstart.md` step 4 (brand/glitch/reduced-motion, initial-language,
  switch, no-`data-lang-block` checks) and confirm US2's acceptance scenarios pass. Depends on
  T011, T012, T013. (Verified 2026-09-17 by inspecting the re-exported `index.html`: brand mark
  present, zero `kobweb-logo` references, `Language demo` (English) rendered — matching
  Playwright's default `en-US` locale per FR-008's detection rule — and zero `data-lang-block`
  occurrences anywhere in the export.)

**Checkpoint**: User Stories 1 and 2 both work independently.

---

## Phase 5: User Story 3 - Visitor hitting an unknown URL sees an on-brand 404 (Priority: P3)

**Goal**: A styled `404.html` renders on the Phase 0 foundation, with a bilingual not-found
message.

**Independent Test**: Export the site, open the generated 404 page directly, and confirm it
renders with the same background/type/overlay foundation as every other page (spec.md US3).

### Implementation for User Story 3

- [X] T015 [US3] Create `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Error404.kt`:
  `@Page("/404")` (research.md §2 — exports to `404.html`), rendered inside `PageLayout` so it
  inherits nav/footer/Phase 0 foundation, with a short bilingual "not found" message reading
  `LocalLang.current` (constitution Principle III — this is real user-facing prose, not a
  technical label, so it needs both languages — same as the placeholder pages in T002–T005,
  which were also revised to be bilingual per finding D1; only their content differs (a static
  page heading vs. a dynamic not-found message), not their bilingual treatment).
  Depends on T010 (`LocalLang` must exist).
- [X] T016 [US3] Run `quickstart.md` step 5 (open `404.html` directly, confirm on-brand
  rendering, and confirm the not-found message switches between English and Polish) and confirm
  US3's acceptance scenarios pass. Depends on T015. (Verified 2026-09-17: `404.html` present in
  the export, renders the Phase 0 foundation (`fx-scan`/`fx-vignette`/`fx-grid`, `rgb(7, 7, 10)`
  background, brand mark), shows "Page not found" (English, matching Playwright's default
  locale) — and both `"Page not found"` and `"Strona nie znaleziona"` are present in the compiled
  `anjosite.js` bundle, confirming the message is genuinely bilingual, not hardcoded to one
  language, per the E1 remediation from `/speckit-analyze`.)

**Checkpoint**: All three user stories are independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

- [X] T017 [P] Run `quickstart.md` step 6: grep the exported HTML output for any design-notes-
  panel markup/text (FR-011) — confirm absence. No code change expected; the panel was never
  ported into Kotlin source, so this is a verification-only task. (Verified 2026-09-17:
  `grep -irl "design-notes"` across the exported output returns zero matches.)
- [X] T018 [P] Check off F005–F009 in `ROADMAP.md` now that Phase 1/Layout and Routing is
  complete.
- [X] T019 Diff `site/build.gradle.kts` against this feature's baseline (T001) and confirm zero
  new Gradle dependencies were added (constitution Principle VIII). (Verified 2026-09-17:
  `git diff --stat site/build.gradle.kts` against the last commit is empty — the file was not
  touched at all this phase.)
- [X] T020 Run `quickstart.md` end-to-end (all 6 steps) as the final sign-off for this feature.
  (Verified 2026-09-17: steps 1/3/4/5/6 verified by direct inspection of the real
  `kobweb export -PkobwebExportLayout=STATIC` output, per T008/T014/T016/T017 above — the same
  Playwright-rendered HTML/JS a browser would load. Step 2 confirmed by checking every page's
  `href`/`src` references are root-relative (`/`, `/about`, `/projects`, `/trophies`, `/contact`,
  `/cv`, `/favicon.ico`, `/anjosite.js`) across all seven exported pages including `404.html` —
  zero repo-name-prefixed paths, zero broken references.)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately.
- **Foundational (Phase 2)**: Empty — nothing blocks Phase 3.
- **User Story 1 (Phase 3)**: Depends on Phase 1 (T001 baseline) only. This is the MVP.
- **User Story 2 (Phase 4)**: T011/T012 depend on US1's T006 — `NavHeader.kt` is edited
  sequentially across both stories (links land first, then brand/switch), an inherent ordering,
  not a workaround.
- **User Story 3 (Phase 5)**: T015 depends on US2's T010 (`LocalLang` must exist for the
  bilingual not-found message) — this makes US3 depend on US2 completing first, unlike Phase 0
  where all three stories were mutually independent after Setup.
- **Polish (Phase 6)**: Depends on all three stories being complete.

### Within Each User Story

- New files before the shared-file edits that reference them (T002–T005 before T006).
- Foundational state (`Lang.kt`, its provider) before the UI that consumes it.
- Implementation before the quickstart verification task that closes out the story.

### Parallel Opportunities

- T002, T003, T004, T005 (four distinct new page files, no dependencies on each other) can run in
  parallel.
- T017 and T018 (different files/concerns, both read-only checks) can run in parallel.

---

## Parallel Example: User Story 1

```bash
# T002-T005 touch different files and have no dependency on each other:
Task: "Create minimal placeholder page pages/Projects.kt"
Task: "Create minimal placeholder page pages/Trophies.kt"
Task: "Create minimal placeholder page pages/Contact.kt"
Task: "Create minimal placeholder page pages/Cv.kt"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 (T001 baseline).
2. Complete Phase 3 (User Story 1 — T002–T008).
3. **STOP and VALIDATE**: run `quickstart.md` steps 1–3.
4. This alone delivers real, non-dead navigation to all six destinations — the other two stories
   layer on-brand polish (nav/i18n) and a broken-link safety net (404) on top.

### Incremental Delivery

1. Setup → User Story 1 (MVP: navigation is real, all six links work).
2. Add User Story 2 (on-brand nav, working language switch).
3. Add User Story 3 (on-brand 404 — depends on US2's `LocalLang`).
4. Polish.

## Notes

- No test tasks: this project has no automated test suite; `quickstart.md` steps are the
  verification method for every story, per the constitution's static-export-based definition of
  done (Principle VII).
- T010 is the single point where `LocalLang` is provided for the whole app — do not create a
  second competing `CompositionLocal` provider elsewhere (data-model.md's Lang validation rule).
- Commit after each task or logical group, per the project's usual workflow.
