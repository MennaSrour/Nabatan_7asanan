package com.menna.nabatan_7asena.presentation.screens.home

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.menna.nabatan_7asena.domain.entity.DailyContent
import com.menna.nabatan_7asena.presentation.screens.home.composables.DailyChallengeDialog
import com.menna.nabatan_7asena.ui.theme.NabataAppTheme
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class DailyChallengeDialogTest {
    @get:Rule val compose = createComposeRule()

    @Test fun wrongAnswerShowsFeedbackAndCorrectRetryRemainsVisible() {
        val answers = mutableListOf<Boolean>()
        compose.setContent {
            NabataAppTheme(false) {
                DailyChallengeDialog(DailyContent.Riddle("سؤال", " الإجابة ", listOf("أخرى", "الإجابة")), {}, answers::add)
            }
        }
        compose.onNodeWithText("أخرى").performClick()
        compose.onNodeWithText("إجابة غير صحيحة. حاول مرة أخرى.").assertIsDisplayed()
        compose.onNodeWithText("الإجابة").performClick()
        compose.mainClock.advanceTimeBy(5000)
        compose.onNodeWithText("إجابة صحيحة! أحسنت.").assertIsDisplayed()
        compose.onNodeWithText("الإجابة").performClick()
        compose.runOnIdle { assertEquals(listOf(false, true), answers) }
    }
}
