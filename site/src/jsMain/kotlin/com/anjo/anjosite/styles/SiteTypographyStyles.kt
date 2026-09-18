package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

object SiteTypographyStyles : StyleSheet() {
    init {
        ".kicker" style {
            property("font-family", "var(--mono)")
            property("font-size", "12px")
            property("letter-spacing", "0.24em")
            property("text-transform", "uppercase")
            property("color", "var(--red)")
        }
        ".label" style {
            property("margin", "0")
            property("font-family", "var(--mono)")
            property("font-size", "13px")
            property("letter-spacing", "0.24em")
            property("text-transform", "uppercase")
            property("color", "var(--pink)")
        }
        ".label--sm" style {
            property("font-size", "11px")
            property("letter-spacing", "0.2em")
        }
        ".label--cyan" style {
            property("color", "var(--cyan)")
        }
        ".label--red" style {
            property("color", "var(--red-lt)")
        }
        ".display" style {
            property("margin", "0")
            property("font-weight", "900")
            property("letter-spacing", "-0.03em")
            property("line-height", "0.9")
            property("text-transform", "uppercase")
            property("font-size", "clamp(40px, 6vw, 88px)")
        }
        ".display--xl" style {
            property("font-size", "clamp(44px, 7vw, 104px)")
            property("line-height", "0.88")
        }
        ".display em" style {
            property("font-style", "normal")
            property("color", "var(--pink)")
        }
        ".display em.cyan" style {
            property("color", "var(--cyan)")
        }
        ".display em.red" style {
            property("color", "var(--red-lt)")
        }
        ".display em.glow" style {
            property("text-shadow", "0 0 24px var(--glow-text)")
        }
        ".lead" style {
            property("margin", "0 0 18px")
            property("max-width", "62ch")
            property("font-size", "19px")
            property("line-height", "1.6")
            property("color", "var(--ink)")
            property("text-wrap", "pretty")
        }
        ".body" style {
            property("margin", "0 0 18px")
            property("max-width", "62ch")
            property("font-size", "16px")
            property("line-height", "1.65")
            property("color", "var(--dim)")
            property("text-wrap", "pretty")
        }
        ".body--sm" style {
            property("font-size", "15px")
        }
        ".meta" style {
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("line-height", "1.7")
            property("color", "var(--faint)")
        }
        ".rule" style {
            property("height", "2px")
            property("background", "var(--pink)")
            property("margin", "32px 0 28px")
        }
        ".rule--soft" style {
            property("background", "var(--rule)")
        }
    }
}
