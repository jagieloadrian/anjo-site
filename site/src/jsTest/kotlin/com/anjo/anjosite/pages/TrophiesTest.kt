package com.anjo.anjosite.pages

import com.anjo.anjosite.components.widgets.TrophyTier
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TrophiesTest {
    private val fixture = """
        {
          "stats": [
            {"key": "level", "value": "42"},
            {"key": "platinums", "value": "3"}
          ],
          "games": [
            {"imageUrl": "/covers/foo.jpg", "alt": "Foo cover", "name": "Foo", "percentText": "78%", "muted": false},
            {"imageUrl": null, "name": "Bar", "alt": "Bar cover", "percentText": "12%", "muted": true}
          ],
          "trophies": [
            {"tier": "PLATINUM", "name": "Foo Master", "gameName": "Foo", "rarityPercent": "4.2%", "earnedAt": "2024-01-01"},
            {"tier": "BRONZE", "name": "First Steps", "gameName": "Bar", "rarityPercent": "88.1%", "earnedAt": null}
          ]
        }
    """.trimIndent()

    @Test
    fun parseTrophiesData_mapsStats() {
        val data = parseTrophiesData(fixture)
        assertEquals(2, data.stats.size)
        assertEquals("level", data.stats[0].key)
        assertEquals("42", data.stats[0].value)
    }

    @Test
    fun parseTrophiesData_mapsGames() {
        val data = parseTrophiesData(fixture)
        assertEquals(2, data.games.size)
        assertEquals("Foo", data.games[0].name)
        assertEquals("/covers/foo.jpg", data.games[0].imageUrl)
        assertNull(data.games[1].imageUrl)
        assertEquals(true, data.games[1].muted)
    }

    @Test
    fun parseTrophiesData_mapsTrophies() {
        val data = parseTrophiesData(fixture)
        assertEquals(2, data.trophies.size)
        assertEquals(TrophyTier.PLATINUM, data.trophies[0].tier)
        assertEquals("Foo Master", data.trophies[0].name)
        assertEquals("2024-01-01", data.trophies[0].earnedAt)
        assertNull(data.trophies[1].earnedAt)
    }

    @Test
    fun parseTrophiesData_missingIconUrlDefaultsToNull() {
        val data = parseTrophiesData(fixture)
        assertNull(data.trophies[0].iconUrl)
    }
}
