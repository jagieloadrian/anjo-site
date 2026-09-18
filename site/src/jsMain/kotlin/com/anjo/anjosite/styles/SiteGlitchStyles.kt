package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.percent

object SiteGlitchStyles : StyleSheet() {
    val glitchShift by keyframes {
        each(0.percent, 100.percent) {
            property("transform", "translate(0)")
        }
        each(20.percent) {
            property("transform", "translate(-2px, 1px)")
        }
        each(40.percent) {
            property("transform", "translate(2px, -1px)")
        }
        each(60.percent) {
            property("transform", "translate(-1px, -1px)")
        }
        each(80.percent) {
            property("transform", "translate(1px, 1px)")
        }
    }
    val caretBlink by keyframes {
        each(0.percent, 49.percent) {
            property("opacity", "1")
        }
        each(50.percent, 100.percent) {
            property("opacity", "0")
        }
    }

    init {
        ".glitch" style {
            property("position", "relative")
            property("display", "inline-block")
        }
        ".glitch:hover" style {
            property("animation", "${glitchShift.name} 0.22s steps(2) infinite")
        }
        ".glitch:hover::after" style {
            property("content", "attr(data-text)")
            property("position", "absolute")
            property("left", "2px")
            property("top", "0")
            property("color", "var(--cyan)")
            property("opacity", "0.7")
            property("clip-path", "inset(0 0 55% 0)")
        }
        ".caret" style {
            property("display", "inline-block")
            property("width", "9px")
            property("height", "16px")
            property("background", "var(--pink)")
            property("vertical-align", "-3px")
            property("animation", "${caretBlink.name} 1s steps(1) infinite")
        }
    }
}
