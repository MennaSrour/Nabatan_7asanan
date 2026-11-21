package com.menna.nabatan_7asena.domain.util

import kotlin.test.*

class TaskTitleTest {
    @Test fun normalizesArabicMarksTatweelAlifAndWhitespace() {
        assertEquals(normalizeTaskTitle("سنة الفجر"), normalizeTaskTitle("  سُـنَّةُ   الفَجْرِ  "))
        assertEquals(normalizeTaskTitle("أذكار الصباح"), normalizeTaskTitle("اذكار\u00a0الصباح!"))
        assertEquals(normalizeTaskTitle("صلاة على النبي"), normalizeTaskTitle("صلاه على النبى"))
    }
    @Test fun canonicalFajrPresetCannotBeAddedAgainUnderItsLongerName() {
        assertFailsWith<DuplicateDailyTaskException> { requireNewDailyTask("سنة الفجر ركعتين", listOf("سُنّة الفجر")) }
    }
    @Test fun keepsDifferentMeaningfulCountsAndAllowsDistinctTasks() {
        assertNotEquals(normalizeTaskTitle("استغفار 100"), normalizeTaskTitle("استغفار 1000"))
        requireNewDailyTask("بر الوالدين", listOf("صلاة الفجر"))
    }
    @Test fun emptyNormalizedTitlesAreRejected() {
        assertFailsWith<IllegalArgumentException> { requireNewDailyTask("  ـَ!  ", emptyList()) }
    }
}
