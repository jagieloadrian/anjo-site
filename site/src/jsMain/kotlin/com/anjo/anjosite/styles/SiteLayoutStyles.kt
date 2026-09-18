package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

// Mechanically split out of the original single-file transcription of docs/handoff/styles.css
// (request: move CSS into Kotlin; then split that one 1200-line object into single-responsibility
// files). Every declaration is a literal, unreordered transcription via the raw
// property(name, value) escape hatch already used throughout this codebase — not a redesign.

// Page-level section/band grid layout (.screen, .band, .split, .labelled).
object SiteLayoutStyles : StyleSheet() {
    init {
        // ── screens + section grid ─────────────────────────────
        ".screen" style {
            property("position", "relative")
            property("z-index", "1")
        }
        ".screen[hidden]" style {
            property("display", "none")
        }
        ".band" style {
            property("position", "relative")
            property("z-index", "1")
            property("border-bottom", "2px solid var(--rule)")
        }
        ".band--strong" style {
            property("border-bottom-color", "var(--pink)")
        }
        ".band--pad" style {
            property("padding", "56px 24px 40px")
        }
        // Dividers are the 2px grid gap showing through, not the children's borders: correct at any column count, so no breakpoint has to guess the axis.
        ".split" style {
            property("display", "grid")
            property("grid-template-columns", "repeat(auto-fit, minmax(340px, 1fr))")
            property("gap", "2px")
            property("background", "var(--rule)")
        }
        ".split > *" style {
            property("min-width", "0")
            property("padding", "40px 24px")
            property("background", "var(--bg)")
        }
        ".labelled" style {
            property("display", "grid")
            property("grid-template-columns", "260px minmax(0, 1fr)")
            property("gap", "2px")
            property("background", "var(--rule)")
        }
        ".labelled > *" style {
            property("min-width", "0")
            property("padding", "40px 24px")
            property("background", "var(--bg)")
        }
        media("(max-width: 860px)") {
            ".labelled" style {
                property("grid-template-columns", "minmax(0, 1fr)")
            }
        }
    }
}
