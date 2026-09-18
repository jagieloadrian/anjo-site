# Quickstart: Verify Layout and Routing

Validates the acceptance scenarios in `spec.md` (User Stories 1–3) against a real static export.
See `data-model.md` for the Route/Lang/Bilingual String shapes and `research.md` for why each
mechanism was chosen.

## Prerequisites

- JDK installed (Gradle toolchain will fetch the rest)
- Repo root: `/home/diether18/IdeaProjects/anjo-site`
- Phase 0 (specs/001-foundation-setup) already implemented — this phase builds on its foundation

## 1. Export the static site

```bash
cd site
../gradlew kobwebExport -PkobwebExportLayout=STATIC
```

**Expected**: build succeeds with zero errors (SC-005). Output lands under `site/.kobweb/site/`,
now including `index.html`, `about.html`, `projects.html`, `trophies.html`, `contact.html`,
`cv.html`, and `404.html`.

## 2. Serve the export

```bash
cd site/.kobweb/site
python3 -m http.server 8000
```

Open `http://localhost:8000/` in a browser.

## 3. Check User Story 1 — real navigation (SC-001)

- Click each of the six nav links (Home, About, Projects, Trophies, Contact, CV). Confirm the
  browser URL changes each time and the page loads its own distinct HTML file (check DevTools →
  Network — no client-side-only visibility toggling, a real navigation/request occurs).
- Grep the exported Kotlin/JS bundle or source for `data-screen`/`.hidden`-style show-hide logic —
  none should exist (FR-001).

## 4. Check User Story 2 — nav brand, language switch (SC-003)

- Confirm the nav shows the site's own brand mark, not the Kobweb logo.
- Hover the brand mark: the glitch animation plays. In DevTools → **Rendering** → emulate
  `prefers-reduced-motion: reduce`, reload, hover again: no animation plays (FR-005).
- Note the page's initial language. Change your browser's language setting (or check
  `navigator.language` in DevTools console) and reload — confirm it matches Polish-if-`pl`-else-
  English (FR-008).
- Click the language switch: confirm the Index placeholder's demo string changes between its
  English and Polish form, with no full page reload (FR-009).
- Grep the Kotlin source for `data-lang-block` — none should exist (FR-010).

## 5. Check User Story 3 — on-brand 404 (SC-002)

- Open `http://localhost:8000/does-not-exist` directly, or open `site/.kobweb/site/404.html`
  directly in a browser.
- Confirm it renders with the same background/type/overlay foundation as any other page — not a
  bare/default fallback (FR-004).
- Toggle the language switch and reload `404.html`: confirm the not-found message text changes
  between its English and Polish form, same as the Index demo string (spec.md Assumptions).

## 6. Design-notes panel absence check (FR-011)

- Grep the exported HTML output for any design-notes-panel markup/text — none should be present
  on any page.

## Done

All six checks pass → Phase 1/Layout and Routing is complete per this feature's Success Criteria.
