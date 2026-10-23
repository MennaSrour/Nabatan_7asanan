package com.menna.nabatan_7asena.presentation.common

import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.menna.nabatan_7asena.domain.entity.User
import org.junit.Rule
import org.junit.Test

class GenderedStringResourceTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun changingUiStateGender_updatesTextWithoutRecreatingTheScreen() {
        val gender = mutableStateOf(User.Gender.BOY)
        composeRule.setContent {
            CompositionLocalProvider(LocalUserGender provides gender.value) {
                Text(genderedStringResource(GenderedStrings.quizReady))
            }
        }
        composeRule.onNodeWithText("جاهز؟").assertIsDisplayed()
        composeRule.runOnIdle { gender.value = User.Gender.GIRL }
        composeRule.onNodeWithText("جاهزة؟").assertIsDisplayed()
    }

    @Test
    fun formattedArgument_updatesAlongsideGender() {
        val gender = mutableStateOf(User.Gender.BOY)
        val task = mutableStateOf("صلاة الفجر")
        composeRule.setContent {
            CompositionLocalProvider(LocalUserGender provides gender.value) {
                Text(genderedStringResource(GenderedStrings.taskComplete, task.value))
            }
        }
        composeRule.onNodeWithText("اضغط لإتمام صلاة الفجر").assertIsDisplayed()
        composeRule.runOnIdle {
            gender.value = User.Gender.GIRL
            task.value = "صلاة الظهر"
        }
        composeRule.onNodeWithText("اضغطي لإتمام صلاة الظهر").assertIsDisplayed()
    }

    @Test
    fun explicitGender_overridesInheritedGender() {
        composeRule.setContent {
            CompositionLocalProvider(LocalUserGender provides User.Gender.GIRL) {
                Text(genderedStringResource(GenderedStrings.quizReady, gender = User.Gender.BOY))
            }
        }
        composeRule.onNodeWithText("جاهز؟").assertIsDisplayed()
    }
}
