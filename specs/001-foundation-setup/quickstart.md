# Quickstart: Verify Foundation Setup

Validates the acceptance scenarios in `spec.md` (User Stories 1–3) against a real static export.
See `data-model.md` for the token/font/base-path values being checked and `research.md` for why
each mechanism was chosen.

## Prerequisites

- JDK installed (Gradle toolchain will fetch the rest)
- Repo root: `/home/diether18/IdeaProjects/anjo-site`

## 1. Export the static site

```bash
cd site
../gradlew kobwebExport -PkobwebExportLayout=STATIC
# or, if the Kobweb CLI is installed: kobweb export --layout static
```

**Expected**: build succeeds with zero errors (SC-005 / FR-009). Output lands under
`site/.kobweb/site/`.

## 2. Serve the export from a root path (matches the clarified base path)

```bash
cd site/.kobweb/site
python3 -m http.server 8000
```

Open `http://localhost:8000/` in a browser.

## 3. Check User Story 1 — on-brand visual foundation

- Background, text, and accent colors match the five design tokens (`SC-001`) — compare against
  `docs/handoff/styles.css` `:root` values.
- Headings/body render in Archivo, code/terminal-style text in JetBrains Mono, not a system
  fallback font once loaded (`SC-002`). Confirm via browser DevTools → Elements → Computed →
  `font-family`.
- Zero layout shift once fonts finish loading (`SC-002`): DevTools → **Performance** tab, record a
  reload, check the **Experience** track for layout-shift markers — none expected. Cross-check
  with **Lighthouse** → Performance → Cumulative Layout Shift = 0 (or negligible, `< 0.1`).
- No default/unstyled Kobweb template look remains on the Index page.

## 4. Check User Story 2 — reduced motion

In Chrome DevTools: **Rendering** tab → **Emulate CSS media feature `prefers-reduced-motion`** →
`reduce`. Reload.

- Scanline overlay (`.fx-scan`) is gone (`SC-004`).
- Vignette and grid overlays still render (they're static, not motion — see research.md §3).
- Set back to `no-preference` / `reset`, reload — scanline returns.

## 5. Check User Story 3 — base path / link integrity

- With the export served from `/` (step 2), open DevTools → **Network** tab, reload, and confirm
  zero 404s for CSS, JS, fonts, or internal links (`SC-003`).
- Confirm `.kobweb/conf.yaml` has no `basePath` key set (research.md §1) — its absence is the
  correct, intentional state for a root-page deploy, not an oversight.

## 6. Overlay interaction check (FR-007)

- Click/tap through the nav (`PageLayout`/`NavHeader`, unaffected by this phase) with overlays
  visible. Every nav element remains clickable — overlays must never swallow a pointer event.
  (The Index placeholder itself has no interactive elements per FR-010/T007, so there's nothing
  to click there.)

## Done

All six checks pass → Phase 0/Foundation Setup is complete per this feature's Success Criteria.
