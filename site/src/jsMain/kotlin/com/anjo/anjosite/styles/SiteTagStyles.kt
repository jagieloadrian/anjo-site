package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.StyleSheet

object SiteTagStyles : StyleSheet() {
    init {
        ".tags" style {
            property("display", "flex")
            property("flex-wrap", "wrap")
            property("gap", "8px")
        }
        ".tag" style {
            property("font-family", "var(--mono)")
            property("font-size", "12px")
            property("padding", "7px 11px")
            property("border", "2px solid var(--rule-ink-mid)")
            property("color", "var(--ink-2)")
        }
        ".tag--pink" style {
            property("border-color", "var(--pink)")
            property("color", "var(--pink)")
            property("background", "var(--tint-1)")
        }
        ".tag--cyan" style {
            property("border-color", "var(--cyan)")
            property("color", "var(--cyan)")
            property("background", "var(--tint-cyan)")
        }
        ".tag--red" style {
            property("border-color", "var(--red)")
            property("color", "var(--red-lt)")
            property("background", "var(--tint-red)")
        }
        ".tag--sm" style {
            property("font-size", "11px")
            property("padding", "6px 10px")
        }
    }
}
