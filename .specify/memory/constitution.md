<!--
Sync Impact Report
Version change: TEMPLATE → 1.0.0 (initial ratification)
Modified principles: n/a (first concrete fill of template placeholders)
Added sections:
  - Core Principles I–VIII (Static-First & Zero Backend, Content as Data,
    Bilingual Parity, Single-Sourced Design Tokens, Accessibility & Motion
    Discipline, No Secrets in the Client Bundle, GitHub Pages Static Export
    as Sole Deployment Target, YAGNI on Dependencies)
  - Technology Constraints
  - Development Workflow
  - Governance
Removed sections: none
Templates requiring updates:
  - .specify/templates/plan-template.md: ✅ compatible as-is (generic
    "[Gates determined based on constitution file]" placeholder, no
    principle names hardcoded — Constitution Check gate should evaluate
    against Principles I–VIII at each /speckit-plan run)
  - .specify/templates/spec-template.md: ✅ no constitution references, no
    change needed
  - .specify/templates/tasks-template.md: ✅ no constitution references, no
    change needed
  - .specify/templates/checklist-template.md: ✅ no constitution references,
    no change needed
  - README.md: ✅ already documents `kobweb export --layout static` as the
    shipping step (Principle VII); no contradiction found
Follow-up TODOs:
  - TODO(RATIFICATION_DATE): set to the date this version is actually
    adopted by the team if that differs from the fill date below.
-->

# anjo-site Constitution

## Core Principles

### I. Static-First, Zero Backend
The site MUST ship as a static export with no server-side code and no
database. Any feature that looks like it needs a backend (contact form,
guestbook, counters) MUST instead route through `mailto:` links or a
public-key third-party service (e.g. Formspree, Web3Forms) that is safe to
embed client-side. `configAsKobwebApplication` in `site/build.gradle.kts`
MUST NOT enable `includeServer = true` unless this principle is formally
amended.
**Rationale**: A personal portfolio has no operational reason to run or pay
for a server; every backend adds an attack surface and a maintenance cost
with no corresponding feature need.

### II. Content as Data, Not Markup
Projects, trophies, skills, and timeline/CV entries MUST live in
serializable Kotlin data classes or JSON/Markdown front matter, never
hardcoded inline inside composables. Adding a fifth project or a new job
entry MUST be possible by adding a data entry alone — no layout code
changes required.
**Rationale**: The handoff mock (`docs/handoff/`) hardcodes every project
card by hand; porting that pattern into Compose would make routine content
updates require code changes and review for no benefit.

### III. Bilingual Parity (EN/PL)
Every user-facing page MUST ship both English and Polish from the same
component via a language state (e.g. `CompositionLocal`), never via
duplicated markup blocks per language (the `data-lang-block` pattern in
`docs/handoff/index.html` is explicitly the anti-pattern being replaced).
Technical labels, stack/tag names, and tool names stay in English in both
languages.
**Rationale**: Duplicated markup blocks double the surface area for every
future layout change and silently drift out of sync; a shared component
with a language switch cannot drift structurally.

### IV. Single-Sourced Design Tokens
Colors, fonts, and spacing MUST be defined once in `SiteTheme.kt` (or an
equivalent single theme source) and referenced from there. No composable
may hardcode a color, font-family, or magic spacing value inline.
**Rationale**: The handoff mock centralizes its palette in five CSS custom
properties (`--pink`, `--cyan`, `--red`, `--bg`, `--ink`); the Kobweb port
MUST preserve that property — a palette change is one edit, not a grep-and-
replace across composables.

### V. Accessibility & Motion Discipline
`prefers-reduced-motion` MUST disable all decorative animation (scanlines,
glitch text, boot-typing effect, blinking cursor) — the reduced-motion
variant renders final state immediately instead of animating toward it.
Interactive touch targets MUST be at least 48px tall on breakpoints below
720px.
**Rationale**: These are the two concrete accessibility commitments already
implemented in `docs/handoff/app.js` and `styles.css`; the Kobweb rebuild
MUST NOT regress them.

