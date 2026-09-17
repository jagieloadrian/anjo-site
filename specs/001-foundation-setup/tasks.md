# Tasks: Foundation Setup

**Input**: Design documents from `/specs/001-foundation-setup/`

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
  the unmodified template and confirm it currently succeeds — this is the pre-change baseline, not
  a code change. (Confirmed 2026-09-17: builds clean, exports `/` and `/about` to
  `site/.kobweb/site/`.)

---

## Phase 2: Foundational

**Purpose**: Blocking prerequisites shared by all user stories.

*(none)* — this feature is small enough that there is no shared infrastructure to build before
story work starts; every story's tasks are self-contained edits to existing files. Proceed
directly to Phase 3.

---

## Phase 3: User Story 1 - Visitor sees the on-brand visual foundation (Priority: P1) 🎯 MVP

**Goal**: Background/text/accent colors, typefaces, global reset, and the three decorative
overlays match the mock, demonstrated on the existing Index page.

**Independent Test**: Export the site and open the output (including Index) — background, text,
accent colors and both typefaces match the mock, no default Kobweb styling visible (spec.md US1).

### Implementation for User Story 1

- [X] T002 [P] [US1] Port the five mock tokens (`--bg`, `--ink`, `--pink`, `--cyan`, `--red`) into
  `SitePalette`/`SitePalettes` in `site/src/jsMain/kotlin/com/anjo/anjosite/SiteTheme.kt`, setting
  both `light` and `dark` to the same values (research.md §4).
- [X] T003 [P] [US1] Add Archivo + JetBrains Mono Google Fonts `<link>` elements via
  `kobweb.app.index.head` in `site/build.gradle.kts` (research.md §2).
- [X] T004 [US1] Add the global reset layer (margin/padding/box-sizing, base font stack, base
  background/text color sourced from T002's tokens) to `initSiteStyles` in
  `site/src/jsMain/kotlin/com/anjo/anjosite/AppStyles.kt`. Depends on T002.
- [X] T005 [US1] Add the three overlay styles — scanline, vignette, background grid — (fixed
  position, correct z-index/opacity/gradients per `docs/handoff/styles.css:40-56`, and
  `pointer-events: none` per FR-007) to
  `site/src/jsMain/kotlin/com/anjo/anjosite/AppStyles.kt`. Depends on T002.
- [X] T006 [US1] Render the three overlay elements at the app-shell level in
  `site/src/jsMain/kotlin/com/anjo/anjosite/AppEntry.kt` so they appear on every page, not just
  Index. Depends on T005.
- [X] T007 [US1] Update `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Index.kt` to a minimal
  placeholder that renders using the new tokens/typefaces (replace the default template demo
  content — no real copy, no navigation, per FR-010). The placeholder MUST contain no interactive
  elements (drop the template's boilerplate `Button`/CTA rather than resizing it) — constitution
  Principle V's 48px touch-target rule has no verification step in this phase, so the safest
  compliant state is zero clickable elements on the placeholder. Depends on T002, T004.
  (Revised 2026-09-17 per `/speckit-analyze` finding D1: an earlier version used a descriptive
  English sentence with no Polish counterpart, violating constitution Principle III. Replaced with
  color swatches + font-name labels — technical labels are explicitly exempt from bilingual parity
  per Principle III — verified in the re-exported `index.html`.)
- [X] T007b [US1] Remove the decorative `SvgCobweb` demo graphic and its invocation from
  `site/src/jsMain/kotlin/com/anjo/anjosite/components/layouts/PageLayout.kt` — discovered during
  implementation: `PageLayout` wraps every page (including Index) and renders this template demo
  SVG regardless of T007's changes, which would otherwise leave default Kobweb styling visible and
  violate US1's acceptance criteria. Not in plan.md's original file list; added by explicit user
  confirmation during `/speckit-implement`. Depends on T002.
- [X] T008 [US1] Run `quickstart.md` steps 1–3 (export, serve, visual/DevTools check) and confirm
  US1's acceptance scenarios pass. Depends on T003, T006, T007, T007b. (Verified 2026-09-17 by
  inspecting the exported `index.html` directly: `--silk-background-color: rgb(7, 7, 10)`,
  `--silk-color: rgb(242, 240, 239)`, `font-family: Archivo, system-ui, sans-serif`, no leftover
  template demo content — CLS not separately measured in an interactive browser in this
  environment; no numeric CLS threshold was set for this phase per plan.md Performance Goals.)

**Checkpoint**: User Story 1 is independently functional and testable.

---

## Phase 4: User Story 3 - Site is reachable at its published GitHub Pages address (Priority: P1)

**Goal**: Confirm the site is correctly configured for a root (`/`) GitHub Pages deploy with no
broken references.

**Independent Test**: Export with the base path configured, serve from that same path, confirm no
404s (spec.md US3).

### Implementation for User Story 3

- [X] T009 [US3] Confirm `site/.kobweb/conf.yaml` has no `basePath` key (default `""` = root is
  already correct per research.md §1) — leave the file as-is; this task is a verification, not an
  edit.
- [X] T010 [US3] Run `quickstart.md` steps 2 and 5 (serve the export from `/`, confirm zero 404s
  in the Network tab). Depends on T008, T009. (Verified 2026-09-17: every local asset/link
  reference in the exported HTML is root-relative — `/`, `/about`, `/favicon.ico`, `/anjosite.js`,
  `/kobweb-logo.png` — none prefixed with the repo name.)

**Checkpoint**: User Stories 1 and 3 both work independently.

---

## Phase 5: User Story 2 - Visitor with reduced-motion preference sees a calmer page (Priority: P2)

**Goal**: Scanline overlay is suppressed under `prefers-reduced-motion: reduce`; vignette and
grid remain.

**Independent Test**: With reduced-motion enabled, load the export and confirm the scanline is
absent while everything else renders normally (spec.md US2).

### Implementation for User Story 2

- [X] T011 [US2] Add a `prefers-reduced-motion: reduce` rule that hides only the scanline overlay
  (`fx-scan`), following the existing `CSSMediaQuery.MediaFeature` pattern already used in
  `initSiteStyles` for `body` scroll-behavior, in
  `site/src/jsMain/kotlin/com/anjo/anjosite/AppStyles.kt` (research.md §3). Depends on T005.
- [X] T012 [US2] Run `quickstart.md` steps 4 and 6 (reduced-motion emulation, overlay
  click-through check) and confirm US2's acceptance scenarios pass. Depends on T011. (Verified
  2026-09-17 by inspecting the exported CSS directly:
  `@media (prefers-reduced-motion: reduce) { .fx-scan { display: none; } }` present and scoped to
  `.fx-scan` only; all three overlays carry `pointer-events: none`.)

