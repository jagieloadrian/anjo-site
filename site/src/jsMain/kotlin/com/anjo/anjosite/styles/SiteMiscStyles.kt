package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

object SiteMiscStyles : StyleSheet() {
    init {
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
