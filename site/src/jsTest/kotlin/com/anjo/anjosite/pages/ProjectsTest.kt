package com.anjo.anjosite.pages

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProjectsTest {
    private val fixture = """
        {
          "projects": [
            {
              "slug": "foo-bar",
              "kind": "App",
              "title": "Foo Bar",
              "shortDescription": "A short one",
              "fullDescription": "A long one",
              "tags": ["Kotlin", "Compose"],
              "coverImageUrl": "/covers/foo-bar.png",
              "coverImageAlt": "Foo Bar cover",
              "repoUrl": "https://github.com/example/foo-bar",
              "platform": "Android",
              "status": "Active",
              "role": "solo",
              "packageOrRepo": "com.example.foobar"
            },
            {
              "slug": "no-cover",
              "kind": "Library",
              "title": "No Cover",
              "shortDescription": "short",
              "fullDescription": "long",
              "tags": [],
              "coverImageUrl": null,
              "coverImageAlt": "",
              "repoUrl": null,
              "platform": "JVM",
              "status": "Archived",
              "role": "solo",
              "packageOrRepo": null
            }
          ]
        }
    """.trimIndent()

    @Test
    fun parseProjectEntries_mapsCoreFields() {
        val entries = parseProjectEntries(fixture)
        assertEquals(2, entries.size)
        val foo = entries[0]
        assertEquals("foo-bar", foo.slug)
        assertEquals("Foo Bar", foo.title)
        assertEquals(listOf("Kotlin", "Compose"), foo.tags)
        assertEquals("/covers/foo-bar.png", foo.coverImageUrl)
    }

    @Test
    fun parseProjectEntries_handlesNullableFields() {
        val entries = parseProjectEntries(fixture)
        val noCover = entries[1]
        assertNull(noCover.coverImageUrl)
        assertNull(noCover.repoUrl)
        assertNull(noCover.packageOrRepo)
        assertEquals(emptyList(), noCover.tags)
    }
}