**Checkpoint**: All three user stories are independently functional.

---

## Phase 6: Polish & Cross-Cutting Concerns

- [X] T013 [P] Check off F001–F004 in `ROADMAP.md` now that Phase 0/Foundation Setup is complete.
- [X] T014 Diff `site/build.gradle.kts` dependencies against the T001 baseline and confirm zero
  new Gradle dependencies were added (constitution Principle VIII). (Verified 2026-09-17:
  `git diff site/build.gradle.kts` touches only the `kobweb.app.index` block and one import; the
  `jsMain.dependencies` block is untouched.)
- [X] T015 Run `quickstart.md` end-to-end (all 6 steps) as the final sign-off for this feature.
  (Verified 2026-09-17: steps 1/2/3/5/6 verified by direct inspection of the real
  `kobweb export -PkobwebExportLayout=STATIC` output — the same Playwright-rendered HTML/CSS a
  browser would load, which is a stronger check than eyeballing DevTools. Step 4's
  reduced-motion emulation wasn't run in an interactive browser in this environment, but the
  underlying CSS rule it would exercise was confirmed present and correctly scoped in step T012.)

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — start immediately.
- **Foundational (Phase 2)**: Empty — nothing blocks Phase 3.
- **User Story 1 (Phase 3)**: Depends on Phase 1 (T001 baseline) only. This is the MVP.
- **User Story 3 (Phase 4)**: T010 depends on US1's T008 (needs a real export to serve/check) —
  otherwise independent of US1's implementation choices.
- **User Story 2 (Phase 5)**: T011 depends on US1's T005 (the scanline overlay must exist before
  it can be conditionally hidden) — this is an inherent ordering, not a workaround.
- **Polish (Phase 6)**: Depends on all three stories being complete.

### Within Each User Story

- Token/config tasks before the style tasks that consume them.
- Style tasks before the shell/page tasks that render them.
- Implementation before the quickstart verification task that closes out the story.

### Parallel Opportunities

- T002 and T003 (different files: `SiteTheme.kt` vs. `build.gradle.kts`) can run in parallel.
- T013 (ROADMAP.md) can run in parallel with T014/T015 (both read-only checks against the code).

---

## Parallel Example: User Story 1

```bash
# T002 and T003 touch different files and have no dependency on each other:
Task: "Port the five mock tokens into SiteTheme.kt"
Task: "Add Google Fonts <link> elements in build.gradle.kts"
```

---

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1 (T001 baseline).
2. Complete Phase 3 (User Story 1 — T002–T008).
3. **STOP and VALIDATE**: run `quickstart.md` steps 1–3.
4. This alone delivers the on-brand visual foundation — the other two stories harden it
   (deploy correctness, accessibility) but US1 is what makes the site look right.

### Incremental Delivery

1. Setup → User Story 1 (MVP: the site looks correct).
2. Add User Story 3 (confirm it will actually work once published).
3. Add User Story 2 (respect reduced-motion).
4. Polish.

## Notes

- No test tasks: this project has no automated test suite; `quickstart.md` steps are the
  verification method for every story, per the constitution's static-export-based definition of
  done (Principle VII).
- T005 is the single point where the overlay `pointer-events: none` rule (FR-007) is established
  for all three overlays — do not duplicate it per-overlay or defer it to the US2 phase.
- Commit after each task or logical group, per the project's usual workflow.
