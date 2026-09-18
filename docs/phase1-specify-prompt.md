Phase 1 — Layout and Routing: replace the handoff mock's client-side visibility router with
Kobweb's real file-based routing, and stand up the real nav shell and language-switching
mechanism on top of the Phase 0 visual foundation (specs/001-foundation-setup) — still with no
real page content.

Scope (maps to ROADMAP.md F005–F009):

1. Confirm Kobweb's routing model replaces the mock's router (F005)
   docs/handoff/app.js implements a hand-rolled router: it shows/hides `[data-screen]` sections
   via `.hidden`, with a comment at app.js:78 noting "On GitHub Pages: swap this for hash routing
   or history routing". Kobweb doesn't need any of that — `@Page` + file-based routing under
   `pages/` already produces one exported HTML file per route (`kobweb export --layout static`
   emits `/`, `/about`, etc., confirmed in Phase 0's quickstart.md). This phase's job is to
   confirm/document that no client-side router, hash routing, or `app.js`-style show/hide logic
   is needed — real navigation is just `@Page` files plus Kobweb's `Link`/routing, already used by
   the existing `NavHeader.kt` (`Link("/", "Home")`, `Link("/about", "About")`).

2. Verify 404 handling under static export (F006)
   Confirm `kobweb export -PkobwebExportLayout=STATIC` generates a `404.html` (Kobweb's standard
   static-export behavior for unmatched routes) and that it renders using the same theme/overlay
   foundation as every other page (Phase 0), not a bare unstyled fallback — this matters because
   GitHub Pages serves `404.html` directly for any unmatched path.

3. NavHeader: brand mark, nav buttons, language switch (F007)
   `site/src/jsMain/kotlin/com/anjo/anjosite/components/sections/NavHeader.kt` already has a
   working nav shell (logo, `MenuItems()`, responsive hamburger/side-menu, color-mode toggle) —
   ported from the Kobweb template, not yet on-brand. This phase updates it to match the mock's
   nav (docs/handoff/index.html): the site's actual brand/logo treatment in place of the Kobweb
   logo, nav links matching the site's real sections (not just Home/About — decide against the
   final page set once Phase 3's pages are known, or keep to what exists today and extend later),
   and a language switch control wired to F008 below. Keep the existing color-mode
   toggle/hamburger/side-menu structure — it's infrastructure, not mock-specific.

4. i18n: replace `data-lang-block` with a real language state (F008)
   The mock duplicates every piece of copy in two `data-lang-block="en"` / `data-lang-block="pl"`
   sibling elements (see docs/handoff/index.html:44-45, 74-78, 180-185) and toggles visibility via
   CSS/JS — this is exactly the anti-pattern constitution Principle III calls out by name as "the
   anti-pattern being replaced". This phase introduces the real mechanism: a language state
   (`CompositionLocal` or equivalent) that every future page/component reads from, with content
   for each language modeled as data (a class/holder per string or content block, not duplicated
   composable trees) — plus the actual switch control in NavHeader (F007) that flips it. No real
   page copy is being ported yet (that's Phase 3); this phase only needs to prove the mechanism
   works, e.g. on the existing Index placeholder or a minimal demo string.

5. Design-notes panel: decide production handling (F009)
   docs/handoff/index.html ships a design-notes panel explicitly marked "usunąć przed wdrożeniem"
   (remove before deploy) in ROADMAP.md's current-state notes. Decide: omit it entirely from the
   Kobweb port (simplest, matches "remove before deploy"), or gate it behind a dev-only flag if
   it's still useful during Phase 2/3 component work. Default to omission unless there's a
   concrete reason to keep it — YAGNI (constitution Principle VIII).

Out of scope for this phase: actual page content for About/Projects/Trophies/Contact/CV (Phase
3), component library (Terminal, ProjectCard, etc. — Phase 2), real bilingual copy beyond the
mechanism proof in F008, CI/deploy pipeline (Phase 5).

Definition of done: `kobweb export -PkobwebExportLayout=STATIC` succeeds from site/, the exported
output includes a correctly styled `404.html`, NavHeader renders on-brand with a working language
switch that changes at least one piece of visible text, and no `app.js`-style visibility router
exists anywhere in the Kotlin source — a passing `kobweb run` dev session alone is not sufficient
(constitution Principle VII).
