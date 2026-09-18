package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.StyleSheet

object SiteCardStyles : StyleSheet() {
    init {
        ".cards" style {
            property("display", "grid")
            property("grid-template-columns", "repeat(auto-fit, minmax(320px, 1fr))")
            property("gap", "2px")
            property("background", "var(--rule-mid)")
            property("border-bottom", "2px solid var(--pink)")
        }
        ".card" style {
            property("padding", "32px 24px")
            property("min-width", "0")
            property("background", "var(--bg)")
        }
        ".card:hover" style {
            property("background", "var(--card-hover)")
        }
        ".card-top" style {
            property("display", "flex")
            property("align-items", "baseline")
            property("justify-content", "space-between")
            property("gap", "12px")
            property("font-family", "var(--mono)")
            property("font-size", "11px")
        }
        ".card-idx" style {
            property("letter-spacing", "0.18em")
        }
        ".card-kind" style {
            property("color", "var(--faint)")
        }
        ".card-title" style {
            property("display", "block")
            property("margin-top", "16px")
            property("padding", "0")
            property("border", "none")
            property("background", "transparent")
            property("text-align", "left")
            property("cursor", "default")
            property("color", "var(--ink)")
            property("font-family", "var(--sans)")
            property("font-size", "30px")
            property("font-weight", "900")
            property("line-height", "1.02")
            property("letter-spacing", "-0.02em")
            property("text-transform", "uppercase")
        }
        "button.card-title" style {
            property("cursor", "pointer")
        }
    }
}
