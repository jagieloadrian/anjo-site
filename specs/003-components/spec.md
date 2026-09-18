# Feature Specification: Shared UI Components

**Feature Branch**: `003-components`

**Created**: 2026-09-17

**Status**: Draft

**Input**: User description: "Phase 2 — Components: build the shared Silk UI components consumed
by every page in Phase 3 (Terminal boot-typing effect, ProjectCard, TimelineEntry, StatRow,
GameCover, TrophyRow, Tag(variant)), on top of Phase 0 design tokens (SiteTheme.kt) and Phase 1
patterns (NavHeader.kt, LangProvider). Content is passed in via parameters/data classes, never
hardcoded. Cross-cutting: 48px minimum touch target below the 720px breakpoint, clamp()-based
fluid typography, full prefers-reduced-motion coverage where animation exists. Out of scope: the
pages that consume these components (Phase 3), JSON data sources (Phase 4), routing."

## Clarifications

### Session 2026-09-17

- Q: Do these components need full accessibility baseline now (required `alt` text params, ARIA
  roles/labels on interactive elements), or is that fully deferred to the Phase 6 a11y pass
  (F031)? → A: Full baseline now — required `alt` text on every image-bearing component, and
  ARIA roles/labels on every interactive element, shipped as part of this phase rather than
  deferred.
- Q: Is `GameCover` embedded inside `TrophyRow` by fixed composition, or are they independent
  components a page combines as needed? → A: Independent — `GameCover` and `TrophyRow` are
  separate, standalone components; whichever page uses them decides the layout.
- Q: Is `ProjectCard` a purely presentational block, or a self-contained clickable
  container? → A: Self-contained clickable container — the whole card is the click/navigation
  target, taking an `href` parameter rather than a page wrapping it or an inner "view details"
  link.
- Q: Is `Tag` always a static label, or always a clickable link? → A: Always clickable — `Tag`
  requires an `href` parameter (e.g. navigating to a filtered view for that tag), not an optional
  add-on and not a purely static label.
- Q: Are `GameCover` and `TrophyRow` interactive (clickable, like `ProjectCard`/`Tag`) or purely
  static display? → A: Both interactive — `GameCover` and `TrophyRow` also require a caller-
  supplied `href`, consistent with `ProjectCard` and `Tag`.
- Q: `href`-only or does `Tag`/`ProjectCard`/`GameCover`/`TrophyRow` also need an `onClick`-style
  escape hatch (e.g. client-side filtering with no navigation)? → A: `href`-only — all four
  clickable components navigate via Silk's `Link` (research.md §3); a caller that needs
  client-side filtering does so by giving `Tag` an `href` to a filtered route/URL, not by the
  component supporting a second interaction mechanism. Narrows the two prior answers above, which
  had left `onClick` open as an alternative.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Visitor sees content-bearing components render consistently (Priority: P1)

A visitor viewing any page built in Phase 3 sees projects, stats, and trophies presented through
a consistent, on-brand visual language — the same card, row, and tag styling everywhere the same
kind of content appears, regardless of which page it's on.

**Why this priority**: `ProjectCard`, `StatRow`, `GameCover`, `TrophyRow`, and `Tag` are the
components every content-bearing page (Projects, CV, Trophies) depends on. Without them, Phase 3
has nothing to build pages out of. They carry no animation or timing logic, making them the
lowest-risk, highest-leverage slice to ship first.

**Independent Test**: Each of these five components can be built, code-reviewed, and shipped
without any of the others or without Phase 3/4 existing — none references another component, a
page, or a JSON data source. Visual/browser verification against representative sample data is
done once, when a real Phase 3 page first wires a given component in, rather than through
throwaway scratch-page wiring created solely to preview it in isolation first.

**Acceptance Scenarios**:

1. **Given** a project's title, cover image, tags, and short description, **When** `ProjectCard`
   renders it, **Then** all four pieces of content are visible and legible.
2. **Given** a label and a value, **When** `StatRow` renders it, **Then** both are displayed as a
   single aligned row.
3. **Given** a trophy's tier, name, and earned date, **When** `TrophyRow` renders it, **Then** all
   three are visible, a trophy with no earned date (not yet unlocked) still renders without error,
   and the row is a clickable element with its own navigation target.
4. **Given** the same `Tag` component with different `variant` values, **When** it renders,
   **Then** each variant is visually distinct (e.g. tech-stack tag vs. status tag) while sharing
   the same underlying shape, and every rendered tag is a clickable element with its own
   navigation target.
