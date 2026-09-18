# Tasks: Polish + Automated Test Suite

**Input**: Design documents from `/specs/007-tests-polishing/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, quickstart.md

**Tests**: Explicitly requested by the feature itself — User Story 3 *is* the automated test
suite (unit + browser/a11y). Test-file tasks are listed inline within the story that needs them,
not as a separate optional section.

**Organization**: Tasks are grouped by user story (spec.md P1/P1/P2/P3). Two stories share
Priority P1 (US1 SEO/OG and US2 Accessibility) — both are MVP-tier; either can be done first.

## Phase 1: Setup (Shared Infrastructure)

- [X] T001 [P] Create `e2e/package.json` (`"private": true`, devDependencies
  `@playwright/test` and `@axe-core/playwright`) per plan.md's Project Structure.
- [X] T002 Run `npm install` in `e2e/` to generate `e2e/package-lock.json`. Depends on T001.
- [X] T003 [P] Add a `jsTest` source set to `site/build.gradle.kts`'s `kotlin { sourceSets { } }`
  block with `implementation(kotlin("test"))` (research.md §1) — no new version-catalog entry.

---

## Phase 2: Foundational (Blocking Prerequisites for US2 and US3)

**Purpose**: The shared Playwright/axe tooling both the accessibility story (US2) and the
regression-suite story (US3) run on top of. (US1's SEO/OG work and US4's font work don't depend
on this — they can proceed in parallel with this phase.)

- [X] T004 Create `e2e/playwright.config.ts`: a single Chromium-only project (spec Assumption),
  `webServer.command = "python3 -m http.server 4173 --directory ../site/.kobweb/site"`,
  `webServer.url = "http://127.0.0.1:4173"`, `use.baseURL` set to the same URL (research.md §5).
  Depends on T002. Command later changed to `python3 serve-static.py 4173 ../site/.kobweb/site` —
  see `e2e/serve-static.py` and research.md §5's amended entry: a bare `http.server` has no
  GitHub-Pages-style fallback, so T022's unknown-slug test (a client-side-only route) would 404 at
  the HTTP layer before the app ever booted.
- [X] T005 Run `npx playwright install --with-deps chromium` in `e2e/` to fetch the browser
  binary once, locally and as a CI prerequisite check. Depends on T004. Sandbox has no root, so
  `--with-deps` (needs `sudo`) failed; `npx playwright install chromium` (binary only, no OS libs)
  succeeded and the browser launches fine here — `ubuntu-latest` in CI has real root, so
  `--with-deps` is kept as the recorded command for T027's CI job.

**Checkpoint**: `npx playwright test` in `e2e/` can now load and run spec files (even with none
yet written) against a served static export.

---

## Phase 3: User Story 1 - Shared links and search results show the right page (Priority: P1) 🎯 MVP

**Goal**: Every page exposes a page-specific `<title>`, meta description, and Open Graph tags
(including `og:image`).

**Independent Test**: quickstart.md step 3 — `grep` the exported HTML of at least two different
pages and confirm distinct, correct values. No test tooling dependency.

### Implementation for User Story 1

- [X] T006 [P] [US1] In `site/src/jsMain/kotlin/com/anjo/anjosite/components/layouts/PageLayout.kt`,
  add a small shared `updatePageMeta(title: String, description: String, ogImage: String)`
  function that sets `document.title` and creates/updates `<meta name="description">`,
  `<meta property="og:title">`, `<meta property="og:description">`,
  `<meta property="og:type" content="website">`, and `<meta property="og:image">` on
  `document.head` (data-model.md `<head>` output contract; research.md §4 confirms
  `LaunchedEffect`-driven `<head>` mutations survive Kobweb's static export).
- [X] T007 [US1] Extend `PageLayoutData` in the same file with `description: BilingualString` and
  `ogImage: String = "/og-banner.png"`; change `PageLayout.kt`'s existing `LaunchedEffect` to
  call `updatePageMeta(data.title, data.description(LocalLang.current), data.ogImage)` instead of
  setting `document.title` directly. Depends on T006.
- [X] T008 [P] [MANUAL] [US1] Hand-author (any image editor/design tool — no new
  Gradle/npm dependency, research.md §8) `site/src/jsMain/resources/public/og-banner.png`
  (1200×630, the OG-recommended aspect ratio) using `SiteTokenStyles.kt`'s existing palette
  (`--bg`/`--ink`/`--pink`/`--cyan`) and the "Adrian Jagieło" wordmark already used in
  `PageLayout.kt`, then commit it as a plain static asset. Rendered ad hoc via PIL (pre-existing
  system tool, not a project dependency) using a system font (DejaVu Sans) rather than the
  vendored Archivo woff2 — Archivo's `latin`-only TTF conversion tofu'd the "ł" glyph and merging
  the `latin`+`latin-ext` variable-font subsets isn't supported by available tooling; the shipped
  site itself still renders "Adrian Jagieło" with the real self-hosted Archivo (T029), only this
  one static banner image took the pragmatic substitute.
- [X] T009 [US1] Update the `PageLayoutData(...)` construction in every static page —
  `pages/Index.kt`, `pages/About.kt`, `pages/Projects.kt`, `pages/Trophies.kt`,
  `pages/Contact.kt`, `pages/Cv.kt`, `pages/Error404.kt` — to pass a page-specific
  `description: BilingualString` derived from that page's own existing content (FR-001). Depends
  on T007.
- [X] T010 [US1] In `site/src/jsMain/kotlin/com/anjo/anjosite/pages/projects/Slug.kt`'s
  `SlugPage()`, once `project` resolves to non-null, call `updatePageMeta(...)` (T006) with
  `project.title(lang)`, `project.shortDescription(lang)`, and `project.coverImageUrl ?:
  "/og-banner.png"` inside a `LaunchedEffect(project)` — the one route where per-page metadata
  can't be known at static `@InitRoute` time and must wait for the runtime fetch (FR-002).
  Depends on T006.

**Checkpoint**: User Story 1 fully functional and independently verifiable via quickstart.md
step 3.

---

## Phase 4: User Story 2 - Site is usable with assistive tech and reduced motion (Priority: P1) 🎯 MVP

**Goal**: Every assembled page passes an automated `wcag2a`+`wcag2aa` `axe-core` scan, in both
light and dark mode.

**Independent Test**: `npx playwright test a11y.spec.ts` in `e2e/` passes with zero violations
(quickstart.md step 2).

### Implementation for User Story 2

- [X] T011 [P] [US2] Review every image/icon usage across `pages/*.kt` and
  `components/widgets/*.kt` for alt text that is *present but inaccurate* (axe's `image-alt` rule
  only catches *missing* alt text, not wrong text) and fix any found (FR-003). Audited: the only
  `Img(...)` call site is `components/widgets/GameCover.kt`, `alt` sourced from
  `trophies.json`'s per-game `"alt"` field (e.g. `"Call of Duty®: Black Ops cover"`) — accurate,
  not generic. No code change needed.
- [X] T012 [P] [US2] Audit `prefers-reduced-motion` coverage beyond the Terminal/glitch-hover
  cases already gated in Phase 2/3 — specifically `Theme.kt`'s light/dark toggle (added ad hoc in
  Phase 4) and any other animation added since — and gate anything still un-gated (FR-004).
  Audited: only two `animation` declarations exist site-wide (`.glitch:hover` / `glitchShift` and
  `.caret` / `caretBlink`, both in `SiteGlitchStyles.kt`), and both are already set to `none` under
  `SiteOverlayStyles.kt`'s `@media (prefers-reduced-motion: reduce)` block. `Theme.kt`'s
  light/dark toggle declares no `transition`/`animation` property. No code change needed.
- [X] T013 [US2] Contrast pass: check every text/background combination in both light and dark
  mode against `SiteTokenStyles.kt`'s existing tokens for WCAG AA (4.5:1 normal, 3:1 large); if a
  real violation is found, adjust only the existing token *values*, never introduce a new one-off
  color (FR-005). Computed contrast ratios for every text-color token against `--bg` in both
  themes; three normal-text (10-12px) violations found and fixed by adjusting the existing token
  values only: dark `--faint` `#6f6e6d`→`#7c7c7a` (3.95:1→4.81:1), light `--faint` `#837f7c`→
  `#6b6967` (3.55:1→4.89:1), light `--red` `#ec3013`→`#c9280f` (3.76:1→4.95:1, dark `--red`
  already passed and is unchanged). All other tokens already met 4.5:1 or are only used at
  large-text size / for non-text UI (borders/backgrounds, 3:1 threshold).
- [X] T014 [US2] Create `e2e/tests/a11y.spec.ts`: for all 8 routes (7 static + one sample project
  detail page), in both light and dark color mode, run `@axe-core/playwright`'s
  `AxeBuilder({ page }).withTags(["wcag2a", "wcag2aa"])` and assert zero violations (FR-003,
  FR-005, FR-017, research.md §6). Depends on T005 (Foundational), T011, T012, T013. Theme pinned
  via `localStorage["aj-theme"]` (an `addInitScript`) rather than clicking `.theme-btn`, after an
  early version proved Chromium's default `prefers-color-scheme` (light) leaked into the "dark"
  runs. Running against the real export surfaced two genuine bugs beyond the T013 audit, both
  fixed: `Cv.kt`'s three skills/languages/interests blocks hardcoded `color: #e6e4e3` (the *dark*
  theme's `--ink-2`) inline instead of `var(--ink-2)`, so they never re-themed and failed contrast
  in light mode — fixed to `var(--ink-2)`; light `--pink` (`#d6006e`, 4.60:1 against flat `--bg`)
  dropped under 4.5:1 against several pink-tinted panel backgrounds it's actually rendered on
  (`.term-bar`, `.term-input span`) — bumped to `#c20064` (>=4.51:1 against every observed
  background, most with more margin).

**Checkpoint**: User Stories 1 AND 2 both independently verifiable.

---

## Phase 5: User Story 3 - Regressions are caught automatically, before merge (Priority: P2)

**Goal**: Unit tests (pure logic) and a Playwright browser suite (real static export) both run
automatically in CI on every push/PR.

**Independent Test**: Break one thing deliberately on a branch (spec's Independent Test); confirm
CI fails on that specific change with no manual step.

### Kotlin unit tests

- [X] T015 [P] [US3] Change `parseTrophiesData`'s visibility from `private` to `internal` in
  `site/src/jsMain/kotlin/com/anjo/anjosite/pages/Trophies.kt` — no behavior change (research.md
  §2, FR-009).
- [X] T016 [P] [US3] Extract `internal fun langForLocale(locale: String): Lang` from
  `detectInitialLang()`'s body in `site/src/jsMain/kotlin/com/anjo/anjosite/Lang.kt` — a pure,
  parameterized version of the `pl`-prefix rule, needed because Kotlin/JS browser-target tests run
  in a real Karma-launched browser, so `window.navigator.language` reflects the test runner's
  actual locale, not a value a test can control (FR-008).
- [X] T017 [P] [US3] Extract `internal fun findProjectBySlug(entries: List<ProjectEntry>, slug:
  String?): ProjectEntry?` in
  `site/src/jsMain/kotlin/com/anjo/anjosite/pages/projects/Slug.kt`, and call it from
  `SlugPage()` in place of the inline `.find { it.slug == slug }` (research.md §3, FR-010).
- [X] T018 [P] [US3] Create `site/src/jsTest/kotlin/com/anjo/anjosite/LangTest.kt`: `kotlin.test`
  cases for `langForLocale` (T016) — `"pl-PL"`→PL, `"pl"`→PL, `"en-US"`→EN, `"PL-pl"`
  (case-insensitive)→PL — plus `BilingualString`/`BilingualTimelineItem.resolve()` against known
  EN/PL inputs (FR-008, data-model.md). Depends on T016.
- [X] T019 [P] [US3] Create `site/src/jsTest/kotlin/com/anjo/anjosite/pages/TrophiesTest.kt`:
  `kotlin.test` cases for `parseTrophiesData` (T015) against a known JSON string fixture,
  asserting the parsed `stats`/`games`/`trophies` match expectations (FR-009). Depends on T015.
- [X] T020 [P] [US3] Create `site/src/jsTest/kotlin/com/anjo/anjosite/pages/ProjectsTest.kt`:
  `kotlin.test` cases for the existing public `parseProjectEntries` against a known JSON string
  fixture (FR-009).
- [X] T021 [P] [US3] Create
  `site/src/jsTest/kotlin/com/anjo/anjosite/pages/projects/SlugTest.kt`: `kotlin.test` cases for
  `findProjectBySlug` (T017) — known slug found, unknown slug → `null`, empty list → `null`
  (FR-010). Depends on T017.

### Playwright browser suite

- [X] T022 [P] [US3] Create `e2e/tests/routes.spec.ts`: visit all 7 static routes + `/404`, assert
  no `page.on("pageerror")` and no `console.error` (FR-011, research.md §6/§7); visit one dynamic
  `/projects/{slug}` page and assert it renders that project's title; visit an unknown slug and
  assert it redirects to `/404` (FR-012). Depends on T005 (Foundational). Filters one known,
  unfixable framework-noise message (`anjosite.js`'s built-in live-reload widget hitting
  `/api/kobweb-status`, which 404s on any static host including GitHub Pages — not an application
  regression). Requesting an unregistered `/projects/{slug}` needed a real GitHub-Pages-style
  static-file fallback to 404.html; see T005/`e2e/serve-static.py` below.
- [X] T023 [P] [US3] Create `e2e/tests/lang-toggle.spec.ts`: click the language switch, assert
  visible nav/heading text changes between EN and PL (FR-013). Depends on T005.
- [X] T024 [P] [US3] Create `e2e/tests/reduced-motion.spec.ts`: launch the browser context with
  `reducedMotion: "reduce"`, assert no decorative animation (scanline/glitch/typing/cursor-blink)
  class or animation style is active on any page (FR-014). Depends on T005. Found and fixed a real
  bug: `SiteStyles.kt` concatenated `SiteOverlayStyles.cssRules` (the reduced-motion override)
  *before* `SiteGlitchStyles.cssRules` (the unconditional `.glitch:hover`/`.caret` animations) —
  equal selector specificity, so the later, unconditional rule always won the cascade regardless
  of the media query. Moved `SiteOverlayStyles.cssRules` to the end of the list.
- [X] T025 [P] [US3] Create `e2e/tests/breakpoints.spec.ts`: at 390px, 430px, and 768px viewport
  widths, assert `document.documentElement.scrollWidth <= clientWidth` (no horizontal overflow)
  on every page, and every interactive element's bounding box is at least 48px on its smaller
  dimension (FR-015 — replaces the manual `docs/handoff/mobile-check.html` review, ROADMAP
  F024a). Depends on T005. Found and fixed two real touch-target gaps in `SiteNavStyles.kt` at
  390px: `.nav-btn` had no `min-width`, so the short "CV" label measured 47.84px wide; `.lang-btn`
  ("EN"/"PL") measured only 34px tall. Added `min-width: 48px` to `.nav-btn` and `min-width`/
  `min-height: 48px` (+ centering) to `.lang-btn`.

### CI wiring

- [X] T026 [US3] Extend the `site-export` job in `.github/workflows/ci.yml` with a
  `./gradlew :site:jsTest` step (FR-016, research.md §9). Depends on T003, T018, T019, T020,
  T021.
- [X] T027 [US3] Add a new `e2e` job to `.github/workflows/ci.yml`: build the static export
  (`kobwebExport -PkobwebExportLayout=STATIC`), then in `e2e/` run `npm ci`, `npx playwright
  install --with-deps chromium`, and `npx playwright test` (FR-016, research.md §9). Depends on
  T014, T022, T023, T024, T025. Implemented via the artifact upload/download pattern
  (`site-export` uploads `site/.kobweb/site` via `actions/upload-artifact`, `e2e` downloads it via
  `actions/download-artifact` and `needs: site-export`) — the second option research.md §9 left
  open, chosen over re-running `kobwebExport` a second time in the `e2e` job.

**Checkpoint**: All user stories 1-3 independently functional; CI shows three green jobs
(`refresh-trophies-tests`, `site-export` with `jsTest`, `e2e`).

---

## Phase 6: User Story 4 - Maintainer makes an informed call on the two optional items (Priority: P3)

**Goal**: Both optional ROADMAP items get a recorded, actioned decision (research.md §10:
fonts now, analytics deferred).

**Independent Test**: Read the recorded decision for each item; confirm neither is left open
(spec's Independent Test).

### Implementation for User Story 4

- [X] T028 [P] [US4] Download the exact font weights already loaded from Google Fonts — Archivo
  400/500/600/700/800/900, JetBrains Mono 400/500/700 — as `.woff2` files into
  `site/src/jsMain/resources/public/fonts/` (research.md §10, FR-007). Both families are variable
  fonts server-side (Google returns byte-identical files per declared weight within a subset), so
  only 4 unique files are vendored: `archivo-latin.woff2`, `archivo-latin-ext.woff2`,
  `jetbrains-mono-latin.woff2`, `jetbrains-mono-latin-ext.woff2` — `latin` **and** `latin-ext`
  per family (not just `latin`) since Polish diacritics live in the latin-ext Unicode block
  (Constitution Principle III).
- [X] T029 [US4] Add `@font-face` rules (pointing at the T028 files) as a raw `<style>` tag in
  `site/build.gradle.kts`'s `head.add` block, not `SiteTokenStyles.kt` (deviation from plan.md's
  original file target — Compose HTML's typed `StyleSheet` DSL has no `@font-face` support; the
  existing `--sans`/`--mono` custom-property *names* in `SiteTokenStyles.kt` are unchanged, only
  where the underlying font files are declared/served from). Depends on T028.
- [X] T030 [US4] Remove the `fonts.googleapis.com`/`fonts.gstatic.com` `<link rel="preconnect">`
  and `<link rel="stylesheet" href="https://fonts.googleapis.com/...">` entries from
  `site/build.gradle.kts`'s `head.add` block. Depends on T029.

**Checkpoint**: User Story 4 done — quickstart.md step 4 (no Google Fonts network request) and
the analytics deferral (research.md §10, no code) both hold.

---

## Phase 7: Polish & Cross-Cutting Concerns

- [X] T031 Run `./gradlew :site:kobwebExport -PkobwebExportLayout=STATIC` and walk through
  quickstart.md steps 1-6 end-to-end as a final sanity pass, after all stories above are done —
  step 6 (new, `/speckit-analyze` G1) spot-checks SC-005's 10-minute CI budget from a real
  Actions run. Steps 1-4 verified locally: `:site:jsTest` green, full `npx playwright test` green
  (48/48), SEO/OG grep spot-check correct on `trophies.html` and the project detail page, no
  `fonts.googleapis.com`/`fonts.gstatic.com` reference remains in `build.gradle.kts`. Steps 5-6
  need a real pull request/Actions run and can't be executed from this local session — left for
  the maintainer to confirm on the first PR against this branch.
- [X] T032 Update ROADMAP.md's Faza 6 section: check off F030/F031/F034/F035 `[x]`; check off
  F033 `[x]` with a one-line "self-hosted, no CDN request" note; leave F032 unchecked with a
  one-line "deferred — see specs/007-tests-polishing/research.md §10" note. Depends on T031.
- [X] T033 [P] Add a "Faza 007 — Polish + Automated Test Suite" section to `specs/SUMMARY.md`
  following the existing format (Cel / Kluczowe decyzje techniczne / Zaimplementowane pliki /
  Gotchas), once T031 confirms everything works. Depends on T031.

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies — can start immediately.
- **Foundational (Phase 2)**: Depends on T002 (Setup) — blocks US2 (Phase 4) and US3 (Phase 5)
  only. US1 (Phase 3) and US4 (Phase 6) do not depend on it and can proceed in parallel.
- **US1 (Phase 3)**: Depends only on Setup being present (no Foundational dependency).
- **US2 (Phase 4)**: Depends on Foundational (Phase 2).
- **US3 (Phase 5)**: Depends on Foundational (Phase 2) and Setup's `jsTest` source set (T003).
  Its `a11y.spec.ts` coverage overlaps with US2 but its own specs (routes/lang/motion/
  breakpoints) are independent of US2's completion.
- **US4 (Phase 6)**: Depends only on Setup being present.
- **Polish (Phase 7)**: Depends on all four stories being complete.

### Parallel Opportunities

- T001 and T003 (Setup) can run in parallel.
- Once Foundational (T004-T005) is done, US1, US2, US3, and US4 can all be worked in parallel —
  they touch disjoint files except where noted above.
- Within US3, all five Kotlin extraction/visibility tasks (T015-T017) are `[P]` (different
  files), all four unit-test files (T018-T021) are `[P]` once their respective source task is
  done, and all four Playwright spec files (T022-T025) are `[P]` (different files, same
  Foundational dependency).

---

## Parallel Example: User Story 3

```bash
# Once T015-T017 land, all four Kotlin test files can be written together:
Task: "Create site/src/jsTest/kotlin/com/anjo/anjosite/LangTest.kt"
Task: "Create site/src/jsTest/kotlin/com/anjo/anjosite/pages/TrophiesTest.kt"
Task: "Create site/src/jsTest/kotlin/com/anjo/anjosite/pages/ProjectsTest.kt"
Task: "Create site/src/jsTest/kotlin/com/anjo/anjosite/pages/projects/SlugTest.kt"

# Once Foundational (T004-T005) is done, all four Playwright spec files can be written together:
Task: "Create e2e/tests/routes.spec.ts"
Task: "Create e2e/tests/lang-toggle.spec.ts"
Task: "Create e2e/tests/reduced-motion.spec.ts"
Task: "Create e2e/tests/breakpoints.spec.ts"
```

---

## Implementation Strategy

### MVP First (User Stories 1 + 2 — both P1)

1. Complete Phase 1: Setup.
2. Complete Phase 3 (US1) — deliverable/verifiable on its own, no Foundational dependency.
3. Complete Phase 2: Foundational, then Phase 4 (US2) — deliverable/verifiable on its own.
4. **STOP and VALIDATE**: both P1 stories pass their independent tests.

### Incremental Delivery

1. Setup → US1 (SEO/OG) → validate → commit.
2. Foundational → US2 (a11y) → validate → commit.
3. US3 (regression suite, reuses Foundational) → validate (CI green) → commit.
4. US4 (fonts now, analytics deferred) → validate → commit.
5. Polish (final `kobwebExport` sanity pass + ROADMAP.md/SUMMARY.md updates) → commit.

---

## Notes

- `[P]` tasks touch different files with no dependency on an incomplete task in the same phase.
- `[Story]` label maps every user-story-phase task to its spec.md story for traceability.
- Commit after each task or logical group, per this repo's existing per-phase convention
  (`specs/006-trophies-data/tasks.md`).
- Two stories share Priority P1 (US1, US2) — either can be the very first slice delivered; they
  don't depend on each other.
