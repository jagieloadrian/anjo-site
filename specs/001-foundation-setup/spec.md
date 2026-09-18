# Feature Specification: Foundation Setup

**Feature Branch**: `001-foundation-setup`

**Created**: 2026-09-17

**Status**: Draft

## Clarifications

### Session 2026-09-17

- Q: GitHub Pages publish mode — project page under a repository path, or user/organization root
  page? → A: User/organization root page. Site is published at the root domain
  (`jagieloadrian.github.io`), base path is `/`, not a repository subpath. This repo (`anjo-site`)
  is not itself the Pages source under this mode — its static export output is what gets published
  to the dedicated `jagieloadrian.github.io` repository (the exact publish mechanism, e.g. a
  cross-repo CI push, is a planning-level detail, not a spec-level one).
- Q: Does this phase touch the existing Index page, or is verification export-only (no visual
  page check)? → A: The existing Index page gets a minimal placeholder update so it renders on
  the new global tokens/styles/overlays — no real content or navigation is added, but the
  foundation becomes visually verifiable end-to-end rather than only inspectable in exported CSS.

**Input**: User description: "Phase 0 — Foundation: get the Kobweb project (site/) ready to receive
the ported UI, matching the design tokens and asset setup already proven in docs/handoff/ (static
HTML/CSS mock), before any page or component work starts. Scope: GitHub Pages base path decision
(F001), design tokens ported from the mock's five CSS custom properties into the site theme (F002),
Archivo + JetBrains Mono fonts (F003), and the mock's global/structural styles including its three
decorative overlay layers, respecting reduced-motion (F004)."

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visitor sees the on-brand visual foundation (Priority: P1)

A visitor opens the site (even before any real page content exists) and sees the target dark,
high-contrast visual identity — correct background and accent colors, correct typography — instead
of the generic default look of a freshly generated Kobweb project.

**Why this priority**: Every later page and component is built on top of this visual foundation.
Nothing else in the roadmap can be verified as "on-brand" until this is in place.

**Independent Test**: Export the site (`kobweb export --layout static`) and open the exported
output — including the existing Index page, updated with placeholder content only so it renders
on the new foundation. The page background, text color, and font rendering match the target
palette and typefaces, with no leftover default Kobweb styling visible.

**Acceptance Scenarios**:

1. **Given** the exported site, **When** a visitor loads any page, **Then** the background,
   primary text, and accent colors match the five target design tokens (base background, ink/
   text, and pink/cyan/red accents).
2. **Given** the exported site, **When** a visitor loads any page, **Then** headings and body
   text render in the target display typeface and monospace typeface rather than a system
   fallback font (allowing brief fallback only during font load).

---

### User Story 2 - Visitor with reduced-motion preference sees a calmer page (Priority: P2)

A visitor whose system is set to reduce motion opens the site and does not see the decorative
scanline overlay that the default (motion-tolerant) experience includes.

**Why this priority**: Respecting this preference is a standing accessibility commitment for the
whole site (not just this phase) and must be true from the very first styling pass, since later
phases build more motion (typing effect, glitch text) on top of the same convention.

**Independent Test**: With the OS/browser reduced-motion preference enabled, load the exported
site and confirm the scanline overlay is absent while the page otherwise renders normally.

**Acceptance Scenarios**:

1. **Given** a visitor with reduced-motion enabled, **When** they load any page, **Then** the
   scanline overlay is not rendered.
2. **Given** a visitor with reduced-motion enabled, **When** they load any page, **Then** the
   static vignette and background grid overlays still render (they carry no motion of their own).

---

### User Story 3 - Site is reachable at its published GitHub Pages address (Priority: P1)

Once exported and deployed, every internal link, asset path, and font reference on the site
resolves correctly at the actual published GitHub Pages URL: the user/organization root page
(`jagieloadrian.github.io`, base path `/`).

**Why this priority**: If the base path is wrong, the exported site is broken at the one address
it will actually be visited at, even though it may look correct when opened locally — this is the
kind of failure that only shows up after deploy.

**Independent Test**: Export the site with the chosen base path configured, serve the exported
output from that same path locally (or via a preview deploy), and confirm no broken asset/link
(404) occurs.

**Acceptance Scenarios**:

1. **Given** the site exported with its configured base path, **When** it is served from that
   exact path, **Then** all styles, fonts, and internal links load without a 404.
