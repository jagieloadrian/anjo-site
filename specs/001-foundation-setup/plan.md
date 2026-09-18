# Implementation Plan: Foundation Setup

**Branch**: `001-foundation-setup` | **Date**: 2026-09-17 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/001-foundation-setup/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Get the Kobweb project (`site/`) to render on the mock's dark, on-brand visual foundation —
design tokens, typefaces, global reset, and decorative overlay chrome (scanline/vignette/grid),
respecting `prefers-reduced-motion` — with the site's base path correctly set to root (`/`) for a
GitHub user-page deploy (`jagieloadrian.github.io`), and demonstrated on a minimally updated
existing Index page, verified via a successful `kobweb export --layout static`.

## Technical Context

**Language/Version**: Kotlin 2.4.10 (Kotlin/JS target), Compose HTML 1.11.1, Compose runtime 1.12.0

**Primary Dependencies**: Kobweb 0.25.1 (`kobweb-core`, `kobweb-silk`), `kobwebx-markdown` — all
already declared in `site/build.gradle.kts`; no new dependency is needed for this phase (fonts and
head `<link>` elements are handled through Kobweb's existing `kobweb.app.index.head` DSL, colors/
typography through Silk's existing `CssStyle`/`SitePalette` mechanism)

**Storage**: N/A (static site, no persistence beyond the browser's own `localStorage` already used
by the template for color-mode preference)

**Testing**: No automated test suite exists in this project yet, and none is introduced by this
phase. Verification is the static export itself (FR-009/SC-005) plus manual visual/DevTools
checks documented in `quickstart.md` — consistent with the constitution's definition of done
(Principle VII: export success, not a passing test suite, is the gate).

**Target Platform**: Static HTML/CSS/JS served from GitHub Pages (root user page), evaluated in
evergreen desktop and mobile browsers

**Project Type**: Web frontend — Kobweb (Kotlin/JS) static-site-generated application, single
module (`:site`)

**Performance Goals**: No new performance target beyond spec's SC-002 (target typefaces visible on
first paint, zero layout shift once loaded) — no numeric threshold was set (deferred, low impact
per `/speckit-clarify`)

**Constraints**: Zero new Gradle dependencies (constitution Principle VIII); base path MUST be
root (`/`) per Clarifications in spec.md; decorative overlays MUST NOT block pointer events
(FR-007); scanline overlay MUST be suppressed under `prefers-reduced-motion: reduce` (FR-006)

**Scale/Scope**: One theme file, one global-styles file, one Gradle config touch point, one
existing page (Index) minimally updated — no new pages, routes, or components

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Check | Result |
|---|---|---|
| I. Static-First, Zero Backend | No server code touched; `includeServer` stays unset/false | PASS |
| II. Content as Data, Not Markup | N/A this phase — no project/trophy/skill content is introduced; design tokens are theme constants, not per-item content | PASS (N/A) |
| III. Bilingual Parity (EN/PL) | Index placeholder renders only technical labels (token/font names: `bg`, `ink`, `pink`, `cyan`, `red`, `Archivo`, `JetBrains Mono`), never narrative prose — exempt per Principle III's own carve-out for "technical labels, stack names, and tags". Confirmed post-implementation (`/speckit-analyze` finding D1): an earlier draft used a descriptive sentence, which was NOT exempt and was replaced. | PASS |
| IV. Single-Sourced Design Tokens | This phase's entire purpose is centralizing the five mock colors in `SiteTheme.kt` — no composable will hardcode a color | PASS (this phase delivers it) |
| V. Accessibility & Motion Discipline | FR-006/FR-007 directly implement the reduced-motion and non-blocking-overlay rules | PASS (this phase delivers it) |
| VI. No Secrets in the Client Bundle | No credentials involved in this phase | PASS (N/A) |
| VII. GitHub Pages Static Export as Sole Deployment Target | FR-009/SC-005 make static export success the verification gate, not the dev server | PASS |
| VIII. YAGNI on Dependencies | No new Gradle dependency added; fonts/head-links and reduced-motion CSS reuse Kobweb/Silk's existing DSL (see research.md) | PASS |

No violations — Complexity Tracking is not needed for this phase.

## Project Structure

### Documentation (this feature)

```text
specs/001-foundation-setup/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md         # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

No `contracts/` directory: this feature exposes no API, CLI, or other external interface — it is
a static site's internal styling/config layer, consumed only by browsers loading the exported
HTML/CSS. Per the plan template's own guidance, contracts are skipped for purely internal work.

### Source Code (repository root)

```text
site/                                              # Kobweb application module (:site)
├── build.gradle.kts                               # kobweb.app.index.head — add Google Fonts <link>s
├── .kobweb/conf.yaml                               # site.basePath — confirmed default "" == root, no change needed
└── src/jsMain/
    ├── kotlin/com/anjo/anjosite/
    │   ├── SiteTheme.kt                             # SitePalette(s): port the 5 mock tokens here
    │   ├── AppStyles.kt                              # @InitSilk global reset + fx-scan/fx-vignette/fx-grid + reduced-motion rule
    │   ├── AppEntry.kt                               # ColorMode/localStorage plumbing left as-is (research.md §4); gains overlay rendering (T006)
    │   ├── pages/Index.kt                            # minimal placeholder update to render on the new foundation
    │   └── components/layouts/PageLayout.kt          # DEVIATION (discovered during implementation, user-confirmed): remove decorative SvgCobweb demo — see tasks.md T007b
    └── resources/public/                             # static assets (favicon already present; no new assets this phase)
```

**Structure Decision**: Everything for this phase lives inside the existing `:site` module's
`jsMain` source set, editing files that already exist in the Kobweb template — no new modules,
packages, or directories are created.

## Complexity Tracking

*No violations — table intentionally omitted.*
