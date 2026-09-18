package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

// Mechanically split out of the original single-file transcription of docs/handoff/styles.css
// (request: move CSS into Kotlin; then split that one 1200-line object into single-responsibility
// files). Every declaration is a literal, unreordered transcription via the raw
// property(name, value) escape hatch already used throughout this codebase — not a redesign.

// Site footer.
object SiteFooterStyles : StyleSheet() {
    init {
        // ── footer ─────────────────────────────────────────────
        ".footer" style {
            property("position", "relative")
            property("z-index", "1")
            property("display", "flex")
            property("flex-wrap", "wrap")
            property("align-items", "baseline")
            property("justify-content", "space-between")
            property("gap", "16px")
            property("padding", "28px 24px")
            property("border-top", "2px solid var(--pink)")
            property("background", "var(--red)")
            property("color", "var(--bg)")
        }
        "[data-theme=\"light\"] .footer" style {
            property("color", "#fff")
        }
        "[data-theme=\"light\"] .footer a" style {
            property("color", "#fff")
        }
        ".footer-big" style {
            property("font-size", "clamp(24px, 4vw, 44px)")
            property("font-weight", "900")
            property("letter-spacing", "-0.02em")
            property("text-transform", "uppercase")
        }
        ".footer-meta" style {
            property("font-family", "var(--mono)")
            property("font-size", "12px")
            property("letter-spacing", "0.12em")
        }
        ".cv-head" style {
            property("display", "flex")
            property("flex-wrap", "wrap")
            property("align-items", "flex-end")
            property("justify-content", "space-between")
            property("gap", "20px")
            property("padding", "56px 24px 32px")
        }
        ".cv-contact" style {
            property("display", "flex")
            property("flex-direction", "column")
            property("gap", "8px")
            property("font-family", "var(--mono)")
            property("font-size", "13px")
        }
        ".cv-contact span" style {
            property("color", "var(--mut)")
        }
        ".stack-groups" style {
            property("display", "grid")
            property("grid-template-columns", "repeat(auto-fit, minmax(240px, 1fr))")
            property("gap", "28px")
        }
    }
}
