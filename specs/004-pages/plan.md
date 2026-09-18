# Implementation Plan: Pages

**Branch**: `004-pages` | **Date**: 2026-09-17 | **Spec**: [spec.md](./spec.md)

**Input**: Feature specification from `/specs/004-pages/spec.md`

**Note**: This template is filled in by the `/speckit-plan` command. See `.specify/templates/plan-template.md` for the execution workflow.

## Summary

Replace all six placeholder routes with real content, adding the missing `/about` route and one
new dynamic route per project (`/projects/{slug}`, clarification Q1) for the detail view. About and
CV are built from in-source data lists (FR-012); Projects, the Home page's Stack section, and
Trophies are each a hand-authored static JSON file fetched at runtime (FR-006/FR-008, amended
during `/speckit-converge`) — all built from the seven existing Phase 2 components, plus two new
shared components this phase introduces: `ContactPrompt` (FR-010/FR-011 exception) built from
Silk's own native `TextInput`/`Button` widgets, and `LinkCell` (FR-014 exception, amended during
`/speckit-converge`), a small link-row used by Home and Contact. Trophies has explicit
loading/error/success states (FR-009/FR-016) so it's never blank while its fetch is in flight.
Static export requires one extra piece of build config — `site/build.gradle.kts`'s
`kobweb.app.export.addExtraRoute(...)` — to actually produce a static HTML file per project slug,
since Kobweb's exporter otherwise skips any route containing a dynamic segment.

## Technical Context

**Language/Version**: Kotlin 2.4.10 (Kotlin/JS target), Compose HTML 1.11.1, Compose runtime
1.12.0 — unchanged from Phase 0–2.

**Primary Dependencies**: `kobweb-core` 0.25.1, `kobweb-silk` (already declared) — no new Gradle
dependency. Project detail uses Kobweb's dynamic route segments (`@Page("{}")` +
`rememberPageContext().route.params`, research.md §1). `trophies.json` is fetched via
`kotlinx.browser.window.fetch` + `kotlinx-coroutines`'s `Promise.await()` (already transitively
present — `Terminal.kt` already uses `kotlinx.coroutines.delay`) and parsed with Kotlin/JS's
built-in `JSON.parse<dynamic>(...)`, not `kotlinx.serialization` (research.md §3/§4, no such
plugin/dependency exists in this project today). `ContactPrompt` is built from Silk's own native
`TextInput`/`Button` composables (research.md §6). CV's print-only chrome hiding uses Silk's
built-in `mediaPrint` `CssRule` (research.md §5).

