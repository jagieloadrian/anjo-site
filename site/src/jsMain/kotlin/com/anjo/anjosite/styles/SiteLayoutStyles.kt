package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.media

object SiteLayoutStyles : StyleSheet() {
    init {
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
