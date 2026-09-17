# Phase 1 Data Model: Foundation Setup

This phase introduces no runtime/user data (no database, no API payloads — constitution Principle
I). The "entities" below are configuration/theme constants, included because the spec's Key
Entities section names them; they exist as Kotlin code, not as data flowing through the system.

## Design Token Set

Represents the site's single source of truth for color, per constitution Principle IV. Lives in
`SiteTheme.kt` as the existing `SitePalette` class, populated identically for both `light` and
`dark` slots (see research.md §4).

| Field | Type | Source (mock) | Notes |
|---|---|---|---|
| `background` | `Color` | `--bg` | Page background |
| `ink` | `Color` | `--ink` | Primary text color |
| `pink` | `Color` | `--pink` | Accent 1 |
| `cyan` | `Color` | `--cyan` | Accent 2 |
| `red` | `Color` | `--red` | Accent 3 |

Relationship: every composable reads exactly one `SitePalette` instance via
`ColorMode.current.toSitePalette()` — no composable stores or duplicates a color value.

Validation rule: a value here is added/changed in exactly one place (`SiteTheme.kt`); no PR may
introduce a second definition of any of these five colors.

Note: `SitePalette` also carries `nearBackground` and `brand.primary`/`brand.accent` — pre-existing
template fields kept because `Footer.kt`/`NavHeader.kt` still consume them (repointed to
pink/cyan), not new tokens introduced by this phase.

## Typeface Asset

| Field | Type | Value | Notes |
|---|---|---|---|
| `family` | `String` | `"Archivo"` | Display typeface |
| `weights` | `List<Int>` | `[400,500,600,700,800,900]` | Matches mock |
| `family` | `String` | `"JetBrains Mono"` | Monospace typeface |
| `weights` | `List<Int>` | `[400,500,700]` | Matches mock |
| `source` | enum | `CDN` | Google Fonts `<link>`, see research.md §2 |

Not a Kotlin data class in this phase — represented as literal `<link>` head elements
(`site/build.gradle.kts`), since there is no per-page or per-visitor variation to model.

## Base Path Setting

| Field | Type | Value | Notes |
|---|---|---|---|
| `basePath` | `String` | `""` (unset = root) | `.kobweb/conf.yaml` `site.basePath`, see research.md §1 |

Single value, consumed by every generated asset/link URL during static export. No relationships —
this is a build-time constant, not a runtime entity.
