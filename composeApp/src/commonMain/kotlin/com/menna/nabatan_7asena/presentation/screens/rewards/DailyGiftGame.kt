package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.domain.entity.DailyContent
import com.menna.nabatan_7asena.domain.entity.KidsGame
import com.menna.nabatan_7asena.domain.entity.SpeedQuestion
import com.menna.nabatan_7asena.platform.animation.LottieAnimation
import com.menna.nabatan_7asena.platform.animation.LottieCompositionSpec
import com.menna.nabatan_7asena.platform.animation.rememberLottieComposition
import com.menna.nabatan_7asena.resources.AppResources as R

@Composable
internal fun DailyGiftGame(
    game: KidsGame,
    current: RewardAccount,
    dailyRiddle: DailyContent.Riddle?,
    onCorrectAnswer: () -> Unit,
    onBack: () -> Unit,
    viewModel: KidsRewardsViewModel
) {
    val latestPayout by viewModel.payout.collectAsStateWithLifecycle()
    val payout = latestPayout?.takeIf { it.account == current && it.game == game }

    RewardsPage(game.title, onBack, scroll = game != KidsGame.CATCH || payout != null, showBack = payout == null) {
        val finish: (Int) -> Unit = { stars -> viewModel.finishGame(current, game, stars) }
        val finishScored: (Int, Int) -> Unit = { stars, score -> viewModel.finishGame(current, game, stars, score) }
        if (payout != null) {
            RewardText("انتهت الجولة", size = 28, bold = true)
            payout.score?.let { RewardText("النتيجة ${arabicNumber(it)}", size = 22) }
            when {
                payout.failed -> RewardText("تعذّر حفظ النجوم.")
                payout.credited == null -> CircularProgressIndicator(color = RewardPurple)
                else -> {
                    val stars = payout.credited
                    if (stars > 0) {
                        val celebration by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.win_celebration))
                        LottieAnimation(celebration, Modifier.size(140.dp), iterations = 1)
                    }
                    RewardText("النجوم المكتسبة: ${arabicNumber(stars)}", size = 22)
                }
            }
            if (payout.credited != null || payout.failed) {
                RewardButton("إنهاء", onBack, color = RewardGold)
            }
        } else {
            RewardText(game.hint, size = 18)
            when (game) {
                KidsGame.MEMORY -> MemoryMatch(finish)
                KidsGame.VERSE -> VerseBuilder(viewModel, finish)
                KidsGame.TRIVIA -> SpeedTrivia(viewModel, finishScored)
                KidsGame.CATCH -> CatchGoodDeeds(finishScored)
                KidsGame.RIDDLE -> RiddleRound(viewModel, dailyRiddle) { correct ->
                    if (correct) onCorrectAnswer()
                    finishScored(if (correct) 3 else 0, if (correct) 1 else 0)
                }
            }
        }
    }
}

@Composable
private fun RiddleRound(
    viewModel: KidsRewardsViewModel,
    dailyRiddle: DailyContent.Riddle?,
    onAnswer: (Boolean) -> Unit
) {
    val question by produceState<SpeedQuestion?>(null) {
        val builtIn = dailyRiddle?.let { riddle ->
            val index = riddle.options.indexOfFirst(riddle::isCorrectAnswer)
            if (riddle.question.isNotBlank() && riddle.options.size in 2..4 &&
                riddle.options.none(String::isBlank) && index >= 0) {
                SpeedQuestion(riddle.question, riddle.options, index)
            } else null
        }
        val pool = viewModel.questions(KidsGame.RIDDLE)
        value = (pool + listOfNotNull(builtIn)).distinctBy { it.prompt }.random()
    }
    val current = question
    if (current == null) CircularProgressIndicator(color = RewardPurple)
    else {
        val order = remember(current) { current.choices.indices.shuffled() }
        var answered by remember(current) { mutableStateOf(false) }
        RewardText(current.prompt, size = 26, bold = true)
        order.forEach { index ->
            RewardButton(current.choices[index], {
                if (!answered) {
                    answered = true
                    onAnswer(index == current.answerIndex)
                }
            }, Modifier.fillMaxWidth(), enabled = !answered)
        }
    }
}
