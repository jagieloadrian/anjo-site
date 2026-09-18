package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.media

object SiteOverrideStyles : StyleSheet() {
    init {
        ".btn, .btn--link" style {
            property("display", "inline-block")
        }
        media("print") {
            ".fx-scan, .fx-vignette, .fx-grid, .nav, .footer" style {
                property("display", "none")
            }
            "html, body" style {
                property("background", "#fff")
                property("color", "#000")
            }
            ".display em, .kicker, .label, a" style {
                property("color", "#000")
            }
            ".body, .lead, .meta, .tl-stack, .tl-where, .tl-when, .stat-key, .link-key" style {
                property("color", "#333")
            }
            ".tag, .term, .stats, .facts, .links, .sheet, .feed, .covers, .cover-img, .slot" style {
                property("border-color", "#999")
            }
            ".term-bar" style {
                property("background", "transparent")
                property("color", "#000")
            }
        }
    }
}
