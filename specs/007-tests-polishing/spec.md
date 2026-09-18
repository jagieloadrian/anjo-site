# Feature Specification: Polish + Automated Test Suite

**Feature Branch**: `feature-7/tests-polishing`

**Created**: 2026-09-18

**Status**: Draft

**Input**: User description: "Phase 6 — Polish: close out ROADMAP.md Faza 6 (F030-F033) —
per-page SEO meta/OG tags, a full accessibility pass (alt text, complete reduced-motion coverage,
contrast), and a go/no-go call on the two optional items (privacy-friendly analytics, self-hosted
fonts instead of the Google Fonts CDN). Also introduce the project's first automated test suite:
Kotlin/JS unit tests (kotlin.test) for pure logic (language detection, bilingual string
resolution, JSON mapping, slug lookup), and a Playwright UI/browser/e2e suite running against the
real static export, covering all routes, the dynamic project page, the language toggle, reduced
motion, and the three mobile breakpoints — replacing the manual mobile-check.html review. All new
test commands wired into CI. Maps to ROADMAP.md F030-F035."

## Clarifications

### Session 2026-09-18

- Q: Do FR-006/FR-007 (analytics, self-hosted fonts) include implementing the chosen option
  within this phase, or only recording the decision for a future phase? → A: If the decision is
  "implement now", the implementation happens within this same phase; if "deferred", only the
  reasoning is recorded and no code changes are made for that item.
- Q: How are the alt-text (FR-003) and contrast (FR-005) accessibility checks verified? → A: An
  automated `axe-core` scan integrated into the same Playwright suite (Story 3), run against every
  page, failing CI on any violation — not a one-off manual audit.
- Q: Does every page need its own `og:image`, or is text-only OG (title/description/type)
  sufficient? → A: Every page gets an `og:image` — project detail pages reuse that project's
  existing `coverImageUrl` (from `projects.json`); pages with no natural image of their own
  (Home, About, Projects grid, Trophies, Contact, CV, 404) fall back to one static, site-wide
  banner image.
- Q: What `axe-core` ruleset should the accessibility scan (FR-003/FR-005/FR-017) run with? → A:
  Only the `wcag2a` + `wcag2aa` tags — exactly what FR-005's WCAG AA promise covers, not the
  broader opinionated `best-practice` rule set.
- Q: What threshold makes FR-011's "console error" check fail? → A: Only `console.error`
  entries and unhandled JS exceptions — `console.warn` and other warnings are ignored, to keep
  the check deterministic and not flaky on non-fatal noise.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Shared links and search results show the right page (Priority: P1)

As the site maintainer, when I or anyone else shares a link to any page of the site (Home, About,
Projects, a specific project, Trophies, Contact, CV), the preview shown by search engines, social
platforms, and chat apps reflects that specific page — not a generic site-wide description.

**Why this priority**: The site currently has only one shared `description.set()`, so every page
looks identical when shared or indexed. This is the most visible, most immediately embarrassing
gap for a portfolio site whose whole purpose is being shared with recruiters/employers.

**Independent Test**: Paste a link to at least three different pages (e.g. `/`, `/projects/{a
slug}`, `/cv`) into a link-preview tool (or view page source) and confirm each shows a distinct,
accurate title and description.

**Acceptance Scenarios**:

1. **Given** any page of the site, **When** its HTML is inspected or shared, **Then** it shows a
   title and description specific to that page's content, not the generic site-wide default.
2. **Given** a page is shared on a platform that reads Open Graph tags, **When** the preview is
   generated, **Then** it shows that page's own title/description/image instead of no preview or
   a wrong one.
3. **Given** a project detail page, **When** its Open Graph tags are inspected, **Then**
   `og:image` matches that project's `coverImageUrl`; **given** any other page, **when** its
   Open Graph tags are inspected, **then** `og:image` is the shared site-wide banner.

---

### User Story 2 - Site is usable with assistive tech and reduced motion (Priority: P1)

As a visitor using a screen reader, keyboard navigation, or a system-level "reduce motion"
setting, every page and component gives me the information and comfort those settings promise —
no missing image descriptions, no decorative animation that reduced-motion should have suppressed,
no text that's hard to read against its background.

**Why this priority**: Phase 3 built accessibility into components as a baseline (alt text
required by the type system, ARIA on interactive elements) but this is the first phase that
actually audits the *assembled* pages end-to-end rather than trusting each component in
isolation — gaps only show up once components are composed on real pages.

