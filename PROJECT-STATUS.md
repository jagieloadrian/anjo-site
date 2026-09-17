# Project Status

## Completed work

- Fazy 0–2 (fundament, layout/routing, komponenty) — done, see specs/001-003.
- Faza 3 — Strony (specs/004-pages): all six routes real (`/`, `/about`, `/projects` + per-project
  `/projects/{slug}`, `/trophies`, `/contact`, `/cv`). `kobweb export -PkobwebExportLayout=STATIC`
  verified green — static file exists for every route including all 4 project slugs.
- Real-browser verification pass (Playwright) against `docs/handoff/mobile-check.html`'s 390/430/768
  widths and the `/cv` print preview — found and fixed two bugs (see decisions below): sub-48px
  touch targets on bare `Link`/`TextInput`/`Button` elements, and unreadable print-preview colors.

## Important decisions

- Project detail = real Kobweb dynamic route (`@Page("{}")` on `pages/projects/Slug.kt`), **plus**
  `site/build.gradle.kts`'s `export { addExtraRoute(...) }` for each slug — Kobweb's exporter
  silently skips any route containing `{}` otherwise. Slug list is duplicated (by hand) between
  `Projects.kt`'s `projectEntries` and `build.gradle.kts`; keep them in sync when adding a project.
- Bilingual content in in-source lists (Projects/About/CV) uses `BilingualString` fields resolved
  to plain `String` via a `toXxx(lang)`/`.resolve(lang)` mapper at render time — Phase 2 components
  only ever take pre-resolved strings. `trophies.json`'s game/trophy names are an intentional
  bilingual exception (proper nouns from an external PSN automation); only its stat *labels* are
  translated, page-side.
- `ContactPrompt` (new shared component) built on Silk's native `TextInput`/`Button` — zero new
  Gradle dependencies this phase.
- Deleted the leftover Kobweb template's demo `resources/markdown/About.md` — it collided with the
  real `/about` route (duplicate-route KSP error).
- Touch targets: bare `<a>`/`<input>`/`<button>` are `display: inline`, so `min-height` alone does
  nothing below the 720px breakpoint. Fixed with one shared `TouchTargetStyle` (AppStyles.kt) — sets
  `inline-flex` + `align-items: center` + `min-height: 48px` under `max-width: 720px` — applied via
  `.toModifier()` at the 8 bare-widget call sites this phase introduced.
  Pre-existing NavHeader icon buttons (45px) and Footer credit links (20px) are still under 48px but
  out of this phase's diff — left alone.
- Print colors: Kobweb's CssStyle DSL has no `!important` (throws `IllegalStateException` at runtime
  if you try) so a plain `html, body` print rule can't beat Silk's `Surface`/`SmoothColorStyle` dark
  background, and any inline `.color(...)` modifier chain (e.g. `TimelineEntry`'s description text)
  can't be beaten by any external rule at all. Fixed by (a) giving the root `Surface` an
  `id("site-surface")` — an id always outranks a class regardless of registration order — with a
  print rule forcing white/black on it, and (b) moving `TimelineDescriptionStyle`'s alpha-ink color
  into the CssStyle itself (not a chained modifier) so its own print `cssRule` can override it.

## Next steps

- Faza 4 — Dane: nightly GitHub Action generating the real `trophies.json` via `psn-api`
  (ROADMAP F025); externalize `projects.json`/`skills.json` if desired (F026).
