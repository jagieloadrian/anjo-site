package com.anjo.anjosite.styles

import org.jetbrains.compose.web.css.StyleSheet

object SiteTimelineListStyles : StyleSheet() {
    init {
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
            property("grid-template-columns", "repeat(auto-fit, minmax(170px, 1fr))")
            property("gap", "10px")
        }
        ".cover" style {
            property("padding", "18px")
            property("background", "var(--bg)")
            property("border", "2px solid var(--rule-soft)")
        }
        ".cover-img" style {
            property("width", "100%")
            property("aspect-ratio", "1")
            property("margin-bottom", "12px")
            property("border", "2px dashed var(--rule-mid)")
            property("background", "var(--tint-0)")
            property("display", "flex")
            property("align-items", "center")
            property("justify-content", "center")
            property("overflow", "hidden")
            property("font-family", "var(--mono)")
            property("font-size", "10px")
            property("color", "var(--faint)")
        }
        ".cover-img img" style {
            property("width", "100%")
            property("height", "100%")
            property("object-fit", "contain")
            property("display", "block")
        }
        ".cover-name" style {
            property("font-size", "15px")
            property("font-weight", "700")
            property("line-height", "1.25")
            property("overflow", "hidden")
            property("text-overflow", "ellipsis")
            property("display", "-webkit-box")
            property("-webkit-line-clamp", "2")
            property("-webkit-box-orient", "vertical")
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
            property("overflow", "hidden")
            property("border", "2px solid var(--rule-ink-mid)")
        }
        ".feed-icon img" style {
            property("width", "100%")
            property("height", "100%")
            property("object-fit", "cover")
            property("display", "block")
        }
        ".feed-icon--bronze" style {
            property("border-color", "var(--bronze)")
            property("background", "var(--tint-2)")
        }
        ".feed-icon--silver" style {
            property("border-color", "var(--silver)")
            property("background", "var(--tint-2)")
        }
        ".feed-icon--gold" style {
            property("border-color", "var(--gold)")
            property("background", "var(--tint-2)")
        }
        ".feed-icon--platinum" style {
            property("border-color", "var(--platinum)")
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
    }
}