**Independent Test**: Run an accessibility audit (e.g. axe or equivalent) against each exported
page, and manually toggle `prefers-reduced-motion` and both color modes (light/dark) to confirm
no decorative animation persists and text stays readable.

**Acceptance Scenarios**:

1. **Given** any page with an image or icon conveying meaning, **When** audited, **Then** every
   such element has accurate alt text (decorative elements are marked non-informative, not just
   left without an audit).
2. **Given** `prefers-reduced-motion: reduce` is set, **When** any page is viewed, **Then** no
   decorative animation (scanline, glitch, typing effect, cursor blink, or any other motion added
   since Phase 3) plays.
3. **Given** any page in either light or dark color mode, **When** text is checked against its
   background, **Then** it meets WCAG AA contrast (4.5:1 for normal text, 3:1 for large text).

---

### User Story 3 - Regressions are caught automatically, before merge (Priority: P2)

As the site maintainer, when I change a component, a page, or a data file, an automated test suite
tells me — before I merge — whether I broke the language toggle, a route, the dynamic project
page, reduced-motion behavior, or how a page looks on a phone-sized screen, instead of me finding
out after it's already live.

**Why this priority**: Every prior phase (Faza 0-4) shipped with zero automated tests
(specs/SUMMARY.md records this as a deliberate, now-reversed decision) and relied entirely on the
maintainer manually re-checking `docs/handoff/mobile-check.html` — a step the roadmap itself notes
was "nowhere enforced" (ROADMAP.md F024a). This story replaces manual trust with an automatic
gate. Depends on User Stories 1-2 existing as things worth protecting, but is independently
valuable and independently testable on its own.