**Storage**: N/A — no backend, no persistence. About/CV content are in-source `List<...>` literals
(FR-012). Projects, the Home page's Stack section, and Trophies are each a hand-authored static
JSON file (`projects.json`, `stack.json`, `trophies.json`) served as a Kobweb public resource and
fetched at runtime with the same `window.fetch` + `JSON.parse<dynamic>` pattern — none generated
by any automation this phase (FR-008; the nightly PSN job is Phase 4/ROADMAP F025). *(Amended
during `/speckit-converge` T021: `projects.json`/`stack.json` were added mid-phase at explicit user
request, pulling forward part of Phase 4's external-JSON-sourcing scope for these two lists only —
Trophies' fetch-and-render pattern was simply reused rather than reinvented.)*

**Testing**: No automated test suite exists in this project (unchanged from Phase 0–2).
Verification is `quickstart.md`'s manual scenario checklist plus `docs/handoff/mobile-check.html`
(390/430/768) and a browser print-preview check for `/cv` — per constitution Principle VII, a
successful `kobweb export -PkobwebExportLayout=STATIC` producing a real file per route (including
each project slug) is part of this phase's definition of done, not just a passing dev-server run.

**Target Platform**: Static HTML/CSS/JS served from GitHub Pages, evergreen desktop and mobile
browsers — unchanged.

**Project Type**: Web frontend — Kobweb (Kotlin/JS) static-site-generated application, single
module (`:site`) — unchanged.

**Performance Goals**: No new performance target.

**Constraints**: Zero new Gradle dependencies (constitution Principle VIII). Every route in this
phase composes from the seven Phase 2 components plus exactly two new components, `ContactPrompt`
and `LinkCell` (FR-014, amended during `/speckit-converge`) — no other new shared component.
`trophies.json`'s fetch MUST render a defined loading state and a defined error state, never a
blank page (FR-009/FR-016). Project detail MUST be reachable at a real, static, shareable URL —
which requires registering each slug via `build.gradle.kts`'s `kobweb.app.export.addExtraRoute(...)`
(the public API; the underlying `extraRoutes` property itself is `internal`, research.md §1) in
addition to the dynamic `@Page` route, or the static export silently produces no file for it.

**Scale/Scope**: Six page files (one new: `About.kt`) + one new dynamic sub-route file
(`pages/projects/Slug.kt`) + one new shared component (`ContactPrompt.kt`) + concrete in-source
data (`projects`, `about` bio/timeline, `cv` sections) + one hand-authored `trophies.json` +
two one-line `mediaPrint` additions to existing nav/footer styles + one `build.gradle.kts`
`extraRoutes` addition — no new package taxonomy, no CI changes.

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

`.specify/memory/constitution.md` v1.0.0.

| Principle | Check | Result |
|---|---|---|
| I. Static-First, Zero Backend | Contact builds a `mailto:` link entirely client-side (FR-010); Trophies fetches a static JSON file, not a backend endpoint | PASS |
| II. Content as Data, Not Markup | Projects, Home's Stack section, and Trophies are hand-authored JSON fetched at runtime (FR-006/FR-008, amended during `/speckit-converge`); About/CV content are `List<data class>` literals (FR-012). Neither pattern embeds content as markup in composables; adding a project/timeline entry means editing a JSON file or a list, not layout code | PASS |
| III. Bilingual Parity (EN/PL) | FR-013: every route renders through `LocalLang`/`BilingualString`. In-source lists (Projects/About/CV) store `BilingualString` fields resolved at render time; `trophies.json`'s game/trophy proper nouns are a documented exception (research.md §8), its stat *labels* are page-owned `BilingualString`s | PASS |
| IV. Single-Sourced Design Tokens | All new page content and `ContactPrompt` source colors/fonts/spacing from `SiteTheme.kt`, consistent with every Phase 2 component | PASS |
| V. Accessibility & Motion Discipline | `ContactPrompt` uses Silk's native `TextInput`/`Button` (real, labelable, keyboard-operable form elements) rather than a hand-rolled `Div`-based control (research.md §6); no new animation/motion introduced this phase — Home reuses Phase 2's already-compliant `Terminal` | PASS |
| VI. No Secrets in the Client Bundle | `trophies.json` is a hand-authored static file with no credentials; the real PSN-backed automation (Phase 4) is explicitly out of scope here | PASS |
| VII. GitHub Pages Static Export as Sole Deployment Target | Definition of done requires a successful `kobweb export -PkobwebExportLayout=STATIC` producing a real HTML file per route *and* per project slug (via `extraRoutes`) — not just a dev-server pass | PASS (contingent on `extraRoutes`, tracked in research.md §1/tasks) |
| VIII. YAGNI on Dependencies | No new Gradle dependency: dynamic routing, `fetch`, `JSON.parse`, `mediaPrint`, and `TextInput`/`Button` are all already-available Kobweb/Silk/stdlib features (research.md) | PASS |

No violations — Complexity Tracking is not needed for this phase.

## Project Structure

### Documentation (this feature)

```text
specs/004-pages/
├── plan.md              # This file (/speckit-plan command output)
├── research.md          # Phase 0 output (/speckit-plan command)
├── data-model.md         # Phase 1 output (/speckit-plan command)
├── quickstart.md         # Phase 1 output (/speckit-plan command)
└── tasks.md              # Phase 2 output (/speckit-tasks command - NOT created by /speckit-plan)
```

No `contracts/` directory: this phase adds page composables and one shared component to an
existing `jsMain` frontend, not an API/CLI/service boundary — same rationale as every prior phase
(`specs/001-foundation-setup/plan.md` through `specs/003-components/plan.md`). Route shapes and
entity contracts are documented in `data-model.md` instead.

### Source Code (repository root)

```text
site/
├── build.gradle.kts                                     # MODIFIED: kobweb.app.export.addExtraRoute (research.md §1)
└── src/jsMain/
    ├── resources/public/
    │   ├── projects.json                                 # NEW: hand-authored Projects data (FR-006, converge T019/T021)
    │   ├── stack.json                                     # NEW: hand-authored Home Stack data (converge T021)
    │   └── trophies.json                                  # NEW: hand-authored Trophies placeholder (FR-008)
    └── kotlin/com/anjo/anjosite/
        ├── SiteTheme.kt                                  # unchanged this phase
        ├── Theme.kt                                      # NEW: dark/light toggle mechanism, out of FR scope (converge T022)
        ├── Lang.kt                                       # MODIFIED: + BilingualTimelineItem/resolve(lang) (data-model.md, analyze finding C1)
        ├── components/
        │   ├── widgets/
        │   │   ├── Terminal.kt, ProjectCard.kt, TimelineEntry.kt,
        │   │   │   StatRow.kt, GameCover.kt, TrophyRow.kt, Tag.kt   # unchanged this phase (reused as-is)
        │   │   ├── ContactPrompt.kt                       # NEW: ContactMessage + ContactPrompt composable (FR-014 exception #1)
        │   │   └── LinkCell.kt                            # NEW: link-row used by Home + Contact (FR-014 exception #2, converge T020)
        │   └── sections/
        │       ├── NavHeader.kt                           # MODIFIED: + mediaPrint hide rule (FR-017), + brand hover/glitch (converge T022)
        │       └── Footer.kt                               # MODIFIED: + mediaPrint hide rule (FR-017, research.md §5)
        └── pages/
            ├── Index.kt                                   # MODIFIED: real Home content incl. stack.json fetch (FR-001/FR-002, converge T021)
            ├── About.kt                                    # NEW: route doesn't exist today (FR-003/FR-004)
            ├── Projects.kt                                 # MODIFIED: real grid fetched from projects.json (FR-005/FR-006/FR-013, converge T019)
            ├── projects/Slug.kt                            # NEW: dynamic detail route, @Page("{}") (FR-005, research.md §1/§2)
            ├── Trophies.kt                                 # MODIFIED: fetch + render trophies.json (FR-007/FR-008/FR-009/FR-013/FR-016)
            ├── Contact.kt                                  # MODIFIED: wires ContactPrompt (FR-010/FR-011)
            └── Cv.kt                                       # MODIFIED: real print-friendly CV content (FR-012)
```

**Structure Decision**: One new package, `pages/projects/`, holding only the dynamic detail route
— Kobweb's file-based routing requires the sub-path to exist as a real directory under `pages/`
(mirrors `Index.kt`'s existing top-level placement; no other new package). `ContactPrompt` lands
in the existing `components/widgets/` alongside the seven Phase 2 components rather than a new
package, since it's the same kind of thing (a small, flat, sibling composable) — consistent with
Phase 2's own Structure Decision.

## Complexity Tracking

*No violations — table intentionally omitted.*
