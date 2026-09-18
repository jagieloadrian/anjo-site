package com.anjo.anjosite.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.layout.Layout
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Main
import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.LocalLang
import com.anjo.anjosite.components.sections.Footer
import com.anjo.anjosite.components.sections.NavHeader

class PageLayoutData(
    val title: String,
    val description: BilingualString,
    val ogImage: String = "/og-banner.png",
)

// specs/007-tests-polishing data-model.md: sets document.title and creates/updates the SEO/OG
// <head> elements. Called from PageLayout's own LaunchedEffect below (every page, using its
// static PageLayoutData) and a second time from Slug.kt's SlugPage() once its runtime fetch
// resolves — the one route whose real title/description/og:image can't be known at static
// @InitRoute time (research.md §4: verified empirically that Kobweb's static export snapshots
// the DOM *after* these LaunchedEffect-driven mutations run, so both call sites land in the real
// exported HTML).
fun updatePageMeta(title: String, description: String, ogImage: String) {
    val fullTitle = "Adrian Jagieło — $title"
    document.title = fullTitle

    fun upsertMeta(attrName: String, attrValue: String, content: String) {
        val selector = "meta[$attrName=\"$attrValue\"]"
        val existing = document.head?.querySelector(selector)
        if (existing != null) {
            existing.setAttribute("content", content)
        } else {
            val meta = document.createElement("meta")
            meta.setAttribute(attrName, attrValue)
            meta.setAttribute("content", content)
            document.head?.appendChild(meta)
        }
    }

    upsertMeta("name", "description", description)
    upsertMeta("property", "og:title", fullTitle)
    upsertMeta("property", "og:description", description)
    upsertMeta("property", "og:type", "website")
    upsertMeta("property", "og:image", ogImage)
}

// Each page renders its own full-width <section class="band ..."> children directly (mock's
// bands touch the viewport edges with internal grid-gap dividers — a centered max-width column
// wrapper, as this file had before, doesn't match and was never in the mock).
@Composable
@Layout
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
    val data = ctx.data.getValue<PageLayoutData>()
    val lang = LocalLang.current
    LaunchedEffect(data.title, data.description, data.ogImage, lang) {
        updatePageMeta(data.title, data.description(lang), data.ogImage)
    }

    NavHeader()
    Main(attrs = { classes("screen"); style { property("flex", "1 0 auto") } }) {
        content()
    }
    Footer()
}
