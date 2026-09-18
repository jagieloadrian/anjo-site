# Implementation Plan: Polish + Automated Test Suite

**Branch**: `feature-7/tests-polishing` | **Date**: 2026-09-18 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/007-tests-polishing/spec.md`

## Summary

Close out ROADMAP.md Faza 6 (F030-F035): give every page its own SEO title/description and
Open Graph tags (including `og:image`) by extending the existing `PageLayoutData`/`PageLayout.kt`
mechanism that already proves out per-page `document.title` mutations survive Kobweb's real
static export (verified empirically — see research.md §4); close the accessibility gap with an
automated `axe-core` scan (`wcag2a`+`wcag2aa` only) wired into a new Playwright browser suite;
add the project's first `kotlin.test` unit tests for pure logic (`Lang.kt`, `Trophies.kt`'s
parser, and a newly-extracted slug-lookup helper); and make the two optional ROADMAP items a
real decision instead of open questions — self-hosted fonts implemented now (small, zero-new-
dependency, closes a real third-party-request privacy gap), analytics deferred (no stated need,
adds a third-party script and an external account dependency for a personal site with no traffic
goal). All new test commands (unit, browser/a11y) are wired into the existing `ci.yml`.

## Technical Context

**Language/Version**: Kotlin 2.4.10 (Kotlin/JS), Compose HTML 1.11.1, Kobweb 0.25.1 — unchanged
for the site itself. New: a `jsTest` Kotlin source set using `kotlin.test` (ships with the
Kotlin Multiplatform Gradle plugin already applied — no new version-catalog entry, per spec
Assumptions). New: Node.js 20+ (matching `scripts/refresh-trophies`'s existing runtime) for a new
`e2e/` Playwright workspace, isolated from that script's own `package.json`.

**Primary Dependencies**: `@playwright/test` and `@axe-core/playwright` (both new npm
devDependencies, scoped to `e2e/`) — justified under Constitution Principle VIII because no
stdlib/already-installed tool can drive a real browser or scan rendered accessibility violations
(spec Assumptions). No new Gradle/Kotlin dependency beyond `kotlin("test")`, which is bundled.

**Storage**: N/A — no backend, no database. One new static asset (an `og:image` fallback banner)
and the self-hosted font files (FR-007's decision is implement-now — Summary, research.md §10)
are the only new files under
`site/src/jsMain/resources/public/`.

**Testing**: This phase *is* the test infrastructure. Three independent layers, all wired into
CI (FR-016):
1. `scripts/refresh-trophies/index.test.mjs` — existing `node:test`, untouched.
2. New `site/src/jsTest/kotlin/...` — `kotlin.test`, covering `Lang.kt`'s `detectInitialLang()`/
   `BilingualString`/`BilingualTimelineItem.resolve()` (FR-008), `Trophies.kt`'s
   `parseTrophiesData` (FR-009, requires a `private` → `internal` visibility change — research.md
   §2), and a newly-extracted `findProjectBySlug()` helper pulled out of `Slug.kt`'s composable
   body (FR-010, research.md §3).
3. New `e2e/` — Playwright + `@axe-core/playwright`, Chromium only (spec Assumption), run against
   a real `kobwebExport --layout static` output served by `python3 -m http.server` (zero new
   dependency, always present on `ubuntu-latest` — research.md §5), covering FR-011 through
   FR-017.

**Target Platform**: GitHub Actions `ubuntu-latest` (existing `ci.yml`, extended — research.md
§9) and the maintainer's local machine. Site deployment target is unchanged: GitHub Pages static
export at `jagieloadrian.github.io` (Constitution Principle VII).

**Project Type**: Kobweb (Kotlin/JS) web frontend, same project shape as every prior phase, plus
one new isolated Node test workspace (`e2e/`) — no new application module.

**Performance Goals**: SC-005 — the full automated suite (unit + browser) completes in CI in
under 10 minutes (spec Assumption: an arbitrary but reasonable default for this site's size).

**Constraints**: `axe-core` scans are restricted to `wcag2a`+`wcag2aa` tags only (clarify
session). The route/console check (FR-011) fails only on an unhandled JS exception or a
`console.error` entry — `console.warn` is ignored (clarify session). No new one-off colors may be
introduced to satisfy the contrast check (FR-005) — only existing `SiteTokenStyles.kt`
`--pink`/`--cyan`/`--red`/`--bg`/`--ink` tokens. Self-hosted font files (if implemented) must be
the same weights already loaded from Google Fonts (Archivo 400/500/600/700/800/900, JetBrains
Mono 400/500/700) — no font-selection change, purely a hosting-location change.

**Scale/Scope**: Touches `PageLayout.kt`/`PageLayoutData` (extended for per-page
description/OG), `Trophies.kt` (one visibility change), `pages/projects/Slug.kt` (one pure
function extracted), `site/build.gradle.kts` (new `jsTest` source set deps; font `<link>` block
replaced with self-hosted `@font-face`, per FR-007's implement-now decision), one new static asset
(`og-banner`), a new `e2e/` npm workspace (~5 spec files, one per FR-011…FR-017 concern), and
`.github/workflows/ci.yml` (two new steps/jobs). No new page, no new route, no change to any
existing page's visible content beyond what accessibility/contrast fixes require.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

`.specify/memory/constitution.md` v1.0.0.

| Principle | Check | Result |
|---|---|---|
| I. Static-First, Zero Backend | Playwright's local static file server (`python3 -m http.server`) is test-only tooling, never shipped or deployed. If FR-006 analytics were implemented it would have to be a cookieless script-tag service with no repo-hosted backend — moot here since the decision is defer (Summary) | PASS |
| II. Content as Data, Not Markup | Per-page SEO/OG content (FR-001/FR-002) is derived from each page's own already-defined data (existing `BilingualString` copy, `projects.json`'s `coverImageUrl`) through one shared mechanism (`PageLayoutData`), not new hardcoded markup duplicated per page | PASS |
| III. Bilingual Parity (EN/PL) | Per-page meta descriptions (FR-001) reuse the existing `BilingualString`/`LocalLang` pattern, same as all other user-facing copy — not a new, unlocalized text path | PASS |
| IV. Single-Sourced Design Tokens | Contrast fixes (FR-005) may only use existing `SiteTokenStyles.kt` tokens (Technical Context, Constraints) — no new inline colors. Self-hosted fonts (FR-007) change only *where* the `--sans`/`--mono` font files are served from, not the token names or values | PASS |
| V. Accessibility & Motion Discipline | This phase's entire User Story 2/3 is enforcing this principle end-to-end for the first time via automated `axe-core` + Playwright, on top of Phase 3's component-level baseline | PASS |
| VI. No Secrets in the Client Bundle | No credential is introduced by this phase — self-hosted fonts are public static files; analytics (which could have needed a site key) is deferred | PASS |
| VII. GitHub Pages Static Export as Sole Deployment Target | The entire Playwright suite runs against a real `kobwebExport --layout static` output (never the dev server) — this phase is the first to make that check automatic on every push instead of an unenforced manual step (ROADMAP F024a) | PASS |
| VIII. YAGNI on Dependencies | Two new npm devDependencies (`@playwright/test`, `@axe-core/playwright`) are the minimum needed since no stdlib/already-installed tool drives a real browser or scans rendered a11y violations (spec Assumptions); the static file server deliberately adds zero further dependencies (`python3 -m http.server`, research.md §5); analytics is deferred specifically because it fails this principle's "no new dependency unless genuinely can't do it another way" test — there is no functional gap without it | PASS |

No violations — Complexity Tracking is not needed for this phase.

## Project Structure

### Documentation (this feature)

```text
specs/007-tests-polishing/
├── plan.md               # This file (/speckit-plan command output)
├── research.md           # Phase 0 output (/speckit-plan command)
├── data-model.md         # Phase 1 output (/speckit-plan command)
├── quickstart.md         # Phase 1 output (/speckit-plan command)
└── tasks.md              # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

