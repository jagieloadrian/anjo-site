package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.*

// Mechanically split out of the original single-file transcription of docs/handoff/styles.css
// (request: move CSS into Kotlin; then split that one 1200-line object into single-responsibility
// files). Every declaration is a literal, unreordered transcription via the raw
// property(name, value) escape hatch already used throughout this codebase — not a redesign.

// This codebase's own deviations from the mock: the anchor-as-button display fixup and print styles (the mock has neither).
object SiteOverrideStyles : StyleSheet() {
    init {
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
