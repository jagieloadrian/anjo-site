# Phase 0 Research: Foundation Setup

All items below were resolved by reading this project's actual dependency sources
(`~/.gradle/caches/.../com.varabyte.kobweb.gradle/application-0.25.1-sources.jar` and
`kobweb-common-0.25.1-sources.jar`), not from general Kobweb knowledge, so they're specific to the
pinned version (Kobweb 0.25.1) this project uses. No `NEEDS CLARIFICATION` markers remained after
`/speckit-clarify`, so this phase is confirmation/verification research, not open-ended discovery.

## 1. Base path resolution for a GitHub user-page deploy

**Decision**: Leave `site.basePath` unset in `.kobweb/conf.yaml` — do not add a `basePath` key.

**Rationale**: `KobwebConf.site.basePath` (`kobweb-common`,
`com/varabyte/kobweb/project/conf/KobwebConf.kt:28`) defaults to `""`, and every consumer
(`KobwebExportTask`, `KobwebGenerateSiteIndexTask`, `AppBlock.IndexBlock`) treats an empty
`BasePath` as root — it prepends nothing to generated asset/link URLs. Since the spec's resolved
decision is a root user page (`jagieloadrian.github.io`, base path `/`), the default *is* the
correct value. Explicitly writing `basePath: "/"` or `basePath: ""` would be a no-op at best and
a copy-paste risk (someone later "fixing" it to `/anjo-site`) at worst.

**Alternatives considered**:
- Setting `basePath: "/anjo-site"` — rejected, this is the *other* clarified option (project page)
  and would break every asset/link URL under the actual root-page deploy.
- Setting it explicitly to `""` for clarity — rejected as unnecessary; the absence of the key *is*
  the documented default, and FR-009's export-based verification (this phase's DoD) will catch a
  regression if that ever changes upstream.

## 2. Loading Google Fonts (Archivo, JetBrains Mono) without a new dependency

**Decision**: Use Kobweb's existing `kobweb.app.index.head` DSL in `site/build.gradle.kts`:

```kotlin
kobweb {
    app {
        index {
            description.set("...")
            head.add {
                link(rel = "preconnect", href = "https://fonts.googleapis.com")
                link(rel = "preconnect", href = "https://fonts.gstatic.com", crossorigin = "anonymous")
                link(
                    rel = "stylesheet",
                    href = "https://fonts.googleapis.com/css2?family=Archivo:wght@400;500;600;700;800;900&family=JetBrains+Mono:wght@400;500;700&display=swap"
                )
            }
        }
    }
}
```

**Rationale**: `AppBlock.IndexBlock.head` (`ListProperty<HEAD.() -> Unit>`) is exactly the
documented mechanism for adding arbitrary `<head>` elements to the generated `index.html` — it is
already used by the template for `description`, so this is the same DSL, not a new API surface,
and needs no new Gradle dependency (constitution Principle VIII). It reproduces the exact three
`<link>` tags the mock already uses in `docs/handoff/index.html`.

**Alternatives considered**:
- Self-hosting `.woff2` files — deferred per spec Assumptions (F033/backlog item, not required for
  this phase's definition of done).
- A Kobweb/Compose font-loading library — rejected, no such dependency is declared or needed; the
  CDN `<link>` approach is standard HTML and framework-agnostic.

## 3. Reduced-motion handling pattern

**Decision**: Reuse the exact `CSSMediaQuery.MediaFeature("prefers-reduced-motion", ...)` pattern
already present in `AppStyles.kt`'s `initSiteStyles` (currently used to *enable* smooth scrolling
under `"no-preference"`), inverted to target `"reduce"` and hide the scanline overlay:

```kotlin
ctx.stylesheet.registerStyle(".fx-scan") {
    cssRule(CSSMediaQuery.MediaFeature("prefers-reduced-motion", StylePropertyValue("reduce"))) {
        Modifier.display(DisplayStyle.None)
    }
}
```

**Rationale**: This is a direct Kotlin port of the mock's own rule (`docs/handoff/styles.css:58-59`
— `@media (prefers-reduced-motion: reduce) { .fx-scan { display: none; } }`), and it follows the
exact API pattern this codebase already established for the same media feature — no new pattern
introduced, satisfying reuse (Principle VIII) and the constitution's motion-discipline principle.

**Note**: Only the scanline overlay is motion-gated. The vignette and grid overlays are static
gradients with no animation of their own (confirmed by reading `docs/handoff/styles.css`), so they
render unconditionally per FR-006/spec Acceptance Scenario 2 — this is not an oversight, it matches
the mock exactly.

## 4. Color-mode (light/dark) strategy for a single-theme mock

**Decision**: Keep Silk's existing `ColorMode` plumbing in `AppEntry.kt` untouched (system
preference + `localStorage` persistence), but set both `SitePalettes.light` and
`SitePalettes.dark` in `SiteTheme.kt` to the same five mock-derived values.

**Rationale**: The mock has exactly one visual theme — there is no designed light variant to port.
Two options existed: (a) rip out Kobweb's light/dark infrastructure entirely, or (b) point both
palette slots at the same token set. Option (b) is the smaller diff (Principle VIII/YAGNI — don't
delete working, already-wired infrastructure to save a few lines) and guarantees Principle IV
(single-sourced tokens): every composable still calls `ColorMode.current.toSitePalette()` exactly
as the template already does, but now always resolves to the one on-brand palette regardless of
the visitor's OS setting.

**Alternatives considered**:
- Removing `ColorMode`/`SitePalettes` entirely and hardcoding one `SitePalette` object — rejected,
  larger diff for no behavioral difference, and would touch `AppEntry.kt`'s existing, working
  persistence logic for no reason.
- Designing an actual light variant — explicitly out of scope; the mock defines only one theme.

**Implementation addendum (discovered during `/speckit-implement`, not foreseen at planning time)**:
`SitePalette`/`SitePalettes` is a project-defined convenience class — it is NOT what actually
controls the page's visible background/text color. That's Silk's own built-in
`ctx.theme.palettes.light/dark` (set in `SiteTheme.kt`'s `@InitSilk fun initTheme`), consumed by
`Surface(SmoothColorStyle...)` in `AppEntry.kt`. The original template pointed Silk's palette at a
white/black light-mode pair unrelated to `SitePalette`, which would have kept rendering a white
page in light mode even after this phase's token work. Fix: `initTheme` now reads its
background/color values from `SitePalettes.light`/`SitePalettes.dark` directly (both resolving to
the same mock tokens per the decision above), so there is exactly one place the five colors are
defined, consumed by both the custom palette (Footer/Nav accents) and Silk's own palette (page
background/text).

## 5. Global CSS reset scope

**Decision**: Port only what the mock's `styles.css` root/reset layer actually sets (margin/
padding/box-sizing reset, base font stack fallback, base background/text color) into `AppStyles.kt`
`initSiteStyles`, as additional `registerStyleBase`/`cssRule` calls alongside the existing
`html`/`body` rules — not a new file, not a CSS-reset library dependency.

**Rationale**: `AppStyles.kt` already owns this responsibility (it currently sets `body` font stack
and size) — extending the same `@InitSilk` function keeps global style ownership in one place
(single source per Principle IV) rather than splitting it across a new file.

**Alternatives considered**: A dedicated CSS-reset dependency (e.g. a "modern reset" library) —
rejected per Principle VIII; the mock's reset is a handful of rules, not worth a dependency.