No `contracts/` directory: same rationale as every prior phase (`specs/SUMMARY.md`;
`specs/005-ci-deploy/plan.md`; `specs/006-trophies-data/plan.md`) — this project has no external
API/interface of its own. The closest thing, the OG/meta tag shape, is documented in
`data-model.md` instead, since it's page-embedded output, not a callable interface.

### Source Code (repository root)

```text
site/
├── build.gradle.kts                              # MODIFIED: jsTest source set + kotlin.test dep;
│                                                  #   font <link>/preconnect block replaced with
│                                                  #   local @font-face refs (FR-007: implement now)
├── src/jsMain/kotlin/com/anjo/anjosite/
│   ├── components/layouts/PageLayout.kt          # MODIFIED: PageLayoutData gains description/
│   │                                              #   ogImage fields; LaunchedEffect also writes
│   │                                              #   <meta> OG tags (research.md §4)
│   ├── pages/Trophies.kt                         # MODIFIED: parseTrophiesData private → internal
│   ├── pages/projects/Slug.kt                    # MODIFIED: slug lookup extracted to a pure,
│   │                                              #   internal findProjectBySlug() function
│   └── pages/*.kt                                # MODIFIED: each page supplies its PageLayoutData
│                                                  #   description/ogImage (FR-001/FR-002)
├── src/jsMain/resources/public/
│   ├── og-banner.png                             # NEW: site-wide og:image fallback (FR-002)
│   └── fonts/                                    # NEW: vendored .woff2 (FR-007: implement now)
└── src/jsTest/kotlin/com/anjo/anjosite/
    ├── LangTest.kt                               # NEW: FR-008
    └── pages/
        ├── TrophiesTest.kt                       # NEW: FR-009
        ├── ProjectsTest.kt                       # NEW: FR-009
        └── projects/SlugTest.kt                  # NEW: FR-010

e2e/                                               # NEW: isolated Playwright workspace
├── package.json                                  #   @playwright/test, @axe-core/playwright
├── playwright.config.ts                          #   Chromium only; webServer =
│                                                  #   `python3 -m http.server` over the exported
│                                                  #   site/.kobweb/site directory
└── tests/
    ├── routes.spec.ts                             # FR-011, FR-012
    ├── lang-toggle.spec.ts                        # FR-013
    ├── reduced-motion.spec.ts                      # FR-014
    ├── breakpoints.spec.ts                         # FR-015
    └── a11y.spec.ts                                # FR-017

.github/workflows/ci.yml                          # MODIFIED: new `unit-tests` step in/near the
                                                    #   existing site-export job (Kotlin jsTest)
                                                    #   and a new `e2e` job that exports, serves,
                                                    #   and runs Playwright (FR-016)
```

**Structure Decision**: Kotlin unit tests live in the standard Kotlin Multiplatform `jsTest`
source set, mirroring `jsMain`'s package layout — the idiomatic, zero-new-tooling place for them
(same "reuse what's already there" reasoning as reusing `node:test`'s existing pattern for
`refresh-trophies`). The Playwright suite gets its own top-level `e2e/` workspace, isolated from
`scripts/refresh-trophies/` the same way that script is isolated from `site/` — a different
runtime concern (browser automation vs. a PSN fetch script) with its own dependencies, not
sharing a `package.json` with either. `PageLayout.kt` is the single point of extension for
FR-001/FR-002 because it already runs, once per page, in the exact `LaunchedEffect` pattern
proven (research.md §4) to survive Kobweb's static-export DOM snapshot — no second, parallel
mechanism is introduced.

## Complexity Tracking

*No violations — table intentionally omitted.*
