package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.menna.nabatan_7asena.domain.entity.SpeedQuestion
import kotlinx.coroutines.delay
import kotlin.time.TimeSource

private const val TriviaTime = 60_000L

@Composable
internal fun SpeedTrivia(viewModel: KidsRewardsViewModel, onFinish: (Int, Int) -> Unit) {
    val questions by produceState<List<SpeedQuestion>>(emptyList()) { value = viewModel.questions().shuffled() }
    if (questions.isEmpty()) {
        CircularProgressIndicator(color = RewardPurple)
    } else {
        TriviaRound(questions, onFinish)
    }
}

@Composable
private fun TriviaRound(questions: List<SpeedQuestion>, onFinish: (Int, Int) -> Unit) {
    var remaining by remember { mutableLongStateOf(TriviaTime) }
    var index by remember { mutableIntStateOf(0) }
    var streak by remember { mutableIntStateOf(0) }
    var score by remember { mutableIntStateOf(0) }
    var choice by remember { mutableStateOf<Int?>(null) }
    var done by remember { mutableStateOf(false) }
    val start = remember { TimeSource.Monotonic.markNow() }
    val question = questions[index]
    val displayOrder = remember(index) { question.choices.indices.shuffled() }

    fun finish() {
        if (done) return
        done = true
        onFinish(score.coerceAtMost(10), score)
    }

    LaunchedEffect(Unit) {
        while (!done) {
            remaining = (TriviaTime - start.elapsedNow().inWholeMilliseconds).coerceAtLeast(0)
            if (remaining == 0L) {
                finish()
                break
            }
            delay(75)
        }
    }
    LaunchedEffect(choice) {
        if (choice != null) {
            delay(480)
            if (!done) {
                if (index == questions.lastIndex) finish()
                else {
                    index++
                    choice = null
                }
            }
        }
    }

    RewardText("الوقت ${arabicNumber(((remaining + 999) / 1000).toInt())} ث  ·  النتيجة ${arabicNumber(score)}", bold = true)
    RoundProgress(remaining.toFloat() / TriviaTime)
    RewardText(question.prompt, size = 27, bold = true)
    displayOrder.forEach { answer ->
        RewardButton(question.choices[answer], {
            if (choice == null && !done) {
                if (start.elapsedNow().inWholeMilliseconds >= TriviaTime) finish()
                else {
                    choice = answer
                    if (answer == question.answerIndex) {
                        streak++
                        score += if (streak % 3 == 0) 3 else 1
                    } else streak = 0
                }
            }
        }, Modifier.fillMaxWidth(), enabled = choice == null && !done)
    }
    choice?.let {
        RewardText(if (it == question.answerIndex) "إجابة صحيحة! سلسلة ${arabicNumber(streak)}"
            else "الإجابة الصحيحة: ${question.choices[question.answerIndex]}", bold = true)
    } ?: RewardText("كل ٣ إجابات صحيحة متتالية تمنح نقطتين إضافيتين.", size = 16)
}
