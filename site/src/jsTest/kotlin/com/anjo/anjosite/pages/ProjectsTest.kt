package com.anjo.anjosite.pages

import com.anjo.anjosite.Lang
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
              "title": {"en": "Foo Bar", "pl": "Foo Bar PL"},
              "shortDescription": {"en": "A short one", "pl": "Krótki opis"},
              "fullDescription": {"en": "A long one", "pl": "Długi opis"},
              "tags": ["Kotlin", "Compose"],
              "coverImageUrl": "/covers/foo-bar.png",
              "coverImageAlt": {"en": "Foo Bar cover", "pl": "Okładka Foo Bar"},
              "repoUrl": "https://github.com/example/foo-bar",
              "platform": "Android",
              "status": {"en": "Active", "pl": "Aktywny"},
              "role": "solo",
              "packageOrRepo": "com.example.foobar"
            },
            {
              "slug": "no-cover",
              "kind": "Library",
              "title": {"en": "No Cover", "pl": "Bez okładki"},
              "shortDescription": {"en": "short", "pl": "krótko"},
              "fullDescription": {"en": "long", "pl": "długo"},
              "tags": [],
              "coverImageUrl": null,
              "coverImageAlt": {"en": "", "pl": ""},
              "repoUrl": null,
              "platform": "JVM",
              "status": {"en": "Archived", "pl": "Zarchiwizowany"},
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
        assertEquals("Foo Bar", foo.title(Lang.EN))
        assertEquals("Foo Bar PL", foo.title(Lang.PL))
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
