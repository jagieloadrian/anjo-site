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

## Completed work (this session, post-pivot refinements)

Eight user-reported items, all implemented and verified in-browser (Playwright) + against a real
`kobwebExport -PkobwebExportLayout=STATIC` (all 10 pages + `projects.json`/`stack.json`/
`trophies.json` present, fetched content correctly pre-rendered into the static HTML):

1. Footer background is now a fixed bright red (`#ff6b52`, literal, not the themed `--red-lt` var
   — that var flips *darker* in light mode, the wrong direction for "brighter"), `Footer.kt`.
2. Projects are now fetched from `resources/public/projects.json` at runtime (same manual
   `JSON.parse<dynamic>` pattern as `trophies.json` — no kotlinx.serialization). `ProjectEntry` and
   the parser live in `pages/Projects.kt`, reused by `pages/projects/Slug.kt`. **Caveat**: a
   brand-new project slug still needs one line in `site/build.gradle.kts`'s `addExtraRoute` —
   Kobweb's static exporter can't discover dynamic routes from a runtime fetch. Editing an existing
   project's content needs only the JSON file.
3. Sticky footer: `.app` (AppEntry.kt) is now `display:flex; flex-direction:column`, `.screen`
   (PageLayout.kt) is `flex:1 0 auto` — short pages now push the footer to the true viewport
   bottom instead of leaving a gap. Verified with a 2000px-tall viewport on `/contact`.
4. Home's Stack section is now fetched from `resources/public/stack.json` — same pattern, each tag
   carries its own `TagColor` (which ones get a colored border), each group carries a
   `labelColor` for the heading.
5. Contact prompt: extensive Playwright testing (typing, click-send, Enter-to-send, Polish
   diacritics) found **no reproducible code defect** — no console errors, log updates correctly,
   `mailto:` URL is well-formed. The mock's own `app.js` uses the exact same
   `window.location.href = "mailto:..."` handoff, which silently no-ops with no default mail
   client registered — plausible explanation for "doesn't work". Added a real, clickable mailto
   `<a>` fallback link after send so a missing OS mail handler doesn't look like a dead prompt.
6. Brand (nav logo) now has the mock's own `.glitch` class + `data-text` (shake on hover — was
   simply missing since the rewrite) plus a new translucent blue hover background (deliberate
   deviation, no blue token in the mock's palette). Implemented via `onMouseEnter`/`onMouseLeave` +
   inline style, **not** a Silk `CssStyle` — verified in-browser that Kobweb wraps `CssStyle`
   output in `@layer general-styles`, and CSS gives any unlayered rule (the mock's plain
   `.brand { background: transparent }` in styles.css) priority over any layered one regardless of
   specificity, so a `CssStyle`-based hover can never win here.
7. Synced `docs/handoff/`'s light/dark theme addition into the site: new `Theme.kt`
   (`LocalTheme`/`LocalThemeSetter`, `detectInitialTheme`/`persistTheme`, same shape as
   `Lang.kt`), theme toggle button in `NavHeader.kt`, `data-theme` attribute set on
   `<html>` via a `LaunchedEffect` in `AppEntry.kt`. Also fixed two real bugs found in the mock's
   own hand-edited `styles.css` while porting it (fixed in both `docs/handoff/styles.css` and our
   copy): a self-referential `--rule: var(--rule);` in `:root` (broke every dark-mode divider
   line) and a malformed 720px-breakpoint `.theme-btn` override that pasted the full base rule
   instead of a minimal one and dropped the `min-height: 48px` touch target its sibling buttons get
   there.
8. User asked whether styling can live in Kotlin (`CssStyle`) instead of the raw stylesheet.
   Decision: **kept the raw `styles.css` as the resync source** for anything mock-derived (tokens,
   light/dark theme, `.theme-btn` chrome) — it's what stays in lockstep with the user's own edits
   to `docs/handoff/`, and item 6 above is a concrete demonstration of why mixing Silk `CssStyle`
   with that raw stylesheet is actively risky (the `@layer` interaction). Everything genuinely new
   and site-specific (footer color, sticky footer, brand hover) is written directly in Kotlin via
   inline `style{}`/modifiers — already this codebase's established convention for one-off styling,
   satisfies the literal ask without the `@layer` footgun.

## Next steps

- Faza 4 — Dane: nightly GitHub Action generating the real `trophies.json` via `psn-api`
  (ROADMAP F025).
- Print-preview contrast on CV's bullet lists/skills values is a bit low (mock's `--dim`/`--mut`
  grays, not fully overridden by the added print block) — legible but could be darkened further.
- If a brand-new project is added often enough that the `addExtraRoute` line becomes annoying, a
  small Gradle-time JSON read to auto-generate that list would remove the last manual step (not
  built now — YAGNI until it's actually a recurring pain point).
