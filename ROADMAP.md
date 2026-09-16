# Roadmap — anjo-site (Kobweb → GitHub Pages)

Status na 2026-09-16: świeży Kobweb template (`site/`), bez custom kodu. Makieta docelowa w `docs/handoff/` (statyczny HTML/CSS/JS, do przepisania na Compose HTML + Silk).

## Stan obecny

- `site/build.gradle.kts` — frontend only, Compose HTML + Silk, plugin markdown włączony, brak jsMain treści poza boilerplate (`AppEntry`, `NavHeader`, `Footer`, `PageLayout`, `MarkdownLayout`, `IconButton`).
- `.kobweb/conf.yaml` — tytuł `d18-site`, port 8080, brak `basePath`.
- Handoff: 7 ekranów (Home/About/Projects/Project/Trophies/Contact/CV), vanilla router przez `hidden`, boot-typing terminal, mailto contact prompt, EN/PL toggle przez `data-lang-block`, design-notes panel (usunąć przed wdrożeniem), `trophies.json` placeholder, zero backendu.

## Fazy i features

### Faza 0 — Fundament
- **F001** — `configAsKobwebApplication`: ustalić `basePath` (project page `/anjo-site/` vs `username.github.io` root)
- **F002** — `SiteTheme.kt`: przenieść tokeny `--pink/--cyan/--red/--bg/--ink` z `styles.css`
- **F003** — Google Fonts Archivo + JetBrains Mono (link albo self-host `.woff2`)
- **F004** — `AppStyles.kt`: reset, scanline/vignette/grid fx jako global CSS

### Faza 1 — Layout i routing
- **F005** — Potwierdzić: Kobweb static export = SSG per-page, routing file-based automatyczny → hash routing z `app.js` zbędny
- **F006** — Zweryfikować generowanie `404.html` przy `kobweb export --layout static`
- **F007** — `NavHeader.kt`: brand glitch, nav buttons, lang switch
- **F008** — i18n: `LangProvider`/`CompositionLocal` zamiast `data-lang-block`, treści jako data class per język
- **F009** — Design-notes panel: pominąć w produkcji (albo dev-only flag, do decyzji)

### Faza 2 — Komponenty
- **F010** — `Terminal` (boot typing): `LaunchedEffect` + `prefers-reduced-motion` check
- **F011** — `ProjectCard`
- **F012** — `TimelineEntry`
- **F013** — `StatRow`
- **F014** — `GameCover`
- **F015** — `TrophyRow`
- **F016** — `Tag(variant)`
- **F017** — Touch target min-height 48px pod 720px breakpoint
- **F018** — `clamp()` typografia

### Faza 3 — Strony
- **F019** — Home
- **F020** — About
- **F021** — Projects (grid + detail — jeden layout, data class per projekt)
- **F022** — Trophies (UI + `trophies.json` fetch)
- **F023** — Contact (mailto: build; opcjonalnie Formspree/Web3Forms)
- **F024** — CV (print-friendly, osobne źródło danych)

### Faza 4 — Dane
- **F025** — `trophies.json`: nocny GitHub Action z `psn-api`, `NPSSO` jako repo secret
- **F026** — `projects.json` / `skills.json` jako statyczne dane zamiast hardkodowanych stringów

### Faza 5 — CI/Deploy
- **F027** — GitHub Action: `kobweb export --layout static` → deploy `gh-pages`
- **F028** — Decyzja: project page vs user page (wpływa na `basePath`)
- **F029** — Osobny nocny workflow na `trophies.json`

### Faza 6 — Polish
- **F030** — SEO: per-page meta/OG (bazowy `description.set()` już w build.gradle)
- **F031** — A11y pass: alt teksty, reduced-motion pełne pokrycie, kontrast
- **F032** — (opcjonalnie) privacy-friendly analytics (Plausible/GoatCounter)
- **F033** — (opcjonalnie) self-hosted fonty zamiast Google Fonts CDN

## Draft `/speckit.constitution`

```
This project is a personal portfolio site built with Kobweb (Kotlin/JS + Compose HTML + Silk),
exported as a static site and deployed to GitHub Pages. Non-negotiable principles:

1. Static-only, zero backend. No server-side code, no database. Anything that looks like
   a form submission (contact) goes through mailto: or a public-key third-party form service
   (Formspree/Web3Forms) — never a custom endpoint.

2. Content as data, not markup. Projects, trophies, skills, and timeline entries live in
   serializable data classes or JSON/markdown front matter, never hardcoded inline in
   composables. Adding a 5th project or a new job entry must not require touching layout code.

3. Bilingual parity (EN/PL). Every user-facing page ships both languages from the same
   component via a language state, not duplicated markup blocks. Technical labels, stack
   names, and tags stay English in both languages.

4. Design tokens are single-sourced in SiteTheme.kt (colors, fonts, spacing). No inline
   magic values in individual composables.

5. Accessibility and motion: prefers-reduced-motion must disable all decorative animation
   (scanlines, glitch, typing effect, cursor blink). Interactive touch targets are minimum
   48px on breakpoints below 720px.

6. No secrets in the JS bundle. Anything password-equivalent (e.g. PSN NPSSO) lives only in
   GitHub Actions secrets, feeding a pre-generated static JSON consumed at build/runtime —
   never fetched client-side with a credential.

7. Deployment target is GitHub Pages static export (`kobweb export --layout static`) only.
   Every phase's definition of done includes a successful static export, not just a passing
   dev server run.

8. YAGNI on dependencies: no new library added unless Kobweb/Silk/Kotlin stdlib genuinely
   can't do it in a reasonable amount of code.
```
