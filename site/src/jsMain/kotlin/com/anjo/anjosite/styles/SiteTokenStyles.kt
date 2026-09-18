package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.StyleSheet

object SiteTokenStyles : StyleSheet() {
    init {
        ":root" style {
            property("--bg", "#07070a")
            property("--ink", "#f2f0ef")
            property("--dim", "#cfcdcc")
            property("--mut", "#9b9a99")
            property("--faint", "#7c7c7a")
            property("--pink", "#ff2d95")
            property("--cyan", "#00e5ff")
            property("--red", "#ec3013")
            property("--red-lt", "#ff6b52")
            property("--note", "#d9ff00")
            property("--gold", "#e8c547")
            property("--silver", "#c8ccd1")
            property("--bronze", "#d98a4e")
            property("--platinum", "#9fe8ff")
            property("--rule", "rgba(255, 45, 149, 0.35)")
            property("--rule-soft", "rgba(242, 240, 239, 0.18)")
            property("--mono", "'JetBrains Mono', ui-monospace, monospace")
            property("--sans", "'Archivo', system-ui, sans-serif")
            property("color-scheme", "dark")
            property("--ink-2", "#e6e4e3")
            property("--rule-mid", "rgba(255, 45, 149, 0.5)")
            property("--rule-ink", "rgba(242, 240, 239, 0.18)")
            property("--rule-ink-mid", "rgba(242, 240, 239, 0.3)")
            property("--rule-ink-soft", "rgba(242, 240, 239, 0.1)")
            property("--rule-ink-strong", "rgba(242, 240, 239, 0.45)")
            property("--tint-0", "rgba(255, 45, 149, 0.04)")
            property("--tint-1", "rgba(255, 45, 149, 0.1)")
            property("--tint-2", "rgba(255, 45, 149, 0.12)")
            property("--tint-3", "rgba(255, 45, 149, 0.16)")
            property("--tint-red", "rgba(236, 48, 19, 0.14)")
            property("--tint-cyan", "rgba(0, 229, 255, 0.12)")
            property("--tint-note", "rgba(217, 255, 0, 0.06)")
            property("--panel", "rgba(10, 10, 14, 0.85)")
            property("--nav-bg", "rgba(7, 7, 10, 0.92)")
            property("--card-hover", "#14070f")
            property("--grid-line", "rgba(255, 45, 149, 0.07)")
            property("--glow-ring", "rgba(255, 45, 149, 0.25)")
            property("--glow-box", "rgba(255, 45, 149, 0.18)")
            property("--glow-text", "rgba(255, 45, 149, 0.45)")
            property("--scan-line", "rgba(0, 0, 0, 0.38)")
            property("--scan-op", "0.55")
            property("--vignette-edge", "rgba(0, 0, 0, 0.55)")
        }
        "[data-theme=\"light\"]" style {
            property("color-scheme", "light")
            property("--bg", "#f3f2f2")
            property("--ink", "#201e1d")
            property("--ink-2", "#2c2a28")
            property("--dim", "#3f3c3a")
            property("--mut", "#625f5c")
            property("--faint", "#6b6967")
            property("--pink", "#c20064")
            property("--cyan", "#00657f")
            property("--red", "#c9280f")
            property("--red-lt", "#b8280f")
            property("--note", "#566300")
            property("--gold", "#7d6200")
            property("--silver", "#5b5e63")
            property("--bronze", "#944e17")
            property("--platinum", "#006b85")
            property("--rule", "rgba(214, 0, 110, 0.45)")
            property("--rule-mid", "rgba(214, 0, 110, 0.65)")
            property("--rule-soft", "rgba(32, 30, 29, 0.2)")
            property("--rule-ink", "rgba(32, 30, 29, 0.2)")
            property("--rule-ink-mid", "rgba(32, 30, 29, 0.35)")
            property("--rule-ink-soft", "rgba(32, 30, 29, 0.14)")
            property("--rule-ink-strong", "rgba(32, 30, 29, 0.5)")
            property("--tint-0", "rgba(214, 0, 110, 0.04)")
            property("--tint-1", "rgba(214, 0, 110, 0.08)")
            property("--tint-2", "rgba(214, 0, 110, 0.1)")
            property("--tint-3", "rgba(214, 0, 110, 0.14)")
            property("--tint-red", "rgba(236, 48, 19, 0.1)")
            property("--tint-cyan", "rgba(0, 101, 127, 0.1)")
            property("--tint-note", "rgba(86, 99, 0, 0.09)")
            property("--panel", "rgba(255, 255, 255, 0.94)")
            property("--nav-bg", "rgba(243, 242, 242, 0.93)")
            property("--card-hover", "#faeaf1")
            property("--grid-line", "rgba(32, 30, 29, 0.07)")
            property("--glow-ring", "rgba(214, 0, 110, 0.2)")
            property("--glow-box", "rgba(214, 0, 110, 0.08)")
            property("--glow-text", "rgba(214, 0, 110, 0.14)")
            property("--scan-line", "rgba(32, 30, 29, 0.09)")
            property("--scan-op", "0.5")
            property("--vignette-edge", "rgba(32, 30, 29, 0.12)")
        }
        "*" style {
            property("box-sizing", "border-box")
        }
        "html, body" style {
            property("margin", "0")
            property("padding", "0")
            property("max-width", "100%")
            property("overflow-x", "hidden")
            property("background", "var(--bg)")
            property("color", "var(--ink)")
            property("font-family", "var(--sans)")
        }
        "a" style {
            property("color", "var(--pink)")
            property("text-decoration", "none")
        }
        "a:hover" style {
            property("color", "var(--red)")
        }
        "::selection" style {
            property("background", "var(--pink)")
            property("color", "var(--bg)")
        }
        "button:focus-visible, a:focus-visible, input:focus-visible" style {
            property("outline", "2px solid var(--pink)")
            property("outline-offset", "2px")
        }
    }
}
