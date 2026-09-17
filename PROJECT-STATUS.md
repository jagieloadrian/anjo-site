# Project Status

## Completed work

- Fazy 0–2 (fundament, layout/routing, komponenty) — done, see specs/001-003.
- Faza 3 — Strony (specs/004-pages): all six routes real (`/`, `/about`, `/projects` + per-project
  `/projects/{slug}`, `/trophies`, `/contact`, `/cv`). `kobweb export -PkobwebExportLayout=STATIC`
  verified green — static file exists for every route including all 4 project slugs.
- **Visual rewrite (this session, post-Faza-3 correction)**: the site now renders the mock
  (`docs/handoff/index.html`/`styles.css`/`app.js`) faithfully. The original Faza 3 implementation
  approximated the mock's look with hand-rolled Silk `CssStyle`s and dropped most of Home's content
  (only shipped the hero, not the About/Stack/Trophies/Contact teaser bands the mock's
  `data-screen="home"` also contains) — caught when the user actually looked at it in a browser and
  it didn't resemble the mock at all. See decisions below for the fix.

## Important decisions

- **Styling now comes from the mock's own stylesheet, not reinvented Kotlin CSS.**
  `docs/handoff/styles.css` is copied verbatim (plus one added `@media print` block, the mock has
  none) to `site/src/jsMain/resources/public/styles.css` and linked in `<head>`
  (`site/build.gradle.kts`). Every page is built with raw `org.jetbrains.compose.web.dom.*` tags
  (`Div`, `Section`, `H1`, `Link`, …) carrying the mock's exact class names (`.band`, `.split`,
  `.term`, `.card`, `.tl`, `.stat`, `.tag`, …), not Silk `CssStyle`. This replaced almost all of
  `AppStyles.kt`/`SiteTheme.kt` (both deleted — dead code once nothing read `SitePalette`/
  `ColorMode` anymore) and every Phase 2 shared component's internals (`Tag`, `StatRow`→
  `StatCell`/`Fact`, `GameCover`, `TrophyRow`, `ProjectCard`, `TimelineEntry`, `Terminal`,
  `ContactPrompt`, `NavHeader`, `Footer`) — same public shape where reasonable, markup rewritten.
  **Why**: reinventing the mock's design tokens/spacing/borders by hand in Kotlin was the direct
  cause of three separate bugs this session (a `Box`-vs-grid overlap on `/projects`, invisible
  print text, sub-48px touch targets) and still didn't look like the mock. The mock's own CSS is
  the single source of truth for visual design; Kotlin now only owns routing, state, i18n and
  fetch — matching what `app.js`'s own top comment says the port should do ("three things only:
  route switching, the boot typing effect, the contact prompt").
- Mock's `<button data-route="...">` client-side "SPA" navigation is real Kobweb multi-page routing
  instead (decided back in specs/002-layout-routing, unchanged) — every internal nav element is a
  real `<a>` via Kobweb's `Link`. One consequence found this session: the mock's `.btn` relies on a
  bare `<button>`'s default `display: inline-block`; a bare `<a>` defaults to `display: inline`,
  which silently drops `margin-top`/`margin-bottom` and caused a real overlap bug. Fixed with one
  added rule, `.btn, .btn--link { display: inline-block; }`, in our copy of the stylesheet.
- Project detail = real Kobweb dynamic route (`@Page("{}")` on `pages/projects/Slug.kt`), **plus**
  `site/build.gradle.kts`'s `export { addExtraRoute(...) }` for each slug — Kobweb's exporter
  silently skips any route containing `{}` otherwise. Slug list is duplicated (by hand) between
  `Projects.kt`'s `projectEntries` and `build.gradle.kts`; keep them in sync when adding a project.
  Per an earlier clarification, every project gets its own detail page (the mock only gives its one
  "featured" project a detail page and points the rest at their repo) — kept as-is, just re-skinned
  to the mock's `.card`/`.sheet`/`.slot` look.
- Bilingual content: `BilingualString` fields resolved to plain `String` at render time, same as
  before. Nav/page prose is bilingual; tag/skill/timeline/CV labels stay English-only in both
  languages, matching the mock's own `data-lang-block` usage (it never wraps those).
- `ContactPrompt` rewritten on raw `<input>`/`<button>` (no Silk `TextInput`/`Button` — nothing
  needs Silk's theming anymore) inside the mock's `.term`/`.term-input` chat-log markup.
- Deleted dead code as a consequence of the pivot: `AppStyles.kt`, `SiteTheme.kt`, `IconButton.kt`
  (NavHeader's old hamburger/color-mode/design-notes chrome is gone — the mock has none of that,
  one dark theme, no toggle), `MarkdownLayout.kt` (already-unused Kobweb template leftover).
- `PageLayout.kt` no longer centers content in a `max-width` column — the mock's bands are
  full-width with internal grid-gap dividers touching the viewport edge; a centered column never
  matched it.

## Verified (this session, real browser + real export)

- All 6 pages + project detail screenshotted against the mock's own rendered sections — matches.
- `docs/handoff/mobile-check.html` widths (390/430/768): no horizontal overflow on any page.
- `/cv` print preview: white background, dark text, nav/footer/decorative overlays hidden.
- `kobweb export -PkobwebExportLayout=STATIC`: all 10 pages export, `styles.css` included.

## Next steps

- Faza 4 — Dane: nightly GitHub Action generating the real `trophies.json` via `psn-api`
  (ROADMAP F025); externalize `projects.json`/`skills.json` if desired (F026).
- Print-preview contrast on CV's bullet lists/skills values is a bit low (mock's `--dim`/`--mut`
  grays, not fully overridden by the added print block) — legible but could be darkened further.