### VI. No Secrets in the Client Bundle
Anything password-equivalent (PSN `NPSSO`, API keys, form service secrets)
MUST NOT be fetched or embedded client-side. Such credentials live only in
GitHub Actions secrets, which feed a pre-generated static JSON (e.g.
`trophies.json`) consumed by the site at build or runtime — never a
credentialed client-side fetch.
**Rationale**: The Kotlin/JS bundle ships to every visitor's browser in
plain, readable form; anything shipped in it is public.

### VII. GitHub Pages Static Export as the Sole Deployment Target
The only supported deployment output is `kobweb export --layout static`
run from `site/`. A phase or feature is not "done" on a passing
`kobweb run` dev-server check alone — its definition of done MUST include a
successful static export. CI MUST run this exact export command before any
deploy to `gh-pages`.
**Rationale**: The dev server and the static-export renderer are not
guaranteed identical; only the export path is what actually ships.

### VIII. YAGNI on Dependencies
No new library or Gradle dependency is added unless the Kobweb/Silk/Kotlin
standard library genuinely cannot do it in a reasonable amount of code.
Prefer what `site/build.gradle.kts` already declares (`compose.runtime`,
`compose.html.core`, `kobweb.core`, `kobweb.silk`, `kobwebx.markdown`)
before reaching for anything new — including the commented-out
`silk.icons.fa` dependency, which stays commented out until a concrete icon
need can't be met with the built-in SVG icons.
**Rationale**: Every added dependency is a build-time, bundle-size, and
maintenance cost for a project with no operational team behind it.

## Technology Constraints

- Kobweb (Kotlin Multiplatform), frontend-only: `jsMain` source set in the
  `:site` Gradle module, package `com.anjo.anjosite`. `jvmMain` /
  `includeServer = true` stay disabled per Principle I.
- UI layer: Compose HTML (`compose.runtime`, `compose.html.core`) + Silk
  (`kobweb.silk`) for styling/theming; `kobwebx.markdown` for Markdown-
  sourced pages (e.g. `resources/markdown/About.md`).
- `.kobweb/conf.yaml` defines the site title and the dev/prod script and
  content-root paths; production `siteRoot` is `.kobweb/site`, the output
  of the static export. `basePath` MUST be set correctly for whichever
  GitHub Pages mode is chosen (project page under `/anjo-site/` vs. a
  `username.github.io` root page) before the first production export.
- Build tooling is Gradle Kotlin DSL (`gradlew`, `settings.gradle.kts`,
  `gradle.properties`); no other build system is introduced.

## Development Workflow

- Every phase's definition of done requires a successful
  `kobweb export --layout static` run from `site/`, not just a working
  `kobweb run` dev session (Principle VII).
- Any change touching a user-facing page MUST be checked in both EN and PL
  (Principle III) and with `prefers-reduced-motion` toggled on (Principle
  V) before being considered complete.
- Any change introducing a new Gradle dependency MUST state in its
  plan/PR why the existing stack (Principle VIII's declared dependency
  list) could not do it.
- CI (GitHub Actions) MUST run the same static-export command used locally
  before any deploy to `gh-pages`, so the export is never validated only
  on a developer machine.

## Governance

This constitution supersedes other project documents (including
`ROADMAP.md` phase notes) wherever the two disagree; a roadmap phase MUST
be adjusted to comply, not the other way around. Amendments follow semantic
versioning:
- **MAJOR** — a principle is removed or redefined in a backward-incompatible
  way (e.g. dropping the zero-backend rule).
- **MINOR** — a new principle or section is added, or existing guidance is
  materially expanded.
- **PATCH** — wording, clarification, or typo fixes with no rule change.

Every `/speckit-plan` run MUST evaluate its Constitution Check gate against
Principles I–VIII above. Any complexity or deviation that cannot satisfy a
principle MUST be justified in that plan's Complexity Tracking section or
the principle takes precedence and the plan is revised.

**Version**: 1.0.0 | **Ratified**: 2026-09-16 | **Last Amended**: 2026-09-16
