package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.StyleSheet

object SiteTerminalStyles : StyleSheet() {
    init {
        ".term" style {
            property("border", "2px solid var(--pink)")
            property("background", "var(--panel)")
            property("box-shadow", "0 0 0 1px var(--glow-ring), 0 0 40px var(--glow-box)")
        }
        ".term-bar" style {
            property("display", "flex")
            property("align-items", "center")
            property("justify-content", "space-between")
            property("gap", "12px")
            property("padding", "10px 14px")
            property("border-bottom", "2px solid var(--pink)")
            property("background", "var(--tint-2)")
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("letter-spacing", "0.14em")
            property("color", "var(--pink)")
        }
        ".term-dots" style {
            property("display", "flex")
            property("gap", "6px")
        }
        ".term-dots i" style {
            property("width", "10px")
            property("height", "10px")
        }
        ".term-dots i:nth-child(1)" style {
            property("background", "var(--red)")
        }
        ".term-dots i:nth-child(2)" style {
            property("background", "var(--pink)")
        }
        ".term-dots i:nth-child(3)" style {
            property("background", "var(--cyan)")
        }
        ".term-body" style {
            property("padding", "20px 16px")
            property("min-height", "320px")
            property("font-family", "var(--mono)")
            property("font-size", "13px")
            property("line-height", "1.85")
        }
        ".term-body p" style {
            property("margin", "0")
            property("white-space", "pre-wrap")
            property("word-break", "break-word")
        }
        ".term-body .t-cmd" style {
            property("color", "var(--faint)")
        }
        ".term-body .t-out" style {
            property("color", "var(--ink)")
        }
        ".term-body .t-pink" style {
            property("color", "var(--pink)")
        }
        ".term-body .t-cyan" style {
            property("color", "var(--cyan)")
        }
        ".term-input" style {
            property("display", "flex")
            property("align-items", "center")
            property("gap", "8px")
            property("padding", "12px 16px")
            property("border-top", "2px solid var(--rule-mid)")
        }
        ".term-input span" style {
            property("font-family", "var(--mono)")
            property("font-size", "14px")
            property("color", "var(--pink)")
        }
        ".term-input input" style {
            property("flex", "1")
            property("min-width", "0")
            property("background", "transparent")
            property("border", "none")
            property("outline", "none")
            property("font-family", "var(--mono)")
            property("font-size", "14px")
            property("color", "#ffffff")
        }
        "[data-theme=\"light\"] .term-input input" style {
            property("color", "var(--ink)")
        }
    }
}
