package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

// Mechanically split out of the original single-file transcription of docs/handoff/styles.css
// (request: move CSS into Kotlin; then split that one 1200-line object into single-responsibility
// files). Every declaration is a literal, unreordered transcription via the raw
// property(name, value) escape hatch already used throughout this codebase — not a redesign.

// Header/nav bar: brand, nav links, language switch, theme toggle.
object SiteNavStyles : StyleSheet() {
    init {
        // ── header ─────────────────────────────────────────────
        ".nav" style {
            property("position", "sticky")
            property("top", "0")
            property("z-index", "50")
            property("display", "flex")
            property("align-items", "stretch")
            property("justify-content", "space-between")
            property("gap", "16px")
            property("flex-wrap", "wrap")
            property("border-bottom", "2px solid var(--pink)")
            property("background", "var(--nav-bg)")
            property("backdrop-filter", "blur(6px)")
        }
        ".brand" style {
            property("display", "flex")
            property("align-items", "center")
            property("gap", "12px")
            property("padding", "18px 24px")
            property("border", "none")
            property("border-right", "2px solid var(--rule)")
            property("background", "transparent")
            property("cursor", "pointer")
            property("font-family", "var(--sans)")
            property("font-weight", "900")
            property("font-size", "16px")
            property("letter-spacing", "0.12em")
            property("text-transform", "uppercase")
            property("color", "var(--ink)")
        }
        ".brand i" style {
            property("width", "12px")
            property("height", "12px")
            property("background", "var(--red)")
        }
        ".nav-group" style {
            property("display", "flex")
            property("align-items", "stretch")
            property("flex-wrap", "wrap")
            property("min-width", "0")
        }
        ".nav-btn" style {
            property("display", "flex")
            property("align-items", "center")
            property("padding", "18px")
            property("border", "none")
            property("border-left", "2px solid var(--rule)")
            property("background", "transparent")
            property("color", "var(--mut)")
            property("cursor", "pointer")
            property("font-family", "var(--mono)")
            property("font-size", "12px")
            property("letter-spacing", "0.16em")
            property("text-transform", "uppercase")
        }
        ".nav-btn:hover" style {
            property("color", "var(--ink)")
            property("background", "var(--tint-1)")
        }
        ".nav-btn.is-active" style {
            property("color", "var(--pink)")
            property("background", "var(--tint-3)")
        }
        ".lang" style {
            property("display", "flex")
            property("align-items", "center")
            property("border-left", "2px solid var(--rule)")
            property("padding", "0 12px")
        }
        ".lang-btn" style {
            property("padding", "8px 10px")
            property("border", "2px solid var(--pink)")
            property("background", "transparent")
            property("color", "var(--pink)")
            property("cursor", "pointer")
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("letter-spacing", "0.14em")
        }
        ".lang-btn + .lang-btn" style {
            property("border-left", "none")
        }
        ".lang-btn.is-active" style {
            property("background", "var(--pink)")
            property("color", "var(--bg)")
        }
        ".theme-btn" style {
            property("display", "flex")
            property("align-items", "center")
            property("gap", "8px")
            property("padding", "18px 16px")
            property("border", "none")
            property("border-left", "2px solid var(--rule)")
            property("background", "transparent")
            property("color", "var(--pink)")
            property("cursor", "pointer")
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("letter-spacing", "0.14em")
            property("text-transform", "uppercase")
            property("white-space", "nowrap")
        }
        ".theme-btn:hover" style {
            property("background", "var(--tint-2)")
        }
        ".notes-btn" style {
            property("padding", "18px 16px")
            property("border", "none")
            property("border-left", "2px solid var(--rule)")
            property("background", "transparent")
            property("color", "var(--note)")
            property("cursor", "pointer")
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("letter-spacing", "0.14em")
            property("text-transform", "uppercase")
        }
        ".notes-btn.is-on" style {
            property("background", "var(--note)")
            property("color", "var(--bg)")
        }
    }
}
