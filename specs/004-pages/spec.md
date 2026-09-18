# Feature Specification: Pages

**Feature Branch**: `004-pages`

**Created**: 2026-09-17

**Status**: Draft

**Input**: User description: "Phase 3 — Pages: replace every placeholder route with the mock's
real content, wired through the seven shared components built in Phase 2 (specs/003-components)
and the language/theme mechanisms from Phase 0/1. Add the one route that doesn't exist yet
(About). Scope: Home, About, Projects (grid + detail), Trophies (UI + trophies.json fetch,
static/placeholder JSON only), Contact (mailto-only), CV (print-friendly, separate data source),
plus a mobile check of every page against docs/handoff/mobile-check.html at 390/430/768. Out of
scope: new shared components, trophies.json generation automation, projects/skills as external
JSON files, CI/deploy, SEO/analytics polish."

## Clarifications

### Session 2026-09-17

- Q: How is a single project's detail view addressed/reached? → A: Dedicated route per project
  via a Kobweb dynamic route segment (e.g. `/projects/{slug}`) — each project exports as its own
  static HTML file with a shareable URL, rather than a client-side toggle within `/projects`.
- Q: What does the visitor see while `trophies.json` is still loading (before it resolves or
  fails)? → A: A skeleton-like loading state reusing each component's existing placeholder mode
  (e.g. `GameCover`'s image-placeholder from Phase 2) — never a blank content area.
- Q: Does Contact keep the mock's single free-text message field (fixed subject, hardcoded
  recipient), or add structured fields (name/email/subject)? → A: Match the mock exactly — a
  single message field only, fixed subject line, recipient hardcoded to the site owner's email.
- Q: FR-014 restricts every route to the seven existing Phase 2 components, but Contact needs a
  text input and send control that isn't one of the seven — is that restriction absolute? → A:
  Yes, absolute — Contact's message-input/send interaction ships as one new shared component
  introduced by this phase (not inline Silk primitives), the sole exception to FR-014's "no new
  shared component" rule.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visitor lands on Home and reaches Projects (Priority: P1)

A visitor arrives at the site's root, reads the real hero content (with the boot-typing terminal
intro), and follows a call-to-action into the real Projects grid, then opens a single project's
detail view.

**Why this priority**: Home and Projects are the site's front door and its primary evidence of
work — the journey most visitors and reviewers take first. Without real content here, the site
has nothing to show regardless of what else is finished.

**Independent Test**: Load `/` and confirm real (non-placeholder) hero content renders with the
terminal effect; follow the "view projects" call-to-action to `/projects`, confirm a grid of real
`ProjectCard`s renders from a data list, and open one card's detail view.

**Acceptance Scenarios**:

1. **Given** a visitor with no reduced-motion preference loads `/`, **When** the page mounts,
   **Then** the hero's boot text types out via `Terminal` and the kicker/heading/lede and both
   CTAs ("view projects", "read cv") are visible.
2. **Given** a visitor on `/`, **When** they activate the "view projects" CTA, **Then** they land
   on `/projects` showing one `ProjectCard` per entry in the projects data list.
3. **Given** a visitor on the Projects grid, **When** they open one project's card, **Then** they
   see that project's detail view with its full description, tags, and any linked assets.
4. **Given** the projects data list is empty, **When** `/projects` renders, **Then** the grid
   shows no cards and no broken layout (empty state, not an error).

---

### User Story 2 - Visitor reads About and the printable CV (Priority: P2)

A visitor wanting background on the site owner reads the About page's bio and career timeline,
then opens the CV page and prints or exports it to PDF and gets a clean, resume-formatted result.

**Why this priority**: These are the credibility/background pages a visitor checks after Home and
Projects, not before — real content matters, but the journey isn't blocked without them the way
Home/Projects would block it.

**Independent Test**: Load `/about`, confirm real bio copy and a `TimelineEntry` list render; load
`/cv`, confirm real CV content renders on-screen and use the browser's print preview to confirm it
still reads correctly as a resume (no nav chrome, no cut-off content).

**Acceptance Scenarios**:

1. **Given** a visitor loads `/about`, **When** the page renders, **Then** real bio copy and one
   `TimelineEntry` per career/history item are visible, sourced from a data list.
2. **Given** a visitor loads `/cv`, **When** the page renders, **Then** real CV content appears,
   drawn from its own data source (not reused from Projects or About).
