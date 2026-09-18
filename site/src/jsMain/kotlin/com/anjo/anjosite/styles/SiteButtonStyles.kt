package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

object SiteButtonStyles : StyleSheet() {
    init {
        ".btn" style {
            property("font-family", "var(--mono)")
            property("font-size", "13px")
            property("letter-spacing", "0.12em")
            property("text-transform", "uppercase")
            property("padding", "14px 20px")
            property("cursor", "pointer")
            property("border", "2px solid var(--pink)")
            property("background", "var(--pink)")
            property("color", "var(--bg)")
            property("font-weight", "700")
            property("text-align", "left")
        }
        ".btn:hover" style {
            property("background", "var(--bg)")
            property("color", "var(--pink)")
        }
        ".btn--ghost" style {
            property("background", "transparent")
            property("color", "var(--ink)")
            property("border-color", "var(--rule-ink-strong)")
            property("font-weight", "400")
        }
        ".btn--ghost:hover" style {
            property("border-color", "var(--red)")
            property("color", "var(--red)")
            property("background", "transparent")
        }
        ".btn--link" style {
            property("padding", "0")
            property("border", "none")
            property("background", "transparent")
            property("color", "var(--mut)")
            property("font-size", "12px")
            property("letter-spacing", "0.1em")
            property("font-weight", "400")
        }
        ".btn--link:hover" style {
            property("color", "var(--pink)")
            property("background", "transparent")
        }
        ".btn--sm" style {
            property("font-size", "12px")
            property("padding", "13px 18px")
        }
        ".btn--outline" style {
            property("background", "transparent")
            property("color", "var(--pink)")
        }
        ".btn--outline:hover" style {
            property("background", "var(--pink)")
            property("color", "var(--bg)")
        }
    }
}