5. **Given** a project or game with no cover image supplied, **When** `ProjectCard` or
   `GameCover` renders it, **Then** a placeholder is shown instead of a broken image, and the
   component remains a clickable element with its own navigation target.

---

### User Story 2 - Visitor sees the boot-typing terminal intro (Priority: P2)

A visitor landing on a page that uses the terminal effect sees boot-sequence text type itself out
character by character, evoking a retro terminal boot — unless they've asked their system to
reduce motion, in which case they see the final text immediately with no animation.

**Why this priority**: `Terminal` is self-contained, decorative, and used by fewer pages than the
Phase 1 components, but it's the only component in this phase carrying real timing/animation
logic and its own reduced-motion branch, so it's tested separately from the static-content
components in User Story 1.

**Independent Test**: Render `Terminal` with sample boot text under both a normal motion
preference and a `prefers-reduced-motion: reduce` preference, and confirm the typing animation
plays in the first case and is entirely skipped (full text shown instantly) in the second.

**Acceptance Scenarios**:

1. **Given** a visitor with no reduced-motion preference, **When** `Terminal` mounts, **Then** the
   supplied boot text appears progressively, character by character.
2. **Given** a visitor with `prefers-reduced-motion: reduce` set, **When** `Terminal` mounts,
   **Then** the full boot text appears immediately, with no character-by-character animation.
3. **Given** `Terminal` is currently mid-animation, **When** the visitor's reduced-motion
   preference changes, **Then** the component respects the new preference rather than continuing
   a now-unwanted animation.

---

### User Story 3 - Visitor reads a chronological history entry (Priority: P3)

A visitor viewing a CV or About-style page sees a single point in a timeline (a role, an
education entry, a milestone) presented with its date, title, and description in a scannable,
consistent format.

**Why this priority**: `TimelineEntry` is used by fewer Phase 3 pages than the Priority 1
components (primarily CV/About) and has no dependents within this phase, so it can slot in last
without blocking anything else.

**Independent Test**: `TimelineEntry` can be built and shipped with zero dependency on any other
component or page. Its actual rendered output is confirmed the first time a real Phase 3 page
wires it in, not via standalone scratch-page wiring.

**Acceptance Scenarios**:

1. **Given** a date, a title, and a description, **When** `TimelineEntry` renders it, **Then** all
   three are visible and clearly associated with one chronological entry.

---

### Edge Cases

- What happens when `ProjectCard` or `GameCover` receives no image (missing/empty cover)? →
  Placeholder graphic shown, no broken-image icon, no layout shift.
- What happens when `TrophyRow` receives no earned date? → Renders as a locked/unearned trophy
  state rather than showing an empty or malformed date.
- What happens when `StatRow` receives an empty value? → Row still renders (label visible), no
  crash, no dangling separator.
- What happens when `Tag` receives an unrecognized `variant`? → Falls back to a default/neutral
  visual style rather than failing to render.
- What happens when the viewport is narrower than 720px? → Every interactive element across all
  components (tag as link, card as link, etc.) maintains a minimum 48px touch target.
- What happens when `Terminal`'s host page has very long boot text? → Typing animation still
  completes and the reduced-motion fallback still shows the full text immediately, regardless of
  length.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Every component MUST receive its displayed content exclusively through parameters
  or a data class — no component may hardcode project, trophy, stat, tag, or timeline content.
- **FR-002**: `Terminal` MUST animate its supplied text character by character when the visitor
  has no reduced-motion preference set.
- **FR-003**: `Terminal` MUST display its full supplied text immediately, with no animation, when
  the visitor has `prefers-reduced-motion: reduce` set, and MUST react if that preference changes
  during an in-progress animation.
- **FR-004**: `ProjectCard` MUST display a project's cover image (or placeholder), title, tags,
  and short description, and MUST itself be a single clickable/navigable container (accepting a
  caller-supplied `href`) rather than a purely presentational block a page wraps.
- **FR-005**: `TimelineEntry` MUST display a date, a title, and a description as one associated
  entry.
- **FR-006**: `StatRow` MUST display a label/value pair as a single row, and MUST render without
  error when the value is empty.
- **FR-007**: `GameCover` MUST display a supplied cover image, falling back to a placeholder when
  no image is supplied, and MUST itself be a clickable element requiring a caller-supplied `href`,
  consistent with `ProjectCard` and `Tag`.