**Independent Test**: Deliberately break one thing (e.g. make the language toggle a no-op, or
remove a route's content) on a branch, open a pull request, and confirm CI fails on that specific
change without any manual step.

**Acceptance Scenarios**:

1. **Given** a change to `Lang.kt`'s language-detection logic or `BilingualString`/
   `BilingualTimelineItem.resolve()`, **When** the unit test suite runs, **Then** it fails if the
   behavior no longer matches the documented rules (e.g. `pl`-prefixed browser locale → Polish).
2. **Given** a change to the JSON-parsing/mapping logic in `Trophies.kt` or `Projects.kt`, **When**
   the unit test suite runs, **Then** it fails if a known input no longer maps to the expected
   parsed shape.
3. **Given** a request for an unknown `/projects/{slug}`, **When** the unit test suite runs,
   **Then** it confirms the lookup falls back to the documented "not found" behavior instead of
   crashing or silently rendering nothing.
4. **Given** a real static export of the site served locally, **When** the browser test suite
   runs, **Then** it visits all seven static routes and the 404 page and fails if any of them
   fails to load, throws an unhandled JS exception, or logs a `console.error` (warnings are
   ignored).
5. **Given** the same real static export, **When** the browser test suite runs, **Then** it visits
   one dynamic project page and confirms an unknown slug falls back to the 404 page.
6. **Given** the same real static export, **When** the browser test suite runs, **Then** it
   toggles the language switch and fails if visible text does not change language.
7. **Given** the same real static export with `prefers-reduced-motion: reduce` emulated, **When**
   the browser test suite runs, **Then** it fails if decorative animation is still present.
8. **Given** the same real static export, **When** the browser test suite runs at 390px, 430px,
   and 768px viewport widths, **Then** it fails if any page overflows horizontally or has a touch
   target smaller than the documented 48px minimum.
9. **Given** any pull request or push, **When** CI runs, **Then** it runs the existing
   `refresh-trophies` tests, the new unit tests, and the new browser test suite automatically,
   without a manual trigger.
10. **Given** a page with a missing alt attribute or insufficient contrast, **When** the automated
    `axe-core` scan runs as part of the browser test suite, **Then** CI fails on that violation.

---

### User Story 4 - Maintainer makes an informed call on the two optional items (Priority: P3)

As the site maintainer, I get a clear, documented recommendation on whether to add
privacy-friendly analytics and whether to self-host fonts instead of using the Google Fonts CDN,
so I can decide once instead of the items sitting as perpetual "maybe later" roadmap noise.

**Why this priority**: Both items are explicitly marked optional in ROADMAP.md; lowest priority
because the site functions correctly either way — this is a decision-quality improvement, not a
functional gap.

**Independent Test**: Read the resulting recommendation for each item and confirm it states a
clear decision (do it now / defer, with a reason) rather than leaving the question open.

**Acceptance Scenarios**:

1. **Given** the analytics question, **When** evaluated against Principle VIII (YAGNI on
   dependencies) and the zero-backend constraint (Principle I), **Then** a clear decision is
   recorded: implement now, or explicitly deferred with a stated reason.
2. **Given** the self-hosted-fonts question, **When** evaluated the same way, **Then** a clear
   decision is recorded: implement now, or explicitly deferred with a stated reason.

---

### Edge Cases

- What happens when the browser test suite runs against a page that legitimately has no dynamic
  content differences between light/dark mode or EN/PL (e.g. the 404 page)? It is still covered by
  the route-loads-without-errors check (Story 3, Scenario 4), just not by the language-toggle or
  contrast-per-theme checks that need real content to compare.
  - What happens when a Playwright run is flaky (e.g. font loading timing) on an otherwise correct
  page? The suite must fail deterministically on real regressions and not be tuned around a
  known-flaky wait; any retry/wait logic added to reduce flakiness must not mask an actual broken
  state (e.g. waiting for network idle, not swallowing errors).
- What happens to the reduced-motion and contrast checks in dark mode specifically, since
  `Theme.kt` (added ad hoc in Phase 4, outside its original scope) introduced a light/dark toggle
  the original mock never had? Both modes are in scope for the accessibility pass and for any
  automated checks that depend on color (contrast), since both are live, reachable states of the
  shipped site.
- What happens if the SEO/OG description for a page has no natural distinct content to summarize
  (e.g. a future empty state)? Out of scope — all current pages have enough real content to derive
  a distinct description from.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: Every page MUST expose a page-specific `<title>` and meta description, built on top
  of the existing base `description.set()` in `site/build.gradle.kts`, instead of every page
  sharing one generic value.
- **FR-002**: Every page MUST expose Open Graph tags (`og:title`, `og:description`, `og:type`,
  `og:image`) specific to that page, so link previews on social/chat platforms show accurate,
  page-specific content. A project detail page's `og:image` MUST use that project's existing
  `coverImageUrl` (`projects.json`); every other page MUST fall back to one static, site-wide
  banner image.
- **FR-003**: Every image or icon that conveys meaning MUST have accurate alt text, verified across
  all assembled pages (not just at the component level, where this was already enforced in Phase
  3) via an automated `axe-core` scan (see FR-017).
- **FR-004**: `prefers-reduced-motion: reduce` MUST suppress every decorative animation on every
  page, including any animation added after Phase 3's original coverage.
- **FR-005**: Text MUST meet WCAG AA contrast (4.5:1 normal text, 3:1 large text) against its
  background in both light and dark color mode, using only the existing `SiteTokenStyles.kt` tokens (no
  new one-off colors introduced to pass a contrast check), verified via the same automated
  `axe-core` scan (see FR-017).
- **FR-006**: A documented, explicit decision (implement now or deferred-with-reason) MUST be
  recorded for privacy-friendly analytics, evaluated against the project's YAGNI-on-dependencies
  and zero-backend principles. If the decision is "implement now", the implementation MUST be
  completed within this phase; if "deferred", no code changes are made for this item.
- **FR-007**: A documented, explicit decision (implement now or deferred-with-reason) MUST be
  recorded for self-hosted fonts as a replacement for the Google Fonts CDN. If the decision is
  "implement now", the implementation MUST be completed within this phase; if "deferred", no code
  changes are made for this item.
- **FR-008**: An automated unit test suite MUST cover `Lang.kt`'s browser-language detection and
  `BilingualString`/`BilingualTimelineItem.resolve()` against known inputs and expected outputs.
- **FR-009**: An automated unit test suite MUST cover the JSON-parsing/mapping logic in
  `Trophies.kt` and `Projects.kt` against known input shapes.
- **FR-010**: An automated unit test suite MUST cover the `/projects/{slug}` lookup in
  `pages/projects/Slug.kt`, including the fallback behavior for an unknown slug.
- **FR-011**: An automated browser test suite MUST load all seven static routes (Home, About,
  Projects, Trophies, Contact, CV, 404) against a real static export and fail on load failure, an
  unhandled JS exception, or a `console.error` entry (`console.warn` and other non-error console
  output are ignored).
- **FR-012**: The automated browser test suite MUST verify at least one dynamic
  `/projects/{slug}` page renders correctly and that an unknown slug falls back to the 404 page.
- **FR-013**: The automated browser test suite MUST verify the EN/PL language toggle changes
  visible page text.
- **FR-014**: The automated browser test suite MUST verify that decorative animation is suppressed
  when `prefers-reduced-motion: reduce` is emulated.
- **FR-015**: The automated browser test suite MUST verify, at 390px, 430px, and 768px viewport
  widths, that no page overflows horizontally and no interactive element falls below the
  documented 48px touch-target minimum — this automated check replaces the manual
  `docs/handoff/mobile-check.html` review as the definition of done for those breakpoints.
- **FR-016**: All automated test commands (the existing `refresh-trophies` tests, the new unit
  tests, and the new browser test suite) MUST run automatically in CI on every push and pull
  request, not only when run manually by the maintainer.
- **FR-017**: The automated browser test suite MUST run an automated accessibility scan (`axe-core`,
  restricted to the `wcag2a` and `wcag2aa` rule tags) against every page in both light and dark
  color mode and fail on any violation, covering both the alt-text (FR-003) and contrast (FR-005)
  checks as part of the same CI gate as the rest of the browser suite (Story 3), not as a separate
  manual step.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: 100% of the site's static pages (all seven routes plus at least one project detail
  page) show a distinct, accurate title and description when their link is shared or their source
  is inspected.
