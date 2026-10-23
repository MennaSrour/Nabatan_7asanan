package com.menna.nabatan_7asena.presentation.common

import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.entity.User
import java.io.File
import java.util.Locale
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GenderedStringTest {
    private val strings: Map<String, String> by lazy {
        val nodes = DocumentBuilderFactory.newInstance().newDocumentBuilder()
            .parse(File("src/main/res/values/strings.xml"))
            .getElementsByTagName("string")
        (0 until nodes.length).associate { index ->
            val node = nodes.item(index)
            node.attributes.getNamedItem("name").nodeValue to node.textContent
        }
    }

    @Test
    fun maleUser_selectsMaleResource() {
        assertEquals(R.string.login_welcome_male, GenderedStrings.loginWelcome.resourceId(User.Gender.BOY))
    }

    @Test
    fun femaleUser_selectsFemaleResource() {
        assertEquals(R.string.login_welcome_female, GenderedStrings.loginWelcome.resourceId(User.Gender.GIRL))
    }

    @Test
    fun everyGenderedResource_hasBothVariantsAndMatchingFormatArguments() {
        val placeholders = Regex("%\\d+\\$[dsf]")
        val pairedNames = strings.keys.filter { it.endsWith("_male") || it.endsWith("_female") }
        assertTrue("Expected migrated user-facing resources", pairedNames.isNotEmpty())
        pairedNames.map { it.removeSuffix("_female").removeSuffix("_male") }.distinct().forEach { key ->
            val male = strings.getValue("${key}_male")
            val female = strings.getValue("${key}_female")
            assertEquals(key, placeholders.findAll(male).map { it.value }.toList(),
                placeholders.findAll(female).map { it.value }.toList())
        }
    }

    @Test
    fun feminineTaskPrompt_formatsDynamicTitle() {
        assertEquals("اضغطي لإتمام صلاة الفجر",
            String.format(Locale.ROOT, strings.getValue("task_complete_female"), "صلاة الفجر"))
        assertEquals("اضغط لإتمام صلاة الفجر",
            String.format(Locale.ROOT, strings.getValue("task_complete_male"), "صلاة الفجر"))
    }

    @Test
    fun genderedCopy_containsNoEmoji() {
        val emoji = Regex("[\\x{1F000}-\\x{1FAFF}\\u2600-\\u27BF\\uFE0F\\u200D]")
        strings.forEach { (key, value) -> assertFalse(key, emoji.containsMatchIn(value)) }
    }

    @Test
    fun femaleCopy_addressesTheUserRatherThanReusingMasculinePraise() {
        assertEquals("برافو يا بطلة!", strings.getValue("quiz_praise_hero_female"))
        assertEquals("شاطرة جدًا!", strings.getValue("quiz_praise_clever_female"))
        assertEquals("جاهزة؟", strings.getValue("quiz_ready_female"))
    }
}
