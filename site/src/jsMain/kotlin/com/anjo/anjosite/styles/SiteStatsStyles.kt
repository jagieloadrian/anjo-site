package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

// Mechanically split out of the original single-file transcription of docs/handoff/styles.css
// (request: move CSS into Kotlin; then split that one 1200-line object into single-responsibility
// files). Every declaration is a literal, unreordered transcription via the raw
// property(name, value) escape hatch already used throughout this codebase — not a redesign.

// Stat grid cells (trophy counts, etc).
object SiteStatsStyles : StyleSheet() {
    init {
        // ── stats ──────────────────────────────────────────────
        ".stats" style {
            property("display", "grid")
            property("grid-template-columns", "repeat(auto-fit, minmax(150px, 1fr))")
            property("border", "2px solid var(--rule-mid)")
        }
        ".stats--bare" style {
            property("border", "none")
        }
        ".stat" style {
            property("padding", "20px")
            property("border-right", "2px solid var(--rule)")
        }
        ".stat:last-child" style {
            property("border-right", "none")
        }
        ".stat-key" style {
            property("font-family", "var(--mono)")
            property("font-size", "10px")
            property("letter-spacing", "0.18em")
            property("color", "var(--mut)")
        }
        ".stat-num" style {
            property("font-size", "40px")
            property("font-weight", "900")
            property("line-height", "1.1")
            property("color", "var(--pink)")
        }
        ".stat-num--cyan" style {
            property("color", "var(--cyan)")
        }
        ".stat-num--red" style {
            property("color", "var(--red-lt)")
        }
        ".stat-num--lg" style {
            property("font-size", "46px")
        }
        ".facts" style {
            property("display", "flex")
            property("flex-wrap", "wrap")
            property("border", "2px solid var(--rule-soft)")
        }
        ".fact" style {
            property("flex", "1 1 120px")
            property("padding", "16px")
            property("border-right", "2px solid var(--rule-soft)")
        }
        ".fact:last-child" style {
            property("border-right", "none")
        }
        ".fact-val" style {
            property("font-size", "26px")
            property("font-weight", "800")
        }
    }
}
