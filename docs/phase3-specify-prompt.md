Phase 3 — Pages: replace every placeholder route (`site/src/jsMain/kotlin/com/anjo/anjosite/pages/`)
with the mock's real content, wired through the seven shared components built in Phase 2
(specs/003-components) and the language/theme mechanisms from Phase 0/1. Add the one route that
doesn't exist yet (About). No new components, no data-source automation.

Scope (maps to ROADMAP.md F019–F024a):

1. Home (F019)
   `Index.kt` is currently a token/font swatch demo (spec 001-foundation-setup FR-010) plus a
   one-string language-mechanism proof (spec 002-layout-routing FR-008). Replace it with the
   mock's real Home screen (docs/handoff/index.html, `data-screen="home"`): hero copy with the
   `Terminal` boot-typing effect, kicker/heading/lede, the `view projects` / `read cv` CTA pair,
   and any project/stat highlights the mock shows below the fold — all real bilingual copy via
   `BilingualString`, all content passed into components as data, none hardcoded past the string
   level.

2. About (F020) — new page, doesn't exist yet
   No `About.kt` exists under `pages/`. Create the route from the mock's About screen: bio copy,
   `TimelineEntry` list for work history, `StatRow`/tag usage if the mock uses them here. Wire it
   into `NavHeader.kt`'s existing link set (already points at `/about`, per Phase 1).

3. Projects — grid + detail (F021)
   `Projects.kt` is a one-line placeholder heading. Build the real grid (`ProjectCard` per
   project, sourced from a `List<ProjectSummary>` data class — never hardcoded per-card
   composables) and the project detail view the mock shows at `data-screen="project"`. Per
   ROADMAP: one layout, data class per project. Static/JSON data sourcing beyond an in-source
   `List<...>` is Phase 4's job (F026) — don't build a fetch layer here.

4. Trophies — UI + `trophies.json` fetch (F022)
   `Trophies.kt` is a placeholder heading. Build the real screen: `StatRow` summary, `GameCover`
   grid, `TrophyRow` feed, matching `data-screen="trophies"`. This phase DOES fetch a
   `trophies.json` at runtime (ROADMAP explicitly scopes the fetch here, not to Phase 4) — but the
   file's content is a static/placeholder JSON checked in by hand, matching the mock's "IMAGE
   SLOT"/dash placeholders (docs/handoff/README.md: "Dane mają przyjść z trophies.json"). The
   nightly PSN API automation that generates this file for real is Phase 4 (F025) — out of scope
   here.

5. Contact (F023)
   `Contact.kt` is a placeholder heading. Build the real screen from the mock's contact prompt
   (`app.js`'s mailto-building behavior + `Terminal`-style prompt UI): construct a `mailto:` link
   from user-entered fields, client-side only. Formspree/Web3Forms is an explicit ROADMAP "opcjonalnie" —
   default to mailto-only (YAGNI, constitution Principle VIII) unless there's a concrete reason to
   add a form-service dependency.

6. CV (F024)
   `Cv.kt` is a placeholder heading. Build the real, print-friendly CV screen with its own
   separate content source (a dedicated data class/list, not reused from Projects/About) —
   the mock's CV screen doubles as a printable resume, so verify it also looks correct via the
   browser's print preview, not just on-screen.

7. Mobile check across all six pages (F024a)
   After building, review every page against `docs/handoff/mobile-check.html`'s three reference
   widths (390 / 430 / 768) — this file already exists in the handoff but per ROADMAP has never
   actually been exercised in any prior phase. This phase is where that check finally happens,
   for real content on every route, not the placeholders.

Out of scope for this phase: any new shared component (Phase 2 is done — reuse `Terminal`,
`ProjectCard`, `TimelineEntry`, `StatRow`, `GameCover`, `TrophyRow`, `Tag` as-is; if a page
genuinely needs a component variant that doesn't exist, flag it rather than silently extending
Phase 2's surface), `trophies.json` generation automation and moving projects/skills to external
JSON files (Phase 4, F025/F026), CI/deploy pipeline (Phase 5), SEO/analytics polish (Phase 6).

Definition of done: all six routes (`/`, `/about`, `/projects` incl. detail, `/trophies`,
`/contact`, `/cv`) render real bilingual content built from the Phase 2 component library, `Cv`
looks correct in print preview, `Contact` produces a working `mailto:` link, `kobweb export
-PkobwebExportLayout=STATIC` succeeds, and every page has been checked against
`docs/handoff/mobile-check.html`'s 390/430/768 widths — not just a passing `kobweb run` dev
session (constitution Principle VII).
