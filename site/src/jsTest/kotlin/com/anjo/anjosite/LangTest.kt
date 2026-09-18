package com.anjo.anjosite

import kotlin.test.Test
import kotlin.test.assertEquals

class LangTest {
    @Test
    fun langForLocale_plExact() {
        assertEquals(Lang.PL, langForLocale("pl"))
    }

    @Test
    fun langForLocale_plRegion() {
        assertEquals(Lang.PL, langForLocale("pl-PL"))
    }

    @Test
    fun langForLocale_caseInsensitive() {
        assertEquals(Lang.PL, langForLocale("PL-pl"))
    }

    @Test
    fun langForLocale_english() {
        assertEquals(Lang.EN, langForLocale("en-US"))
    }

    @Test
    fun langForLocale_unrelatedLocaleFallsBackToEnglish() {
        assertEquals(Lang.EN, langForLocale("de-DE"))
    }

    @Test
    fun bilingualString_resolvesPerLang() {
        val s = BilingualString(en = "Hello", pl = "Cześć")
        assertEquals("Hello", s(Lang.EN))
        assertEquals("Cześć", s(Lang.PL))
    }

    @Test
    fun bilingualTimelineItem_resolvesAllFieldsPerLang() {
        val item = BilingualTimelineItem(
            date = BilingualString(en = "2024", pl = "2024"),
            title = BilingualString(en = "Engineer", pl = "Inżynier"),
            description = BilingualString(en = "Built things", pl = "Budował rzeczy"),
        )

        val en = item.resolve(Lang.EN)
        assertEquals("2024", en.date)
        assertEquals("Engineer", en.what)
        assertEquals("Built things", en.where)

        val pl = item.resolve(Lang.PL)
        assertEquals("Inżynier", pl.what)
        assertEquals("Budował rzeczy", pl.where)
    }
}
