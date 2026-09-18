package com.anjo.anjosite.pages.projects

import com.anjo.anjosite.BilingualString
import com.anjo.anjosite.pages.ProjectEntry
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SlugTest {
    private fun entry(slug: String) = ProjectEntry(
        slug = slug,
        kind = "App",
        title = BilingualString(en = slug, pl = slug),
        shortDescription = BilingualString(en = "", pl = ""),
        fullDescription = BilingualString(en = "", pl = ""),
        tags = emptyList(),
        coverImageAlt = BilingualString(en = "", pl = ""),
        platform = "JVM",
        status = BilingualString(en = "Active", pl = "Aktywny"),
    )

    private val entries = listOf(entry("foo"), entry("bar"))

    @Test
    fun findProjectBySlug_returnsMatch() {
        assertEquals("bar", findProjectBySlug(entries, "bar")?.slug)
    }

    @Test
    fun findProjectBySlug_unknownSlugReturnsNull() {
        assertNull(findProjectBySlug(entries, "unknown"))
    }

    @Test
    fun findProjectBySlug_emptyListReturnsNull() {
        assertNull(findProjectBySlug(emptyList(), "foo"))
    }

    @Test
    fun findProjectBySlug_nullSlugReturnsNull() {
        assertNull(findProjectBySlug(entries, null))
    }
}
