package com.anjo.anjosite.components.layouts

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.anjo.anjosite.components.sections.Footer
import com.anjo.anjosite.components.sections.NavHeader
import com.varabyte.kobweb.core.PageContext
import com.varabyte.kobweb.core.data.getValue
import com.varabyte.kobweb.core.layout.Layout
import kotlinx.browser.document
import org.jetbrains.compose.web.dom.Main

class PageLayoutData(
    val title: String,
    val description: String,
    val ogImage: String = "/og-banner.png",
)

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

@Composable
@Layout
fun PageLayout(ctx: PageContext, content: @Composable () -> Unit) {
    val data = ctx.data.getValue<PageLayoutData>()
    LaunchedEffect(data.title, data.description, data.ogImage) {
        updatePageMeta(data.title, data.description, data.ogImage)
    }

    NavHeader()
    Main(attrs = { classes("screen"); style { property("flex", "1 0 auto") } }) {
        content()
    }
    Footer()
}
