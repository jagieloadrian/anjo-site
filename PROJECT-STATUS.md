# Project Status

## Completed work

- Fazy 0–2 (fundament, layout/routing, komponenty) — done, see specs/001-003.
- Faza 3 — Strony (specs/004-pages): all six routes real (`/`, `/about`, `/projects` + per-project
  `/projects/{slug}`, `/trophies`, `/contact`, `/cv`). `kobweb export -PkobwebExportLayout=STATIC`
  verified green — static file exists for every route including all 4 project slugs.

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

## Next steps

- Faza 4 — Dane: nightly GitHub Action generating the real `trophies.json` via `psn-api`
  (ROADMAP F025); externalize `projects.json`/`skills.json` if desired (F026).
- Not yet visually verified in a real browser: `docs/handoff/mobile-check.html` 390/430/768 check,
  and the `/cv` print-preview layout (structurally wired via `mediaPrint`, but unobserved).