- **SC-002**: An accessibility audit run against every exported page reports zero missing alt
  attributes and zero elements still animating under `prefers-reduced-motion: reduce`.
- **SC-003**: 100% of sampled text/background combinations across both color modes meet WCAG AA
  contrast.
- **SC-004**: A change that breaks the language toggle, breaks a route, or breaks one of the three
  documented breakpoints causes an automated CI failure on the pull request, with zero manual
  verification steps required to catch it.
- **SC-005**: The full automated test suite (unit + browser) completes in CI in under 10 minutes.
- **SC-006**: The manual `docs/handoff/mobile-check.html` review is no longer part of this
  project's definition of done for any page — it is fully superseded by the automated breakpoint
  checks.
- **SC-007**: Both optional items (analytics, self-hosted fonts) have a recorded decision; neither
  is left as an open question after this phase.

## Assumptions

- The Playwright browser suite runs against Chromium only (not also Firefox/WebKit) — the
  reasonable minimum for a personal portfolio site with no reported cross-browser bugs; broader
  browser coverage can be added later if a real cross-browser issue surfaces.
- "Real static export" throughout means the actual `kobwebExport --layout static` output served
  locally (e.g. a plain static file server), not the Kobweb dev server — matching Constitution
  Principle VII (static export is the definition of done, not a dev-server run).
- The 10-minute CI budget (SC-005) is a reasonable default for a site this size, not a
  maintainer-specified number; it can be revisited if the suite grows.
- Introducing Playwright and `@axe-core/playwright` as new dependencies is treated as justified
  under Constitution Principle VIII (YAGNI on dependencies) because no stdlib/already-installed
  tool can drive a real browser or scan rendered accessibility violations — these are the two new
  dependencies expected to need explicit justification in `/speckit-plan`'s Constitution Check /
  Complexity Tracking (axe-core is added to the same Playwright suite, not a second, separate
  test runner).
- `kotlin.test` and Node's built-in `node:test` are not new dependencies — `kotlin.test` ships
  with the Kotlin Multiplatform plugin already in use, and `node:test` is already the pattern used
  by `scripts/refresh-trophies/index.test.mjs`.
- SEO/OG work (FR-001, FR-002) reuses per-page content already defined for each page (titles,
  descriptions already implied by existing page content) rather than requiring new copywriting —
  if a page's existing content doesn't cleanly yield a description, writing one is in scope as
  part of FR-001, not a separate content phase.
- The one new static asset FR-002 requires — the site-wide `og:image` fallback banner (for every
  page except project detail pages, which reuse an existing `coverImageUrl`) — is a single simple
  image (e.g. reusing/adapting an existing brand asset like `icon-512.png`, not a new per-page
  design), created as part of this phase's scope, not a separate design phase.