- **FR-008**: `TrophyRow` MUST display a trophy's tier/icon and name, and its earned date when
  present, rendering a locked/unearned state when no earned date is supplied. `TrophyRow` MUST
  itself be a clickable element requiring a caller-supplied `href`, consistent with `ProjectCard`
  and `Tag`.
- **FR-009**: `Tag` MUST support multiple visual variants (e.g. tech-stack vs. status) selected via
  a `variant` parameter, and MUST fall back to a default style for an unrecognized variant. `Tag`
  MUST always be a clickable element, requiring a caller-supplied `href` — it is never a purely
  static, non-interactive label.
- **FR-010**: Every interactive element in every component MUST maintain a minimum 48px touch
  target at viewport widths below the 720px breakpoint.
- **FR-011**: Every component's typography MUST scale fluidly via `clamp()`-based sizing rather
  than fixed pixel values or discrete per-breakpoint steps.
- **FR-012**: Every component MUST source its colors, fonts, and spacing exclusively from
  `SiteTheme.kt` design tokens — no inline magic values.
- **FR-013**: Every component MUST accept already-localized text through its parameters and MUST
  NOT contain its own translation or language-detection logic.
- **FR-014**: `ProjectCard` and `GameCover` MUST require a caller-supplied `alt` text parameter
  for their image content — no image may render without accessible alternative text.
- **FR-015**: Every interactive element across all components (card, tag-as-link, etc.) MUST carry
  an appropriate ARIA role and accessible label, not rely on visual styling alone.

### Key Entities

- **ProjectSummary**: A project as shown in `ProjectCard` — cover image (optional), required `alt`
  text for that image (FR-014), title, list of tags, short description, and a navigation target
  (the card's own click/link destination).
- **TimelineItem**: A single chronological entry — date, title, description.
- **StatItem**: A label/value pair shown by `StatRow`.
- **GameCoverImage**: A game's cover artwork reference, with a defined placeholder state when
  absent, required `alt` text for that image (FR-014), plus a navigation target — `GameCover` is
  clickable, like `ProjectCard` and `Tag`.
- **TrophyEntry**: A trophy's tier/type, name, optional earned date (absent = locked/unearned),
  and a navigation target — `TrophyRow` is clickable, like `ProjectCard` and `Tag`.
  Does not embed a `GameCoverImage` — `TrophyRow` and `GameCover` are independent components a
  page composes together when needed.
- **TagVariant**: The set of visual styles `Tag` can render (e.g. tech-stack, status), plus a
  default/fallback style. Every `Tag` instance also carries a navigation target — `Tag` is always
  clickable, never a static label.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: All seven components (Terminal, ProjectCard, TimelineEntry, StatRow, GameCover,
  TrophyRow, Tag) are buildable and shippable with zero dependency on any other component, on a
  Phase 3 page, or on a Phase 4 data source — none references another. Each one's actual rendered
  output is confirmed correct the first time a real Phase 3 page wires it in, not via standalone
  scratch-page wiring built solely to preview it first.
- **SC-002**: A visitor with `prefers-reduced-motion: reduce` sees zero animation frames from
  `Terminal` — full content is visible on first paint.
- **SC-003**: 100% of interactive elements across all components meet the 48px minimum touch
  target on viewports narrower than 720px.
- **SC-004**: A code-level check of all seven components' source finds zero hardcoded color, font,
  or spacing values outside of `SiteTheme.kt` tokens.
- **SC-005**: Any component renders correctly in both Polish and English when given translated
  input, with no component-level code changes required to switch languages.
- **SC-006**: Every image-bearing component fails to compile/render without a supplied `alt`
  text, and a screen reader announces an accessible label for every interactive element.

## Assumptions

- Components are presentational only — none of them fetches its own data; sample/representative
  data is supplied directly by whoever renders them (Phase 3 pages later, ad-hoc previews now).
- The visual reference for each component is the existing static mock in `docs/handoff/`, adapted
  to Silk/Compose HTML idioms rather than reproduced pixel-for-pixel.
- `Terminal`'s caller supplies the boot text content; the component owns only the typing/animation
  timing and the reduced-motion branch, not the copy itself.
- No new testing framework is introduced for this phase; verification is via isolated rendering
  and manual/browser inspection consistent with prior phases.