3. **Given** a visitor opens the browser's print preview on `/cv`, **When** the preview renders,
   **Then** the content reads as a clean printable resume (no interactive nav chrome dominating
   the page, no visibly cut-off sections).

---

### User Story 3 - Visitor browses Trophies (Priority: P2)

A visitor curious about gaming activity opens the Trophies page and sees a stats summary, a grid
of game covers, and a feed of individual trophies.

**Why this priority**: Trophies is personality/hobby content — valuable but secondary to the
professional-facing pages, and it's the one page in this phase with a runtime fetch, so it
carries its own loading/empty-data risk distinct from the purely static pages above.

**Independent Test**: Load `/trophies` and confirm it fetches `trophies.json` and renders
`StatRow` (summary), `GameCover` (grid), and `TrophyRow` (feed) from that fetched data, without
needing the Phase 4 PSN automation to exist — a hand-written placeholder JSON is sufficient.

**Acceptance Scenarios**:

1. **Given** `trophies.json` contains stats, games, and trophies, **When** `/trophies` renders,
   **Then** a `StatRow` summary, a `GameCover` per game, and a `TrophyRow` per trophy all appear,
   sourced from that fetched data (none hardcoded in the page).
2. **Given** `trophies.json` is temporarily unavailable or fails to load, **When** `/trophies`
   renders, **Then** the page shows a clear empty/error state rather than a blank page or a
   crash.
3. **Given** `trophies.json` has not yet resolved, **When** `/trophies` first renders, **Then** a
   skeleton-like loading state (reusing each component's existing placeholder mode) is shown
   rather than a blank content area.

---

### User Story 4 - Visitor contacts via mailto (Priority: P3)

A visitor who wants to get in touch types a message into the Contact page's single message field
and it builds a `mailto:` link to the site owner's fixed address with a fixed subject line and
their message as the body, which opens their own email client to send.

**Why this priority**: Contact is the conversion step at the end of the journey — necessary, but
only reached by visitors who are already convinced, so it's the lowest-risk page to finish last.

**Independent Test**: Load `/contact`, type sample text into the message field, and confirm a
correctly-formed `mailto:` link (hardcoded recipient, fixed subject, body populated from the
message) is produced, with no network request made.

**Acceptance Scenarios**:

1. **Given** a visitor types a message and activates the send action, **When** the link is built,
   **Then** a `mailto:` link opens addressed to the site owner's fixed email, with the fixed
   subject line and the visitor's message as the body.
2. **Given** a visitor leaves the message field empty, **When** they attempt to send, **Then** the
   page indicates the message is required rather than opening a malformed `mailto:` link.

---

### Edge Cases

- What happens when the projects data list is empty? → Grid renders an empty state, no broken
  layout (US1, scenario 4).
- What happens when `trophies.json` fails to load or is malformed? → Trophies page shows a clear
  empty/error state, not a crash or blank screen (US3, scenario 2).
- What happens while `trophies.json` is still in flight? → A skeleton-like loading state renders,
  reusing each component's existing placeholder mode, never a blank content area (US3, scenario
  3).
- What happens when a project detail is opened for an id that doesn't exist in the data list? →
  Visitor sees a not-found state for that project (reusing the site's existing 404 handling from
  Phase 1, not a new mechanism).
- What happens when a visitor has `prefers-reduced-motion: reduce` set and loads Home? → The
  `Terminal` boot text appears immediately in full, per Phase 2's existing reduced-motion
  contract — this phase doesn't change that behavior, only supplies the real boot text.
  Every page is checked against `docs/handoff/mobile-check.html`'s 390 / 430 / 768 reference
  widths after being built (not just Home/Projects) — see SC-005.
- What happens when the Contact prompt's message field is left empty? → Submission is blocked
  with a visible indication that a message is required, not a `mailto:` link with an empty body
  (US4, scenario 2).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The Home route MUST render real bilingual hero content (kicker, heading, lede) via
  `Terminal`, replacing the Phase 0/1 token-swatch and language-demo placeholder.
- **FR-002**: The Home route MUST provide working navigation to both `/projects` and `/cv` via its
  two call-to-action controls.
- **FR-003**: The About route MUST exist (it does not today) and MUST render real bilingual bio
  copy plus a career/history timeline built from `TimelineEntry`.
- **FR-004**: The About route MUST be reachable from `NavHeader`'s existing `/about` link.
- **FR-005**: The Projects route MUST render one `ProjectCard` per entry in a data list (not
  individually hardcoded cards), and MUST provide a detail view for a single selected project,
  reachable at its own dedicated URL via a Kobweb dynamic route segment (e.g. `/projects/{slug}`)
  rather than a client-side-only toggle.
