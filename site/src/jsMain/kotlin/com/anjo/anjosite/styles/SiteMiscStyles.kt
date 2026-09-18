package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

// Mechanically split out of the original single-file transcription of docs/handoff/styles.css
// (request: move CSS into Kotlin; then split that one 1200-line object into single-responsibility
// files). Every declaration is a literal, unreordered transcription via the raw
// property(name, value) escape hatch already used throughout this codebase — not a redesign.

// Design-note callouts and bilingual content visibility toggling.
object SiteMiscStyles : StyleSheet() {
    init {
        // ── design notes ───────────────────────────────────────
        ".note" style {
            property("display", "none")
            property("margin-top", "28px")
            property("padding", "12px 16px")
            property("border-left", "2px solid var(--note)")
            property("background", "var(--tint-note)")
        }
        "body.notes-on .note" style {
            property("display", "block")
        }
        ".note-key" style {
            property("font-family", "var(--mono)")
            property("font-size", "10px")
            property("letter-spacing", "0.2em")
            property("color", "var(--note)")
            property("margin-bottom", "6px")
        }
        ".note p" style {
            property("margin", "0")
            property("max-width", "90ch")
            property("font-size", "13px")
            property("line-height", "1.5")
            property("color", "var(--dim)")
        }
        ".note--loose" style {
            property("margin", "24px")
        }
        // ── language switch ────────────────────────────────────
        "[data-lang-block=\"pl\"]" style {
            property("display", "none")
        }
        "body[data-lang=\"pl\"] [data-lang-block=\"pl\"]" style {
            property("display", "block")
        }
        "body[data-lang=\"pl\"] [data-lang-block=\"en\"]" style {
            property("display", "none")
        }
    }
}
