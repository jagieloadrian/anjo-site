Phase 0 — Foundation: get the Kobweb project (site/) ready to receive the ported UI, matching the
design tokens and asset setup already proven in docs/handoff/ (static HTML/CSS mock), before any
page or component work starts.

Scope (maps to ROADMAP.md F001–F004):

1. GitHub Pages base path (F001)
   Decide and configure whether this site deploys as a GitHub project page (served under
   /<repo-name>/) or a user page (served at the domain root), and set Kobweb's basePath
   accordingly in the app config (site/build.gradle.kts `configAsKobwebApplication`). The choice
   must be consistent with whatever `.kobweb/conf.yaml` and the eventual GitHub Actions deploy
   step use — all three must agree on the same path.

2. Design tokens (F002)
   The mock's palette lives in five CSS custom properties in docs/handoff/styles.css:
   --pink, --cyan, --red, --bg, --ink. Port these into the site's existing theme file
   (site/src/jsMain/kotlin/com/anjo/anjosite/SiteTheme.kt), which currently defines a
   SitePalette/SitePalettes(light/dark) pair for Silk's ColorMode system. Decide whether this
   site keeps light/dark mode at all — the mock has no light variant, it's a single dark
   cyberpunk theme — and either collapse SitePalette to one fixed palette or map the mock's five
   colors onto both light/dark slots. Every other future component must read colors only from
   this theme, never hardcode a hex value (constitution Principle IV).

3. Fonts (F003)
   Load Archivo (weights 400/500/600/700/800/900) and JetBrains Mono (400/500/700), currently
   pulled from Google Fonts CDN in docs/handoff/index.html via <link>. Decide CDN link
   (fast to ship) vs self-hosting the .woff2 files (zero external requests, mentioned as a nice-
   to-have in docs/handoff/README.md, also tracked as F033/backlog) — for Phase 0, CDN is
   acceptable; self-hosting can be a later phase item.

4. Global styles (F004)
   Port the mock's structural/global CSS layer into the site's existing style init file
   (site/src/jsMain/kotlin/com/anjo/anjosite/AppStyles.kt, which already has an @InitSilk hook
   and a couple of CssStyle examples): CSS reset, and the three decorative overlay layers from
   the mock (fx-scan / fx-vignette / fx-grid — scanlines, vignette, background grid). These
   overlays are purely decorative and must be excluded when `prefers-reduced-motion: reduce` is
   set (constitution Principle V) — the existing AppStyles.kt already has a reduced-motion media
   query example for scroll-behavior to follow as a pattern.

Out of scope for this phase: routing, nav, page content, any component beyond the global style
layer, trophies/contact/data pipelines. Those are Phase 1+ (see ROADMAP.md).

Definition of done: `kobweb export --layout static` succeeds from site/ and the exported output
shows the correct palette, fonts, and background fx layers with no console errors — a passing
`kobweb run` dev session alone is not sufficient (constitution Principle VII).
