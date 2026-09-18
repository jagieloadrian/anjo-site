package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

// Mechanically split out of the original single-file transcription of docs/handoff/styles.css
// (request: move CSS into Kotlin; then split that one 1200-line object into single-responsibility
// files). Every declaration is a literal, unreordered transcription via the raw
// property(name, value) escape hatch already used throughout this codebase — not a redesign.

// Full-bleed decorative overlays (scanlines/vignette/grid) and prefers-reduced-motion overrides.
object SiteOverlayStyles : StyleSheet() {
    init {
        // ── overlays ───────────────────────────────────────────
        ".app" style {
            property("position", "relative")
            property("min-height", "100vh")
            property("overflow-x", "hidden")
        }
        ".fx-scan, .fx-vignette, .fx-grid" style {
            property("pointer-events", "none")
        }
        ".fx-scan" style {
            property("position", "fixed")
            property("inset", "0")
            property("z-index", "60")
            property("opacity", "var(--scan-op)")
            property("background-image", "repeating-linear-gradient(to bottom, var(--scan-line) 0px, var(--scan-line) 1px, transparent 1px, transparent 3px)")
        }
        ".fx-vignette" style {
            property("position", "fixed")
            property("inset", "0")
            property("z-index", "61")
            property("background", "radial-gradient(120% 80% at 50% 0%, transparent 40%, var(--vignette-edge) 100%)")
        }
        ".fx-grid" style {
            property("position", "absolute")
            property("inset", "0")
            property("z-index", "0")
            property("background-image", "linear-gradient(var(--grid-line) 1px, transparent 1px), linear-gradient(90deg, var(--grid-line) 1px, transparent 1px)")
            property("background-size", "96px 96px")
        }
        media("(prefers-reduced-motion: reduce)") {
            ".fx-scan" style {
                property("display", "none")
            }
            ".glitch:hover" style {
                property("animation", "none")
            }
            ".glitch:hover::after" style {
                property("content", "none")
            }
            ".caret" style {
                property("animation", "none")
            }
        }
    }
}