- **FR-006**: The Projects data list MUST be sourced from a static, hand-authored `projects.json`
  file fetched at runtime (same pattern as `trophies.json`, FR-007/FR-008), not hardcoded per-card
  in the page — this lets project content be edited without touching Kotlin. *(Amended during
  `/speckit-converge` T019: originally required an in-source `List` with JSON deferred to Phase 4;
  superseded by an explicit user request to externalize Projects the same way Trophies already
  was, made and shipped during this phase.)*
- **FR-007**: The Trophies route MUST fetch `trophies.json` at runtime and render its contents
  through `StatRow`, `GameCover`, and `TrophyRow`, with none of that content hardcoded in the
  page.
- **FR-008**: `trophies.json`'s content for this phase MUST be a static, hand-authored placeholder
  file checked into the project — the automated nightly generation of this file is explicitly out
  of scope for this phase.
- **FR-009**: The Trophies route MUST render a defined empty/error state when `trophies.json` is
  missing, unreachable, or malformed, rather than crashing or rendering blank.
- **FR-010**: The Contact route MUST build a `mailto:` link from a single visitor-entered message
  field, addressed to the site owner's hardcoded email with a fixed subject line, entirely
  client-side, with no network request to any backend or third-party form service — matching
  `docs/handoff/app.js`'s existing contact prompt (single field, fixed subject, hardcoded
  recipient), not a multi-field contact form. This input/send interaction is built as one new
  shared component (`ContactPrompt`) — the sole exception to FR-014.
- **FR-011**: The Contact route MUST block sending and indicate the message is required when the
  message field is empty, rather than producing a `mailto:` link with an empty body. This
  indication, and the send control's own label, are real user-facing prose and MUST be bilingual
  like every other route (FR-013) — `ContactPrompt` reads them via `LocalLang.current` internally,
  same as `NavHeader`'s own chrome text (analysis finding B1).
- **FR-012**: The CV route MUST render real, print-legible content sourced from its own dedicated
  data structure, distinct from the Projects and About data.
- **FR-013**: Every route in this phase MUST render its content bilingually (English/Polish) via
  the existing `LocalLang`/`BilingualString` mechanism — no page-specific translation logic.
- **FR-014**: Every route in this phase MUST be composed from the seven existing Phase 2
  components (`Terminal`, `ProjectCard`, `TimelineEntry`, `StatRow`, `GameCover`, `TrophyRow`,
  `Tag`) plus existing layout/nav infrastructure, with exactly two exceptions: Contact's
  message-input/send interaction ships as one new shared component introduced by this phase
  (`ContactPrompt`, per FR-010), and a small `LinkCell` component (a literal port of the mock's
  `<a class="link-cell">`) used by both Home's Contact band and the dedicated Contact page's
  channel list — no other new shared component may be introduced. *(Amended during
  `/speckit-converge` T020: `LinkCell` shipped during this phase's Home implementation without
  being named as an FR-014 exception at the time.)*
- **FR-015**: Every route in this phase MUST be verified against `docs/handoff/mobile-check.html`'s
  390px, 430px, and 768px reference widths after its real content is in place.
- **FR-016**: The Trophies route MUST render a skeleton-like loading state — reusing each
  component's existing placeholder mode (e.g. `GameCover`'s image placeholder) — while
  `trophies.json` is in flight, rather than a blank content area. *(Renumbered from FR-009a during
  analysis for sequential-ID consistency — no behavior change.)*
- **FR-017**: Every route's nav header and footer chrome MUST be hidden under print media (e.g.
  browser print preview), site-wide — not just on `/cv`, since nothing about "don't compete with
  printed content" is CV-specific once stated this way (SC-004).

### Key Entities

- **ProjectEntry**: A project's bilingual content (title, short/full description, cover alt text
  as `BilingualString`) plus a unique `slug` (identity used by its dedicated `/projects/{slug}`
  route) and plain tech-stack tags (English in both languages, constitution III). Resolved to
  Phase 2's `ProjectSummary` at render time per the current language (analysis finding C1) — the
  detail view renders one in full.
- **AboutContent**: The About page's bio copy (`BilingualString`) plus its ordered list of
  bilingual timeline entries, each resolved to Phase 2's `TimelineItem` at render time (finding
  C1).
