# Phase 1 Quickstart: Shared UI Components

Validation guide for the seven components once implemented. Parameter shapes are in
`data-model.md`; there is no automated test suite in this project (see `plan.md`'s Technical
Context), so this is the manual verification loop.

## Prerequisites

- `site/src/jsMain/kotlin/com/anjo/anjosite/components/widgets/` contains all seven new files
  (`Tag.kt`, `StatRow.kt`, `TimelineEntry.kt`, `ProjectCard.kt`, `GameCover.kt`, `TrophyRow.kt`,
  `Terminal.kt`).
- No Phase 3 page exists yet to host them — render each against representative sample data by
  temporarily calling it from an existing placeholder page body (e.g. `pages/Projects.kt`'s
  `ProjectsPage()`), or a scratch page under `pages/`, and remove the scratch call afterward. This
  is throwaway wiring for verification only — it is not the Phase 3 page work.

## Run

```bash
cd site
kobweb run
```

Open <http://localhost:8080> (or whichever placeholder route hosts the scratch render).

## Scenario checklist

### US1 — content-bearing components (P1)

1. Render one `ProjectCard` with sample `ProjectSummary` (title, description, 2-3 tags, a real
   image URL, `href`). Confirm title/description/tags/cover all visible, and clicking anywhere on
   the card navigates to `href`.
2. Re-render the same `ProjectCard` with `coverImageUrl = null`. Confirm a placeholder renders
   (no broken-image icon), card is still fully clickable.
3. Render one `StatRow` with a normal `StatItem`, and a second with `value = ""`. Confirm both
   render without error/crash.
4. Render one `TrophyRow` with `earnedAt` set, and a second with `earnedAt = null`. Confirm the
   second renders a locked/unearned state, and both rows are clickable.
5. Render `GameCover` with an image and with `imageUrl = null`. Same placeholder/clickable checks
   as `ProjectCard`.
6. Render `Tag` once per `TagVariant`. Confirm each variant is visually distinct and every tag is
   clickable to its `href`.

### US2 — `Terminal` boot effect (P2)

7. Render `Terminal` with a short sample `List<TerminalLine>` (mix of `COMMAND` and other
   styles) under normal OS motion settings. Confirm text types out character by character, with a
   visibly longer pause after `COMMAND` lines than others.
8. In browser devtools, enable "Emulate CSS prefers-reduced-motion: reduce" (Chrome: Rendering
   tab → "Emulate CSS media feature prefers-reduced-motion"), reload. Confirm the full text
   appears instantly, no character-by-character reveal.
9. With the emulation still off (normal motion), reload mid-animation and toggle the emulation on
   *while it's typing*. Confirm the in-progress animation stops and the full text snaps in
   immediately, without waiting for the current line to finish.

### US3 — `TimelineEntry` (P3)

10. Render one `TimelineEntry` with a sample date/title/description. Confirm all three are visible
    and read as one entry.

## Cross-cutting checks

11. **Touch target**: In devtools responsive mode, set viewport width to 719px (just under the
    720px breakpoint). For every clickable component above, confirm its clickable area's computed
    height is at least 48px.
12. **Typography**: Resize the viewport from very narrow to very wide. Confirm no component's text
    jumps between discrete sizes at a breakpoint — it should scale continuously (`clamp()`).
13. **Design tokens**: Skim each new file's source for hex colors, raw `px` font sizes, or spacing
    values not routed through `SiteTheme.kt`'s `SitePalette`/existing spacing constants — there
    should be none (FR-012).
14. **Accessibility — compile-time**: Attempt to construct a `ProjectSummary`, `GameCoverImage`,
    `TrophyEntry`, or `Tag` call omitting `alt`/`href` — confirm this fails to compile (FR-014,
    SC-006), not merely a runtime warning.
15. **Accessibility — screen reader**: With a screen reader (or devtools' Accessibility tree
    inspector) on `GameCover` (image-only, no visible text), confirm its announced accessible name
    matches the supplied `alt` text.
16. **Bilingual-ready**: Render the same `ProjectCard`/`TrophyRow`/etc. twice with Polish and
    English sample strings, passed in as plain parameters (no component code changes). Confirm
    both render correctly (SC-005).