2. **Given** the site exported with its configured base path, **When** it is served from the
   root path instead (mismatch), **Then** the mismatch is detectable before real deployment (e.g.
   via the export/verification step), not discovered only after publishing.

### Edge Cases

- What happens if a visitor's browser fails to load the CDN-hosted fonts (offline, blocked
  request, slow network)? The page must remain readable with a sensible fallback font rather than
  invisible or unstyled text.
- What happens if the reduced-motion preference changes while the page is already open (e.g. the
  visitor toggles the OS setting mid-session)? Out of scope for this phase — evaluating the
  preference on page load is sufficient; live re-evaluation is not required.
- What happens on a very narrow viewport or a very short viewport where the fixed overlays might
  visually interfere with content? Overlays must not block interaction (they carry no pointer
  events) or obscure text to the point of being unreadable.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The site MUST render its background, text, and three accent colors using a single,
  centrally defined set of design tokens rather than values repeated per page or component.
- **FR-002**: The site's central design tokens MUST reproduce the same five colors already
  validated in the design mock (one background, one primary text/ink color, and three accents).
- **FR-003**: The site MUST load a display typeface for headings/body text and a monospace
  typeface for code/terminal-style content, both in the weights the mock actually uses.
- **FR-004**: The site MUST apply a global reset/base style layer so that default browser and
  framework styling does not visibly leak through (e.g. default margins, default link colors,
  default font).
- **FR-005**: The site MUST render the mock's three decorative background overlay layers
  (scanline texture, vignette, background grid) as part of the base page chrome.
- **FR-006**: When the visitor's system requests reduced motion, the site MUST omit the scanline
  overlay while still rendering the vignette and grid overlays.
- **FR-007**: Decorative overlay layers MUST NOT intercept clicks/taps or otherwise block
  interaction with real page content.
- **FR-008**: The site MUST resolve to a root base path (`/`), matching its publish target as a
  GitHub user/organization root page (`jagieloadrian.github.io`) — the unset/default configuration
  value already means root, so this is satisfied by leaving that setting unchanged, not by writing
  a new value — and every generated internal reference (assets, links) MUST resolve correctly
  under that root path.
- **FR-009**: A successful static export MUST be achievable and MUST be the basis for verifying
  FR-001 through FR-008 — verification against the dev server alone is not sufficient.
- **FR-010**: The site's existing Index page MUST render using the new global design tokens,
  typefaces, and overlay chrome as a placeholder, so the foundation is visually verifiable on a
  real page, without introducing real page content or navigation (those remain out of scope for
  this phase).

### Key Entities

- **Design Token Set**: The five named colors (background, ink/text, and three accents) that
  every visual element on the site draws from; single source of truth for the whole site, not
  per-feature data.
- **Typeface Asset**: A font family (display or monospace) with the specific weights actually
  used, and where it's sourced from (CDN vs. bundled) — an asset dependency, not user data.
- **Base Path Setting**: The one configuration value describing where the exported site is
  actually served from, consumed by every internal reference the export produces.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of pages in the exported static output use the five target design tokens for
  background/text/accent color — zero instances of an unstyled or default-template color.
- **SC-002**: A visitor on a typical broadband connection sees the target typefaces rendered
  (not a fallback font) within the first visible paint of any page, with zero layout shift once
  the intended font finishes loading.
- **SC-003**: 100% of internal asset and link references resolve without a 404 when the exported
  site is served from its configured, real publish path.
- **SC-004**: A visitor with reduced-motion enabled never sees the scanline overlay, verified on
  every page, with zero exceptions.
- **SC-005**: The static export command completes successfully with zero errors, and this
  passing export — not just a running dev server — is what any later phase can build on.

## Assumptions

- GitHub Pages publish mode is a user/organization root page (`jagieloadrian.github.io`, base
  path `/`) — resolved in Clarifications above. The mechanism that gets this repo's static export
  output published to that separate root-page repository is a planning-level concern, not decided
  here.
- Fonts are loaded from a CDN for this phase (fastest path to a correct on-brand result);
  self-hosting the font files to remove the external request is acceptable as later, separate
  work and is not required for this phase to be considered done.
- This phase delivers the visual foundation (colors, type, base layout chrome, overlays) only,
  demonstrated on the existing Index page as a placeholder. It does not include navigation,
  additional routed pages, or any real content — those are later phases that will be built on top
  of what this phase establishes.
- "Visitor" in the scenarios above also covers the site's own maintainer previewing the export
  locally — there is no distinct authentication or role system in this static, backend-less site.