- **TrophiesData**: The shape of `trophies.json` — a list of stats (each a stable `key` + fetched
  `value`, with the display *label* supplied bilingually by the page, not the JSON — finding C2),
  a list of games (consumed by `GameCover`), and a list of trophies (consumed by `TrophyRow`).
  Game titles and trophy names are proper-noun content from an external PSN-sourced automation
  (Phase 4) and are rendered as-is in both languages — a documented exception to FR-013, distinct
  from the page-owned stat labels.
- **CvContent**: The CV page's own dedicated content structure (sections/entries for experience,
  skills, etc.) — intentionally separate from `ProjectSummary` and `AboutContent` even where the
  underlying facts overlap, per FR-012.
- **ContactMessage**: The visitor's single free-text message, used as the body of the outgoing
  `mailto:` link — recipient and subject are fixed constants, not visitor-entered; never
  transmitted anywhere except into that link.
- **ContactPrompt** *(new shared component, FR-014 exception)*: The message-input/send widget
  itself — takes a `ContactMessage` value and an empty-field validation state, and is the one new
  shared component this phase introduces alongside the seven from Phase 2.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All six routes (`/`, `/about`, `/projects` incl. detail, `/trophies`, `/contact`,
  `/cv`) render real, non-placeholder bilingual content — zero routes still show a token-swatch,
  single-line placeholder heading, or "coming soon" state.
- **SC-002**: A visitor can go from `/` to a specific project's detail view using only on-page
  navigation, in two clicks or fewer.
- **SC-003**: 100% of Contact submissions with a non-empty message produce a correctly addressed
  `mailto:` link (fixed recipient and subject, message as body); 100% of submissions with an
  empty message are blocked with a visible reason, with zero malformed `mailto:` links produced
  either way.
- **SC-004**: The CV page's print preview shows the complete resume content with no visually
  cut-off section and no interactive site chrome (nav, language switch) competing with the
  printed content.
- **SC-005**: Every one of the six routes has been visually checked at all three of
  `docs/handoff/mobile-check.html`'s reference widths (390 / 430 / 768) with no overlapping,
  clipped, or below-48px-touch-target elements found.
- **SC-006**: `kobweb export -PkobwebExportLayout=STATIC` succeeds and produces a static HTML file
  for every one of the six routes **and for every individual project's `/projects/{slug}` detail
  page** — a shared project link that 404s on GitHub Pages is a failure of this criterion, not
  just of FR-005 (analysis finding F3: this closes the gap between clarification Q1's shareable-
  URL promise and what SC-006 originally measured).

## Assumptions

- The seven Phase 2 components (specs/003-components) cover every content shape this phase needs
  except Contact's message input/send interaction (`ContactPrompt`) and the small `LinkCell` link-
  row used by Home and Contact — the two FR-014 exceptions; no other page may introduce a new
  component without flagging it during planning first.
- Home's dark/light theme toggle (`Theme.kt`) and the brand hover/glitch effect in `NavHeader.kt`
  were added during this phase at explicit user request but aren't described by any FR above —
  they're app-wide chrome/mechanism additions (parallel to `Lang.kt`), not page content, and don't
  affect any of this phase's acceptance criteria. *(Added during `/speckit-converge` T022.)*
- `trophies.json`'s real, automatically-generated content (via the nightly PSN API job) is Phase
  4's responsibility (ROADMAP F025); this phase only needs the fetch-and-render behavior plus a
  static placeholder file that exercises it.
- Contact defaults to `mailto:`-only per YAGNI (constitution Principle VIII) — a third-party form
  service (Formspree/Web3Forms) stays an optional future enhancement (ROADMAP F023), not part of
  this phase's definition of done.
- Project detail pages for an unknown/missing id reuse the existing Phase 1 404 mechanism rather
  than introducing a second not-found pattern.
- No automated test suite exists in this project (unchanged from prior phases); verification is
  manual/browser-based, per `docs/handoff/mobile-check.html` and print-preview checks.
- Translatable text in every in-source list (Projects/About/CV) is stored bilingually and resolved
  to the current language at render time (data-model.md) — resolves analysis finding C1.
- `trophies.json`'s game titles and trophy names are proper-noun data from an external PSN-sourced
  automation and are not translated; only the page-owned stat *labels* are bilingual — resolves
  analysis finding C2.
- `ContactPrompt`'s send-button label and "message required" validation text are internal
  `BilingualString` constants read via `LocalLang.current`, not caller parameters — resolves
  analysis finding B1.
