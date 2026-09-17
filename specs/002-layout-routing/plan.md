# Implementation Plan: Layout and Routing

**Branch**: `002-layout-routing` | **Date**: 2026-09-17 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/002-layout-routing/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Replace the handoff mock's hand-rolled `.hidden`-toggling router with Kobweb's native
file-based `@Page` routing, expand the nav to all six mock destinations (creating four minimal
placeholder pages so no link is ever dead), port the on-brand animated brand mark, introduce a
`CompositionLocal`-based language-state mechanism (browser-language-detected default, EN/PL,
demonstrated on existing placeholder content) to replace `data-lang-block`, add an on-brand
`404.html` via a `@Page("/404")` route, and drop the handoff mock's design-notes panel — all on
top of Phase 0's visual foundation, verified via static export.

## Technical Context

**Language/Version**: Kotlin 2.4.10 (Kotlin/JS target), Compose HTML 1.11.1, Compose runtime 1.12.0

**Primary Dependencies**: Kobweb 0.25.1 (`kobweb-core`, `kobweb-silk`), `kobwebx-markdown` — all
already declared in `site/build.gradle.kts`; no new dependency needed (routing is Kobweb's
built-in `@Page` mechanism, i18n uses `androidx.compose.runtime.CompositionLocal`/
`staticCompositionLocalOf` already transitively available via `compose.runtime`, browser-language
detection uses `kotlinx.browser.window` already transitively available via `kotlinx-browser`,
same package `AppEntry.kt` already imports `kotlinx.browser.document` from)

**Storage**: N/A (static site; language state is in-memory only this phase — no `localStorage`
persistence, per spec's Edge Cases/Assumptions deferring cross-page persistence to a later phase)

**Testing**: No automated test suite exists in this project (unchanged from Phase 0). Verification
is the static export itself (FR-... / SC-005) plus the manual checks in `quickstart.md`, consistent
with constitution Principle VII.

**Target Platform**: Static HTML/CSS/JS served from GitHub Pages (root user page), evaluated in
evergreen desktop and mobile browsers

**Project Type**: Web frontend — Kobweb (Kotlin/JS) static-site-generated application, single
module (`:site`)

**Performance Goals**: SC-003's "under 1 second" language-switch requirement is satisfied
trivially by a client-side Compose recomposition (no network round-trip) — no additional
performance work needed.

**Constraints**: Zero new Gradle dependencies (constitution Principle VIII); no hand-rolled
client-side router may remain (FR-001); glitch animation MUST be suppressed under
`prefers-reduced-motion: reduce` (FR-005, constitution Principle V); placeholder pages MUST NOT
introduce real content (FR-012, spec Assumptions — that's Phase 3's job)

**Scale/Scope**: One routing/nav component (`NavHeader.kt`), four new minimal placeholder page
files, one new i18n module (language `CompositionLocal` + a handful of bilingual demo strings),
one new 404 page file — no real page content, no data layer, no CI changes

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

| Principle | Check | Result |
|---|---|---|
| I. Static-First, Zero Backend | Browser-language detection uses `kotlinx.browser.window` (client-side JS API), no server call; `includeServer` stays unset | PASS |
| II. Content as Data, Not Markup | N/A this phase — placeholder pages carry no real content; the bilingual demo string(s) are the only "content" and are modeled as data (Bilingual String entity), not hardcoded per-language markup | PASS |
| III. Bilingual Parity (EN/PL) | This phase's entire purpose is replacing `data-lang-block` with a real shared-state mechanism (FR-007–FR-010). Post-implementation `/speckit-analyze` (finding D1) caught that nav labels and placeholder-page headings were left English-only, not covered by the technical-label exemption — fixed by routing them through `LocalLang`/`BilingualString` too. | PASS |
| IV. Single-Sourced Design Tokens | No new tokens introduced; placeholder pages and 404 page reuse Phase 0's `SiteTheme.kt`/`AppStyles.kt` exclusively | PASS |
| V. Accessibility & Motion Discipline | FR-005 requires the brand glitch animation to respect `prefers-reduced-motion: reduce`, reusing Phase 0's established CSS media-query pattern | PASS (this phase delivers it) |
| VI. No Secrets in the Client Bundle | No credentials involved in this phase | PASS (N/A) |
| VII. GitHub Pages Static Export as Sole Deployment Target | FR-002/FR-003/SC-005 make static export success (including `404.html` generation) the verification gate | PASS |
| VIII. YAGNI on Dependencies | No new Gradle dependency; i18n uses Compose's built-in `CompositionLocal`, language detection uses `kotlinx-browser`'s existing `window.navigator`, both already transitive deps | PASS |

No violations — Complexity Tracking is not needed for this phase.

## Project Structure

### Documentation (this feature)

```text
specs/002-layout-routing/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md         # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

No `contracts/` directory: this feature exposes no API, CLI, or other external interface — same
rationale as Phase 0 (specs/001-foundation-setup/plan.md).

### Source Code (repository root)

```text
site/
├── build.gradle.kts                                     # unchanged this phase
└── src/jsMain/
    ├── kotlin/com/anjo/anjosite/
    │   ├── Lang.kt                                       # NEW: Lang enum + LocalLang/LocalLangSetter CompositionLocals + browser-detection init + BilingualString
    │   ├── SiteTheme.kt                                  # unchanged this phase (Phase 0 territory)
    │   ├── AppStyles.kt                                  # unchanged this phase
    │   ├── AppEntry.kt                                   # provides the language CompositionLocal at the app-shell level
    │   ├── components/sections/NavHeader.kt              # brand mark + glitch style, all 6 nav links, language switch control
    │   ├── components/layouts/PageLayout.kt              # unchanged this phase (already renders NavHeader/Footer)
    │   └── pages/
    │       ├── Index.kt                                  # gains the bilingual demo string proving the i18n mechanism (FR-009)
    │       ├── Projects.kt                                # NEW: minimal placeholder page (FR-012)
    │       ├── Trophies.kt                                # NEW: minimal placeholder page (FR-012)
    │       ├── Contact.kt                                 # NEW: minimal placeholder page (FR-012)
    │       ├── Cv.kt                                      # NEW: minimal placeholder page (FR-012) — file `Cv.kt` → route `/cv`
    │       └── Error404.kt                                # NEW: @Page("/404") — exports to 404.html (FR-003/FR-004)
    └── resources/public/                                  # unchanged this phase
```

**Structure Decision**: Everything lives inside the existing `:site` module's `jsMain` source
set. One new file (`Lang.kt`) holds the i18n mechanism; five new page files are minimal
placeholders following Phase 0's Index-placeholder pattern; `NavHeader.kt` is the only existing
component with substantial changes (brand mark, six links, language switch). No new modules,
packages, or Gradle dependencies.

## Complexity Tracking

*No violations — table intentionally omitted.*
