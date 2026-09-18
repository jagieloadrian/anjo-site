# Phase 3 Quickstart: Pages

Validation guide for all six real routes once implemented. Entity/component shapes are in
`data-model.md`; there is no automated test suite in this project (see `plan.md`'s Technical
Context), so this — plus `docs/handoff/mobile-check.html` and a browser print preview — is the
verification method.

## Prerequisites

- `pages/Index.kt`, `pages/About.kt` (new), `pages/Projects.kt` (+ its dynamic
  `pages/projects/Slug.kt`), `pages/Trophies.kt`, `pages/Contact.kt`, `pages/Cv.kt` all render
  real content built from the Phase 2 components plus the new `ContactPrompt`.
- `site/build.gradle.kts`'s `kobweb.app.export.extraRoutes` lists one concrete route per project
  slug (research.md §1) — otherwise the static-export check below will fail.
- A hand-authored `trophies.json` exists as a static asset (FR-008).

## Run

```bash
cd site
kobweb run
```

Open <http://localhost:8080>.

## Scenario checklist

### US1 — Home → Projects (P1)

1. Load `/`. Confirm the `Terminal` boot effect plays with real hero copy (kicker/heading/lede)
   and both CTAs ("view projects", "read cv") are present.
2. Click "view projects". Confirm `/projects` shows one `ProjectCard` per entry in the in-source
   projects list.
3. Click a project card. Confirm you land on `/projects/{slug}` (a real URL, not a client-only
   state change) showing that project's full description, tags (as clickable `Tag`s), and any
   linked assets.
4. Reload that `/projects/{slug}` URL directly (simulating a shared link / GitHub Pages request).
   Confirm it still renders — this is the check that `extraRoutes` actually registered the static
   file (research.md §1); if this 404s, the export config is missing that slug.
5. Visit `/projects/does-not-exist`. Confirm you're redirected to the existing 404 page.
6. Temporarily empty the projects list and reload `/projects`. Confirm an empty-but-not-broken
   grid, then restore the list.

### US2 — About + CV (P2)

7. Load `/about`. Confirm real bilingual bio copy and one `TimelineEntry` per career/history item.
8. Load `/cv`. Confirm real CV content, drawn from its own `CvContent` data (not the About/Projects
   lists).
9. Open the browser's print preview on `/cv` (Ctrl/Cmd+P). Confirm nav header and footer are
   hidden and the content reads as a clean printable resume with nothing cut off.

### US3 — Trophies (P2)

10. With `trophies.json` reachable, load `/trophies`. Confirm `StatRow` summary, one `GameCover`
    per game, and one `TrophyRow` per trophy, all sourced from the fetched JSON.
11. Throttle/block the `trophies.json` request (devtools Network tab → block request URL) and
    reload. Confirm a loading skeleton appears first, then a clear error/empty state — never a
    blank page.

### US4 — Contact (P3)

12. Load `/contact`. Type a message and activate send. Confirm a `mailto:` link opens addressed to
    the site owner's fixed email, with the fixed subject and the typed message as the body.
13. Leave the message field empty and attempt to send. Confirm the page blocks it and indicates the
    message is required — no `mailto:` navigation happens.

## Cross-cutting checks

14. **Mobile check (SC-005)**: For each of the six routes, open devtools responsive mode at 390px,
    430px, and 768px (matching `docs/handoff/mobile-check.html`) and confirm no overlapping/clipped
    content and no touch target under 48px.
15. **Bilingual (FR-013)**: Toggle the language switch on each route. Confirm all real copy
    switches with it — project titles/descriptions, About's bio/timeline, CV's sections, and
    Trophies' stat *labels*. Game titles and trophy names on `/trophies` are expected to stay in
    their original (English) form in both languages — a documented exception, not a bug (analyze
    finding C2, spec.md Assumptions).
16. **Static export (SC-006)**: From `site/`, run `kobweb export -PkobwebExportLayout=STATIC`.
    Confirm it succeeds and the output directory contains an HTML file for each of the six routes
    plus one per project slug in `extraRoutes`.
