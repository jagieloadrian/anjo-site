# Feature Specification: Layout and Routing

**Feature Branch**: `002-layout-routing`

**Created**: 2026-09-17

**Status**: Draft

**Input**: User description: "Phase 1 — Layout and Routing: replace the handoff mock's client-side
visibility router with Kobweb's real file-based routing, and stand up the real nav shell and
language-switching mechanism on top of the Phase 0 visual foundation (specs/001-foundation-setup)
— still with no real page content. Scope: confirm Kobweb's routing model replaces the mock's
router (F005), verify 404 handling under static export (F006), NavHeader brand/nav/language
switch (F007), i18n mechanism replacing data-lang-block (F008), design-notes panel production
handling (F009)."

## Clarifications

### Session 2026-09-17

- Q: Which nav destinations does the nav header link to this phase — only the routes that exist
  today (Home/About), or all six of the mock's destinations (Home/About/Projects/Trophies/
  Contact/CV)? → A: All six, matching the mock's nav exactly. Routes without real content yet
  (Projects/Trophies/Contact/CV) get a minimal placeholder page — following the same pattern
  Phase 0 established for the Index page (renders on the foundation, no real copy) — rather than
  a dead link or an omitted nav item.
- Q: Does the brand mark in the nav get the mock's hover glitch animation this phase, or stay
  static (animation deferred)? → A: Full glitch effect, ported from the mock
  (docs/handoff/styles.css:65-82) — small, self-contained, already reduced-motion-gated in the
  mock, and explicitly named in ROADMAP.md F007 ("brand glitch").
- Q: What language shows on first load, before the visitor touches the switch? → A: Detect the
  visitor's browser language; default to Polish if it matches, otherwise English.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visitor navigates between real pages (Priority: P1)

A visitor clicks a nav link and lands on a genuinely different, independently addressable page
(its own exported HTML file, its own URL) — not a hidden/shown section of one big document.

**Why this priority**: Real routing is the structural precondition for every later page (Phase 3)
and for the site being deep-linkable/shareable/indexable at all. Nothing downstream can rely on
"a page" as a concept until this is true.

**Independent Test**: Export the site and confirm each of the six nav destinations (Home, About,
Projects, Trophies, Contact, CV) is a separate file in the export output, reachable by its own
URL, with no `[data-screen]`/`.hidden`-style client-side show/hide logic anywhere in the Kotlin
source.

**Acceptance Scenarios**:

1. **Given** the exported site, **When** a visitor clicks any of the six nav links, **Then** the
   browser navigates to a distinct URL that resolves to a real, separately exported HTML file —
   never a dead link, even for destinations without real content yet.
2. **Given** the site's Kotlin source, **When** it is inspected, **Then** no hand-rolled
   visibility router (hash-based, `.hidden`-toggling, or otherwise) exists — navigation is
   Kobweb's own file-based `@Page` routing only.

---

### User Story 2 - Visitor sees the on-brand nav and can switch language (Priority: P1)

A visitor sees a navigation header matching the site's real brand (not the generic Kobweb
template logo) and can switch the page's language between English and Polish using a visible
control, seeing at least one piece of on-page text change accordingly.

**Why this priority**: The nav is present on every page and is most visitors' first on-brand
impression beyond Phase 0's background/type; the language mechanism is foundational
infrastructure every later page's real copy (Phase 3) will depend on.

**Independent Test**: Load the exported site, confirm the nav shows the site's own brand mark
(not the Kobweb logo), and confirm clicking the language switch changes visible text without a
full navigation to a different route.

**Acceptance Scenarios**:

1. **Given** the exported site, **When** a visitor loads any page, **Then** the nav header shows
   the site's own brand mark, not the placeholder Kobweb logo, and hovering it triggers the
   glitch animation unless reduced-motion is set.
2. **Given** the exported site, **When** a visitor loads any page, **Then** the initial language
   is Polish if their browser language matches, otherwise English.
3. **Given** the exported site, **When** a visitor activates the language switch, **Then** at
   least one piece of visible text changes between its English and Polish form.
4. **Given** the site's Kotlin source, **When** it is inspected, **Then** no piece of bilingual
   content is implemented as duplicated sibling markup blocks (the `data-lang-block` pattern) —
   each string exists once, in a data form, selected by a language state.

---

### User Story 3 - Visitor hitting an unknown URL sees an on-brand 404 (Priority: P3)

A visitor who follows a broken or outdated link sees a 404 page that still carries the site's
visual foundation (Phase 0 tokens/overlays), not a bare, unstyled, or GitHub-default error page.

**Why this priority**: Lower priority than real navigation or the nav/i18n foundation because it
only affects the broken-link edge case, but it's cheap to get right now and embarrassing to catch
after deploy.

**Independent Test**: Export the site, open the generated 404 page directly, and confirm it
renders with the same background/type/overlay foundation as every other page.

**Acceptance Scenarios**:

1. **Given** the exported site, **When** it is built, **Then** a `404.html` file is present in
   the export output.
2. **Given** that `404.html`, **When** it is opened, **Then** it renders using the same design
   tokens, typefaces, and overlay chrome as any other page (no default/unstyled fallback).

### Edge Cases

- What happens to the language switch's chosen language when a visitor navigates to a different
  page? Out of scope for this phase — this phase only has to prove the switch changes visible
  text on the page it's used on; cross-page/session persistence is not required until real pages
  with real copy exist (Phase 3).
