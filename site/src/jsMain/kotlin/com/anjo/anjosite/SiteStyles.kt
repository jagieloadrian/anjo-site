package com.anjo.anjosite

import org.jetbrains.compose.web.css.*


// Mechanically generated from docs/handoff/styles.css (request: move CSS into Kotlin,
// no more mock changes planned). Every declaration below is a literal, unreordered
// transcription (via the raw property(name, value) escape hatch already used throughout
// this codebase) — not a redesign. Keep selectors/props/values byte-identical to the mock
// if this ever needs to be regenerated; see css2kt.py in this session's history.
object SiteStyles : StyleSheet() {
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
        ":root" style {
            property("--bg", "#07070a")
            property("--ink", "#f2f0ef")
            property("--dim", "#cfcdcc")
            property("--mut", "#9b9a99")
            property("--faint", "#6f6e6d")
            property("--pink", "#ff2d95")
            property("--cyan", "#00e5ff")
            property("--red", "#ec3013")
            property("--red-lt", "#ff6b52")
            property("--note", "#d9ff00")
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
        // ── light theme ────────────────────────────────────────── Same token names, re-pointed. Neon pink and cyan fail contrast on a light ground, so they move to deeper steps; the Modernist red is the one hue that holds on both. Glows and scanlines nearly vanish — on white they read as dirt, not light.
        "[data-theme=\"light\"]" style {
            property("color-scheme", "light")
            property("--bg", "#f3f2f2")
            property("--ink", "#201e1d")
            property("--ink-2", "#2c2a28")
            property("--dim", "#3f3c3a")
            property("--mut", "#625f5c")
            property("--faint", "#837f7c")
            property("--pink", "#d6006e")
            property("--cyan", "#00657f")
            property("--red", "#ec3013")
            property("--red-lt", "#b8280f")
            property("--note", "#566300")
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
        // ── overlays ───────────────────────────────────────────
        ".app" style {
            property("position", "relative")
            property("min-height", "100vh")
            property("overflow-x", "hidden")
        }
        ".fx-scan, .fx-vignette, .fx-grid" style {
            property("pointer-events", "none")
        }
        ".fx-scan" style {
            property("position", "fixed")
            property("inset", "0")
            property("z-index", "60")
            property("opacity", "var(--scan-op)")
            property("background-image", "repeating-linear-gradient(to bottom, var(--scan-line) 0px, var(--scan-line) 1px, transparent 1px, transparent 3px)")
        }
        ".fx-vignette" style {
            property("position", "fixed")
            property("inset", "0")
            property("z-index", "61")
            property("background", "radial-gradient(120% 80% at 50% 0%, transparent 40%, var(--vignette-edge) 100%)")
        }
        ".fx-grid" style {
            property("position", "absolute")
            property("inset", "0")
            property("z-index", "0")
            property("background-image", "linear-gradient(var(--grid-line) 1px, transparent 1px), linear-gradient(90deg, var(--grid-line) 1px, transparent 1px)")
            property("background-size", "96px 96px")
        }
        media("(prefers-reduced-motion: reduce)") {
            ".fx-scan" style {
                property("display", "none")
            }
            ".glitch:hover" style {
                property("animation", "none")
            }
            ".glitch:hover::after" style {
                property("content", "none")
            }
            ".caret" style {
                property("animation", "none")
            }
        }
        // ── glitch + caret ─────────────────────────────────────
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
        // ── header ─────────────────────────────────────────────
        ".nav" style {
            property("position", "sticky")
            property("top", "0")
            property("z-index", "50")
            property("display", "flex")
            property("align-items", "stretch")
            property("justify-content", "space-between")
            property("gap", "16px")
            property("flex-wrap", "wrap")
            property("border-bottom", "2px solid var(--pink)")
            property("background", "var(--nav-bg)")
            property("backdrop-filter", "blur(6px)")
        }
        ".brand" style {
            property("display", "flex")
            property("align-items", "center")
            property("gap", "12px")
            property("padding", "18px 24px")
            property("border", "none")
            property("border-right", "2px solid var(--rule)")
            property("background", "transparent")
            property("cursor", "pointer")
            property("font-family", "var(--sans)")
            property("font-weight", "900")
            property("font-size", "16px")
            property("letter-spacing", "0.12em")
            property("text-transform", "uppercase")
            property("color", "var(--ink)")
        }
        ".brand i" style {
            property("width", "12px")
            property("height", "12px")
            property("background", "var(--red)")
        }
        ".nav-group" style {
            property("display", "flex")
            property("align-items", "stretch")
            property("flex-wrap", "wrap")
            property("min-width", "0")
        }
        ".nav-btn" style {
            property("display", "flex")
            property("align-items", "center")
            property("padding", "18px")
            property("border", "none")
            property("border-left", "2px solid var(--rule)")
            property("background", "transparent")
            property("color", "var(--mut)")
            property("cursor", "pointer")
            property("font-family", "var(--mono)")
            property("font-size", "12px")
            property("letter-spacing", "0.16em")
            property("text-transform", "uppercase")
        }
        ".nav-btn:hover" style {
            property("color", "var(--ink)")
            property("background", "var(--tint-1)")
        }
        ".nav-btn.is-active" style {
            property("color", "var(--pink)")
            property("background", "var(--tint-3)")
        }
        ".lang" style {
            property("display", "flex")
            property("align-items", "center")
            property("border-left", "2px solid var(--rule)")
            property("padding", "0 12px")
        }
        ".lang-btn" style {
            property("padding", "8px 10px")
            property("border", "2px solid var(--pink)")
            property("background", "transparent")
            property("color", "var(--pink)")
            property("cursor", "pointer")
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("letter-spacing", "0.14em")
        }
        ".lang-btn + .lang-btn" style {
            property("border-left", "none")
        }
        ".lang-btn.is-active" style {
            property("background", "var(--pink)")
            property("color", "var(--bg)")
        }
        ".theme-btn" style {
            property("display", "flex")
            property("align-items", "center")
            property("gap", "8px")
            property("padding", "18px 16px")
            property("border", "none")
            property("border-left", "2px solid var(--rule)")
            property("background", "transparent")
            property("color", "var(--pink)")
            property("cursor", "pointer")
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("letter-spacing", "0.14em")
            property("text-transform", "uppercase")
            property("white-space", "nowrap")
        }
        ".theme-btn:hover" style {
            property("background", "var(--tint-2)")
        }
        ".notes-btn" style {
            property("padding", "18px 16px")
            property("border", "none")
            property("border-left", "2px solid var(--rule)")
            property("background", "transparent")
            property("color", "var(--note)")
            property("cursor", "pointer")
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("letter-spacing", "0.14em")
            property("text-transform", "uppercase")
        }
        ".notes-btn.is-on" style {
            property("background", "var(--note)")
            property("color", "var(--bg)")
        }
        // ── screens + section grid ─────────────────────────────
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
        // Dividers are the 2px grid gap showing through, not the children's borders: correct at any column count, so no breakpoint has to guess the axis.
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
        // ── type ───────────────────────────────────────────────
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
        // ── buttons ────────────────────────────────────────────
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
        // ── tags ───────────────────────────────────────────────
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
        // ── terminal ───────────────────────────────────────────
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
            property("color", "var(--ink)")
        }
        // ── stats ──────────────────────────────────────────────
        ".stats" style {
            property("display", "grid")
            property("grid-template-columns", "repeat(auto-fit, minmax(150px, 1fr))")
            property("border", "2px solid var(--rule-mid)")
        }
        ".stats--bare" style {
            property("border", "none")
        }
        ".stat" style {
            property("padding", "20px")
            property("border-right", "2px solid var(--rule)")
        }
        ".stat:last-child" style {
            property("border-right", "none")
        }
        ".stat-key" style {
            property("font-family", "var(--mono)")
            property("font-size", "10px")
            property("letter-spacing", "0.18em")
            property("color", "var(--mut)")
        }
        ".stat-num" style {
            property("font-size", "40px")
            property("font-weight", "900")
            property("line-height", "1.1")
            property("color", "var(--pink)")
        }
        ".stat-num--cyan" style {
            property("color", "var(--cyan)")
        }
        ".stat-num--red" style {
            property("color", "var(--red-lt)")
        }
        ".stat-num--lg" style {
            property("font-size", "46px")
        }
        ".facts" style {
            property("display", "flex")
            property("flex-wrap", "wrap")
            property("border", "2px solid var(--rule-soft)")
        }
        ".fact" style {
            property("flex", "1 1 120px")
            property("padding", "16px")
            property("border-right", "2px solid var(--rule-soft)")
        }
        ".fact:last-child" style {
            property("border-right", "none")
        }
        ".fact-val" style {
            property("font-size", "26px")
            property("font-weight", "800")
        }
        // ── cards (gap rules) ──────────────────────────────────
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
        // ── lists, timeline, links ─────────────────────────────
        ".tl" style {
            property("border-left", "2px solid var(--pink)")
            property("padding-left", "20px")
        }
        ".tl--soft" style {
            property("border-left-color", "var(--rule-mid)")
        }
        ".tl + .tl" style {
            property("margin-top", "28px")
        }
        ".tl-when" style {
            property("font-family", "var(--mono)")
            property("font-size", "12px")
            property("letter-spacing", "0.1em")
            property("color", "var(--red)")
        }
        ".tl-what" style {
            property("font-size", "22px")
            property("font-weight", "800")
            property("margin-top", "4px")
        }
        ".tl-where" style {
            property("font-family", "var(--mono)")
            property("font-size", "12px")
            property("color", "var(--mut)")
            property("margin-top", "4px")
        }
        ".tl-stack" style {
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("color", "var(--faint)")
            property("margin-top", "8px")
        }
        ".bullets" style {
            property("margin", "14px 0 0")
            property("padding-left", "20px")
            property("max-width", "70ch")
            property("font-size", "15px")
            property("line-height", "1.7")
            property("color", "var(--dim)")
        }
        ".links" style {
            property("display", "grid")
            property("grid-template-columns", "repeat(auto-fit, minmax(220px, 1fr))")
            property("border", "2px solid var(--rule-soft)")
        }
        ".links--stack" style {
            property("grid-template-columns", "minmax(0, 1fr)")
        }
        ".link-cell" style {
            property("display", "block")
            property("padding", "18px")
            property("color", "var(--ink)")
            property("border-right", "2px solid var(--rule-soft)")
        }
        ".link-cell:last-child" style {
            property("border-right", "none")
        }
        ".links--stack .link-cell" style {
            property("border-right", "none")
            property("border-bottom", "2px solid var(--rule-ink-soft)")
        }
        ".links--stack .link-cell:last-child" style {
            property("border-bottom", "none")
        }
        ".link-cell:hover" style {
            property("background", "var(--tint-2)")
            property("color", "var(--pink)")
        }
        ".link-key" style {
            property("font-family", "var(--mono)")
            property("font-size", "10px")
            property("letter-spacing", "0.18em")
            property("color", "var(--mut)")
            property("margin-bottom", "6px")
        }
        ".link-val" style {
            property("font-family", "var(--mono)")
            property("font-size", "14px")
            property("word-break", "break-all")
        }
        ".sheet" style {
            property("border", "2px solid var(--rule-soft)")
        }
        ".sheet-head" style {
            property("padding", "16px")
            property("border-bottom", "2px solid var(--rule-soft)")
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("letter-spacing", "0.18em")
            property("color", "var(--pink)")
        }
        ".sheet-row" style {
            property("display", "flex")
            property("justify-content", "space-between")
            property("gap", "16px")
            property("padding", "14px 16px")
            property("border-bottom", "2px solid var(--rule-ink-soft)")
            property("font-family", "var(--mono)")
            property("font-size", "13px")
        }
        ".sheet-row:last-child" style {
            property("border-bottom", "none")
        }
        ".sheet-row span:first-child" style {
            property("color", "var(--mut)")
        }
        ".slot" style {
            property("border", "2px dashed var(--rule-mid)")
            property("padding", "24px")
            property("background", "var(--tint-0)")
        }
        ".slot-key" style {
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("letter-spacing", "0.18em")
            property("color", "var(--pink)")
            property("margin-bottom", "8px")
        }
        ".slot p" style {
            property("margin", "0")
            property("font-size", "15px")
            property("line-height", "1.5")
            property("color", "var(--dim)")
        }
        ".covers" style {
            property("display", "grid")
            property("grid-template-columns", "repeat(auto-fit, minmax(150px, 1fr))")
            property("gap", "2px")
            property("background", "var(--rule-soft)")
            property("border", "2px solid var(--rule-soft)")
        }
        ".cover" style {
            property("padding", "18px")
            property("background", "var(--bg)")
        }
        ".cover-img" style {
            property("width", "100%")
            property("aspect-ratio", "1")
            property("margin-bottom", "12px")
            property("border", "2px dashed var(--rule-mid)")
            property("display", "flex")
            property("align-items", "center")
            property("justify-content", "center")
            property("font-family", "var(--mono)")
            property("font-size", "10px")
            property("color", "var(--faint)")
        }
        ".cover-name" style {
            property("font-size", "15px")
            property("font-weight", "700")
            property("line-height", "1.25")
        }
        ".cover-pct" style {
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("color", "var(--pink)")
            property("margin-top", "6px")
        }
        ".cover-pct--mut" style {
            property("color", "var(--mut)")
        }
        ".feed" style {
            property("border", "2px solid var(--rule-soft)")
        }
        ".feed-row" style {
            property("display", "flex")
            property("gap", "14px")
            property("align-items", "center")
            property("padding", "14px 16px")
            property("border-bottom", "2px solid var(--rule-ink-soft)")
        }
        ".feed-row:last-child" style {
            property("border-bottom", "none")
        }
        ".feed-icon" style {
            property("width", "42px")
            property("height", "42px")
            property("flex", "none")
            property("border", "2px solid var(--rule-ink-mid)")
        }
        ".feed-icon--pink" style {
            property("border-color", "var(--pink)")
            property("background", "var(--tint-2)")
        }
        ".feed-name" style {
            property("font-family", "var(--mono)")
            property("font-size", "13px")
            property("color", "var(--ink)")
        }
        ".feed-meta" style {
            property("font-family", "var(--mono)")
            property("font-size", "11px")
            property("color", "var(--mut)")
            property("margin-top", "3px")
        }
        // ── design notes ───────────────────────────────────────
        ".note" style {
            property("display", "none")
            property("margin-top", "28px")
            property("padding", "12px 16px")
            property("border-left", "2px solid var(--note)")
            property("background", "var(--tint-note)")
        }
        "body.notes-on .note" style {
            property("display", "block")
        }
        ".note-key" style {
            property("font-family", "var(--mono)")
            property("font-size", "10px")
            property("letter-spacing", "0.2em")
            property("color", "var(--note)")
            property("margin-bottom", "6px")
        }
        ".note p" style {
            property("margin", "0")
            property("max-width", "90ch")
            property("font-size", "13px")
            property("line-height", "1.5")
            property("color", "var(--dim)")
        }
        ".note--loose" style {
            property("margin", "24px")
        }
        // ── language switch ────────────────────────────────────
        "[data-lang-block=\"pl\"]" style {
            property("display", "none")
        }
        "body[data-lang=\"pl\"] [data-lang-block=\"pl\"]" style {
            property("display", "block")
        }
        "body[data-lang=\"pl\"] [data-lang-block=\"en\"]" style {
            property("display", "none")
        }
        // ── footer ─────────────────────────────────────────────
        ".footer" style {
            property("position", "relative")
            property("z-index", "1")
            property("display", "flex")
            property("flex-wrap", "wrap")
            property("align-items", "baseline")
            property("justify-content", "space-between")
            property("gap", "16px")
            property("padding", "28px 24px")
            property("border-top", "2px solid var(--pink)")
            property("background", "var(--red)")
            property("color", "var(--bg)")
        }
        "[data-theme=\"light\"] .footer" style {
            property("color", "#fff")
        }
        "[data-theme=\"light\"] .footer a" style {
            property("color", "#fff")
        }
        ".footer-big" style {
            property("font-size", "clamp(24px, 4vw, 44px)")
            property("font-weight", "900")
            property("letter-spacing", "-0.02em")
            property("text-transform", "uppercase")
        }
        ".footer-meta" style {
            property("font-family", "var(--mono)")
            property("font-size", "12px")
            property("letter-spacing", "0.12em")
        }
        ".cv-head" style {
            property("display", "flex")
            property("flex-wrap", "wrap")
            property("align-items", "flex-end")
            property("justify-content", "space-between")
            property("gap", "20px")
            property("padding", "56px 24px 32px")
        }
        ".cv-contact" style {
            property("display", "flex")
            property("flex-direction", "column")
            property("gap", "8px")
            property("font-family", "var(--mono)")
            property("font-size", "13px")
        }
        ".cv-contact span" style {
            property("color", "var(--mut)")
        }
        ".stack-groups" style {
            property("display", "grid")
            property("grid-template-columns", "repeat(auto-fit, minmax(240px, 1fr))")
            property("gap", "28px")
        }
        // ── mobile ─────────────────────────────────────────────
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
        // ── anchor-as-button fixup (specs/004-pages, not in the original mock) ── The mock's .btn is a bare-default <button> (browser default display: inline-block); this codebase always renders real navigation as <a> via Kobweb's Link (real routing, no data-route JS), and a bare <a> defaults to display: inline, which makes margin-top/bottom no-ops and caused a real overlap bug against preceding text.
        ".btn, .btn--link" style {
            property("display", "inline-block")
        }
        // ── print (specs/004-pages, not in the original mock) ─── Mock is a single always-dark theme with no print styles of its own; a real printout still needs a light/ink-friendly page, so this block only fires for @media print.
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
