package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

object SiteResponsiveStyles : StyleSheet() {
    init {
        media("(max-width: 720px)") {
            ".nav" style {
                property("flex-direction", "column")
                property("gap", "0")
                property("width", "100%")
            }
            ".brand" style {
                property("border-right", "none")
                property("border-bottom", "2px solid var(--rule)")
                property("padding", "16px 20px")
                property("font-size", "14px")
            }
            ".nav-group" style {
                property("flex-wrap", "nowrap")
                property("width", "100%")
                property("min-width", "0")
                property("max-width", "100%")
                property("overflow-x", "auto")
                property("overscroll-behavior-x", "contain")
                property("-webkit-overflow-scrolling", "touch")
                property("scrollbar-width", "none")
            }
            ".nav-group::-webkit-scrollbar" style {
                property("display", "none")
            }
            ".nav-group > *" style {
                property("flex", "none")
            }
            ".nav-btn" style {
                property("padding", "14px")
                property("white-space", "nowrap")
                property("min-height", "48px")
            }
            ".nav-btn:first-child" style {
                property("border-left", "none")
            }
            ".lang" style {
                property("padding", "0 10px")
            }
            ".theme-btn" style {
                property("padding", "14px")
                property("white-space", "nowrap")
                property("min-height", "48px")
            }
            ".notes-btn" style {
                property("padding", "14px")
                property("white-space", "nowrap")
                property("min-height", "48px")
            }
            ".band--pad" style {
                property("padding", "36px 20px 28px")
            }
            ".split > *, .labelled > *, .cv-head" style {
                property("padding", "28px 20px")
            }
            ".split" style {
                property("grid-template-columns", "minmax(0, 1fr)")
            }
            ".card" style {
                property("padding", "24px 20px")
            }
            ".card-title" style {
                property("font-size", "26px")
            }
            ".cards" style {
                property("grid-template-columns", "minmax(0, 1fr)")
            }
            ".covers" style {
                property("grid-template-columns", "repeat(2, minmax(0, 1fr))")
            }
            ".stats" style {
                property("grid-template-columns", "minmax(0, 1fr) !important")
            }
            ".stat, .stat[style]" style {
                property("padding", "18px 20px")
                property("border-right", "none")
                property("border-bottom", "2px solid var(--rule)")
            }
            ".stats > .stat:last-child" style {
                property("border-bottom", "none")
            }
            ".fact" style {
                property("border-right", "none")
                property("border-bottom", "2px solid var(--rule-soft)")
                property("flex", "1 1 100%")
            }
            ".fact:last-child" style {
                property("border-bottom", "none")
            }
            ".links" style {
                property("grid-template-columns", "minmax(0, 1fr)")
            }
            ".link-cell" style {
                property("border-right", "none")
                property("border-bottom", "2px solid var(--rule-soft)")
            }
            ".link-cell:last-child" style {
                property("border-bottom", "none")
            }
            ".term-body" style {
                property("min-height", "240px")
                property("font-size", "12px")
            }
            ".btn" style {
                property("min-height", "48px")
                property("display", "inline-flex")
                property("align-items", "center")
            }
            ".footer" style {
                property("padding", "24px 20px")
            }
            ".note--loose" style {
                property("margin", "20px")
            }
        }
        media("(max-width: 460px)") {
            ".brand" style {
                property("font-size", "13px")
                property("letter-spacing", "0.08em")
            }
            ".display" style {
                property("font-size", "clamp(34px, 12vw, 52px)")
            }
            ".display--xl" style {
                property("font-size", "clamp(38px, 14vw, 60px)")
            }
            ".lead" style {
                property("font-size", "17px")
            }
            ".card-title" style {
                property("font-size", "23px")
            }
            ".covers" style {
                property("grid-template-columns", "minmax(0, 1fr)")
            }
            ".cv-head" style {
                property("padding", "36px 20px 24px")
            }
            ".footer-big" style {
                property("font-size", "22px")
            }
        }
    }
}