- What happens if a visitor's browser has JavaScript disabled? Out of scope — the static export's
  page-to-page navigation already works via plain `<a href>` links (Kobweb's routing), so this is
  inherently resilient, but no explicit no-JS test is required this phase.
- What happens to the design-notes panel from the mock? It MUST NOT appear anywhere in the
  production export (see FR-011) — there is no "partial" state to test.
- What happens to the brand glitch animation when reduced-motion is set? It MUST NOT play (see
  FR-005) — the brand mark still renders, just without the hover animation, consistent with
  Phase 0's motion-discipline pattern (constitution Principle V).

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The site MUST use Kobweb's native file-based page routing as the sole navigation
  mechanism — no hand-rolled client-side router (hash-based, `.hidden`/`[data-screen]`-toggling,
  or otherwise) may exist in the Kotlin source.
- **FR-002**: A static export MUST produce a separate, independently addressable HTML file per
  route.
- **FR-003**: A static export MUST produce a `404.html` file for unmatched routes.
- **FR-004**: The generated `404.html` MUST render using the same design tokens, typefaces, and
  overlay chrome established in Phase 0 — not a bare/unstyled fallback.
- **FR-005**: The nav header MUST display the site's own brand mark in place of the generic
  Kobweb template logo, with the mock's hover glitch animation (docs/handoff/styles.css:65-82),
  suppressed under `prefers-reduced-motion: reduce` per constitution Principle V.
- **FR-006**: The nav header MUST link to all six of the site's destinations — Home, About,
  Projects, Trophies, Contact, CV — matching the mock's nav (docs/handoff/index.html:21-26).
- **FR-007**: The nav header MUST include a visible language switch control.
- **FR-008**: The site MUST provide a language-state mechanism (a single shared state, not
  per-component duplication) that at least one piece of UI text reads from and re-renders
  according to. Its initial value MUST be Polish if the visitor's browser language matches,
  otherwise English.
- **FR-009**: Activating the language switch MUST change the rendered language of at least one
  piece of visible text, demonstrating the mechanism end-to-end.
- **FR-010**: No bilingual content may be implemented as duplicated sibling markup blocks (the
  `data-lang-block` anti-pattern) — each piece of bilingual text MUST exist once, as data, with
  the active language selecting which value renders.
- **FR-011**: The design-notes panel present in the handoff mock MUST NOT appear anywhere in the
  production static export.
- **FR-012**: Every nav destination without real content yet (Projects, Trophies, Contact, CV)
  MUST have a minimal placeholder page — rendering on the Phase 0 foundation (tokens, fonts,
  overlays), with no real copy or navigation beyond what this phase already provides — so no nav
  link is ever dead. Real content for these pages remains Phase 3's responsibility.

### Key Entities

- **Route**: A single navigable destination (e.g. `/`, `/about`, `/projects`, `/trophies`,
  `/contact`, `/cv`) backed by one `@Page` file; produces exactly one exported HTML file. A route
  without real content yet still exists as a minimal placeholder page (FR-012), never a 404.
- **Language State**: The single shared value (English or Polish) that determines which stored
  string value each bilingual piece of UI text renders; not itself content, just a selector.
  Initialized from the visitor's browser language (Polish on a match, English otherwise), then
  changeable via the nav's language switch.
- **Bilingual String**: A piece of UI text with exactly one English value and one Polish value,
  stored once as data and selected via Language State — never duplicated as sibling markup.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of nav destinations resolve to their own distinct, separately exported HTML
  file — zero destinations implemented as a hidden/shown section of another page.
- **SC-002**: Visiting any unmapped URL under the exported site shows an on-brand 404 page,
  verified with zero exceptions.
- **SC-003**: A visitor sees the correct initial language (Polish on a browser-language match,
  English otherwise) with zero manual action, and can change it via a single visible control with
  the change reflected in under 1 second (no full page reload required).
- **SC-004**: Every bilingual piece of UI text can have both its language values updated by
  editing exactly one place in the source — zero pieces of text require editing two separate
  markup locations to update both languages.
- **SC-005**: The static export command completes successfully with zero errors, and this passing
  export is the verification basis for SC-001 through SC-004 (not the dev server alone).

## Assumptions

- Kobweb's own `@Page`/file-based routing (already used by the existing `NavHeader.kt` `Link`
  calls to `/` and `/about`) fully replaces the mock's `app.js` visibility router — no gap
  requiring a custom routing layer.
- This phase's language-switch mechanism only needs to prove itself on existing placeholder
  content (e.g. the Phase 0 Index placeholder or a small demo string) — real bilingual page copy
  is Phase 3's responsibility, not this phase's.
- Browser-language detection uses the standard `navigator.language`-equivalent signal available
  to Kotlin/JS; no server-side geo/IP-based detection is introduced (constitution Principle I,
  static-only).
- Four new placeholder pages (Projects, Trophies, Contact, CV) are created this phase purely to
  give the six-link nav somewhere real to point (FR-012/Clarifications) — they carry no real
  content, matching the Phase 0 Index-placeholder pattern; Phase 3 replaces their content, not
  their route.
- The design-notes panel is omitted entirely from the Kobweb port (not gated behind a dev-only
  flag) — the simplest option, and consistent with the mock's own "remove before deploy" note.
- Fonts, tokens, and overlays already established in Phase 0 (specs/001-foundation-setup) are not
  revisited here except as the base the 404 page and nav header must render on.
- The 404 page's not-found message is real user-facing prose (unlike the technical-label-only
  placeholder pages in FR-012), so it MUST render in both English and Polish via the same
  Language State mechanism as FR-008/FR-009 — not stated as a separate FR because it follows
  directly from constitution Principle III's general "every user-facing page" rule, not from a
  requirement specific to this feature.
