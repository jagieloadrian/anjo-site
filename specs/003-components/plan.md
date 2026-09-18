# Implementation Plan: Shared UI Components

**Branch**: `003-components` | **Date**: 2026-09-17 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/003-components/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Build the seven shared Silk components Phase 3 will assemble pages from: `Terminal` (character-
by-character boot typing, full reduced-motion bypass with live preference reactivity),
`ProjectCard`/`GameCover`/`TrophyRow`/`Tag` (each a self-contained clickable container taking its
own navigation target — per clarification, none of them is wrapped by the consuming page),
`TimelineEntry`/`StatRow` (static display only). Every component takes its content through a
parameter/data class (never hardcoded), sources colors/fonts/spacing exclusively from
`SiteTheme.kt`, scales type via `clamp()`, keeps every interactive element at a 48px touch target
under the mock's 720px breakpoint, and ships a full accessibility baseline now (required `alt`
text, ARIA roles/labels) rather than deferring it to the Phase 6 audit. No page, routing, or JSON
data-source work — those stay out of scope per the spec.

## Technical Context

**Language/Version**: Kotlin 2.4.10 (Kotlin/JS target), Compose HTML 1.11.1, Compose runtime
1.12.0 — unchanged from Phase 0/1.

**Primary Dependencies**: `kobweb-core`, `kobweb-silk` (already declared in
`site/build.gradle.kts`) — no new dependency. `Terminal`'s typing effect uses
`androidx.compose.runtime.LaunchedEffect` (already transitively available via `compose.runtime`,
same as Phase 1's `NavHeader.kt` side-menu state). Live reduced-motion reactivity (FR-003) uses
`kotlinx.browser.window.matchMedia(...)` with a `"change"` event listener (`kotlinx-browser`
already transitively available, same package Phase 1's `Lang.kt` imports `kotlinx.browser.window`
from) — CSS-only `prefers-reduced-motion` media queries (Phase 0/1's established pattern) can gate
static styles but can't stop an in-flight Kotlin coroutine, so this phase is the first to need the
JS-side listener.

**Storage**: N/A — components are presentational only (spec Assumptions); no component fetches or
persists data. Sample data for isolated rendering/verification is inline, ad-hoc, non-persisted.

**Testing**: No automated test suite exists in this project (unchanged from Phase 0/1).
Verification is isolated rendering of each component against representative sample data
(`quickstart.md`) plus the Kotlin compiler itself: `alt` text and every component's navigation
target are modeled as non-nullable constructor parameters, so FR-014/FR-004/FR-007/FR-008/FR-009
are enforced at compile time, not by a runtime check — satisfies SC-006's "fails to
compile/render without alt" via the type system rather than an assertion.

**Target Platform**: Static HTML/CSS/JS served from GitHub Pages, evergreen desktop and mobile
browsers — unchanged.

**Project Type**: Web frontend — Kobweb (Kotlin/JS) static-site-generated application, single
module (`:site`) — unchanged.

**Performance Goals**: No new performance target. `Terminal`'s per-character/per-line timing is
ported as-is from the mock (`docs/handoff/app.js`: 26ms/char on `$`-prefixed lines, 16ms/char
otherwise, 240ms pause between lines) — preserving the established feel rather than introducing a
new one (research.md).

**Constraints**: Zero new Gradle dependencies (constitution Principle VIII). Every interactive
element MUST hit 48px minimum touch target below `max-width: 720px` — the mock's own breakpoint
(`docs/handoff/styles.css:358`), a raw CSS media query rather than Silk's built-in `Breakpoint`
enum (whose smallest step, `Breakpoint.SM`, is 640px in the Kobweb default scale — not 720px, per
research.md). `Terminal` MUST stop/skip its animation the instant `prefers-reduced-motion`
changes mid-run (FR-003), not just on next mount. No component may hardcode project, trophy, stat,
tag, or timeline content (FR-001).

**Scale/Scope**: Seven new composable files, one entity (data class or enum) per component,
co-located in the same file as its composable — no new package, no page changes, no data layer,
no CI changes.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

`.specify/memory/constitution.md` v1.0.0, ratified 2026-09-17 (during this feature's
`/speckit-analyze` pass, resolving finding C1) — the same eight principles Phase 1's plan already
used from `ROADMAP.md`'s draft block, now formally adopted.

| Principle | Check | Result |
|---|---|---|
| I. Static-First, Zero Backend | All seven components are pure presentational composables — none fetches data, calls a service, or reads a credential | PASS |
| II. Content as Data, Not Markup | This phase's entire purpose: every component's content is a parameter/data class (`ProjectSummary`, `TimelineItem`, `StatItem`, `GameCoverImage`, `TrophyEntry`, `TagVariant`, `TerminalLine`), never hardcoded (FR-001) | PASS |
| III. Bilingual Parity (EN/PL) | FR-013: every component accepts already-localized text; none contains translation/detection logic. `Terminal`'s boot lines reuse the `BilingualString`-per-line shape established in `Lang.kt` | PASS |
| IV. Single-Sourced Design Tokens | FR-012: every component sources color/font/spacing exclusively from `SiteTheme.kt`; no inline magic values | PASS |
| V. Accessibility & Motion Discipline | FR-002/003 (full reduced-motion coverage, live-reactive) and FR-014/015 (required `alt` text, ARIA roles/labels) — delivered now rather than deferred to Phase 6 (F031), per clarification | PASS (this phase delivers it) |
| VI. No Secrets in the Client Bundle | No credentials involved | PASS (N/A) |
| VII. GitHub Pages Static Export as Sole Deployment Target | No build/export changes this phase; components compile as ordinary `jsMain` source consumed by the existing static export | PASS (N/A this phase) |
| VIII. YAGNI on Dependencies | No new Gradle dependency; typing effect and reduced-motion reactivity both use already-transitive `compose.runtime`/`kotlinx-browser` APIs | PASS |

No violations — Complexity Tracking is not needed for this phase.

## Project Structure

### Documentation (this feature)

```text
specs/003-components/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md        # Phase 1 output (/speckit-plan command)
├── quickstart.md        # Phase 1 output (/speckit-plan command)
└── tasks.md             # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

No `contracts/` directory: these components are internal `jsMain` composables consumed by a later
phase of the same codebase, not an API/CLI/service boundary exposed to users or other systems —
same rationale as Phase 0/1 (`specs/001-foundation-setup/plan.md`,
`specs/002-layout-routing/plan.md`). Each composable's parameter signature is documented in
`data-model.md` instead.

### Source Code (repository root)

```text
site/
├── build.gradle.kts                                     # unchanged this phase
└── src/jsMain/
    └── kotlin/com/anjo/anjosite/
        ├── SiteTheme.kt                                  # unchanged this phase (design-token source, FR-012)
        ├── Lang.kt                                       # unchanged this phase (BilingualString pattern reused by Terminal's boot lines)
        └── components/widgets/
            ├── IconButton.kt                              # unchanged this phase
            ├── Tag.kt                                     # NEW: TagVariant enum + Tag composable (FR-009)
            ├── StatRow.kt                                 # NEW: StatItem + StatRow composable (FR-006)
            ├── TimelineEntry.kt                           # NEW: TimelineItem + TimelineEntry composable (FR-005)
            ├── ProjectCard.kt                              # NEW: ProjectSummary + ProjectCard composable (FR-004)
            ├── GameCover.kt                                # NEW: GameCoverImage + GameCover composable (FR-007)
            ├── TrophyRow.kt                                # NEW: TrophyEntry + TrophyRow composable (FR-008)
            └── Terminal.kt                                 # NEW: TerminalLine + Terminal composable (FR-002/FR-003)
```

**Structure Decision**: All seven new files land in the existing `components/widgets/` package
alongside `IconButton.kt` — no new package taxonomy for seven small, flat, sibling components
(YAGNI). Each file co-locates its composable with the one data class/enum it renders, matching
this phase's "content as data" requirement without introducing a separate `models`/`entities`
package nobody asked for.

## Complexity Tracking

*No violations — table intentionally omitted.*
