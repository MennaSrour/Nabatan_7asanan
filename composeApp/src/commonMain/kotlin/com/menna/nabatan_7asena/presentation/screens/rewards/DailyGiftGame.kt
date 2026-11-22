package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.domain.entity.KidsGame

@Composable
internal fun DailyGiftGame(
    game: KidsGame,
    current: RewardAccount,
    onBack: () -> Unit,
    viewModel: KidsRewardsViewModel
) {
    val latestPayout by viewModel.payout.collectAsStateWithLifecycle()
    val payout = latestPayout?.takeIf { it.account == current && it.game == game }
    var round by remember { mutableIntStateOf(0) }

    RewardsPage(game.title, onBack, scroll = game != KidsGame.CATCH || round == 0 || payout != null) {
        val finish: (Int) -> Unit = { stars -> viewModel.finishGame(current, game, stars) }
        val finishScored: (Int, Int) -> Unit = { stars, score ->
            viewModel.finishGame(current, game, stars, score)
        }
        val result = payout
        when {
            result != null -> {
                RewardText("انتهت الجولة", size = 28, bold = true)
                result.score?.let { RewardText("النتيجة ${arabicNumber(it)}", size = 22) }
                when {
                    result.failed -> {
                        RewardText("تعذّر حفظ النجوم.")
                        RewardButton("إعادة المحاولة", { viewModel.finishGame(current, game, result.requested, result.score) })
                    }
                    result.credited == null -> CircularProgressIndicator(color = RewardPurple)
                    else -> {
                        RewardText(
                            when {
                                result.credited > 0 -> "ربحت ${arabicNumber(result.credited)} نجوم ★"
                                result.requested == 0 -> "جرّب جولة أخرى لجمع النجوم."
                                else -> "تم جمع نجوم هذه اللعبة اليوم."
                            }, size = 22
                        )
                        RewardButton("جولة جديدة", { viewModel.resetGame(); round++ }, color = RewardGold)
                    }
                }
            }
            round == 0 -> {
                RewardText(game.hint, size = 22)
                if (game == KidsGame.CATCH) RewardText("التقط النجوم وتجنّب الغيوم. لديك ٣٠ ثانية.")
                RewardButton("ابدأ الجولة", { round = 1 }, color = RewardGold)
            }
            else -> key(current, round) {
                when (game) {
                    KidsGame.MEMORY -> MemoryMatch(finish)
                    KidsGame.VERSE -> VerseBuilder(viewModel, finish)
                    KidsGame.TRIVIA -> SpeedTrivia(viewModel, finishScored)
                    KidsGame.CATCH -> CatchGoodDeeds(finishScored)
                }
            }
        }
    }
}
