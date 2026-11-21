package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.domain.entity.KidsGame
import com.menna.nabatan_7asena.domain.entity.WheelPrize
import com.menna.nabatan_7asena.platform.platformKidsRewardsViewModel

@Composable
internal fun KidsGamesScreen(
    onBack: () -> Unit,
    onGame: (KidsGame) -> Unit,
    viewModel: KidsRewardsViewModel = platformKidsRewardsViewModel()
) {
    val status by viewModel.status.collectAsStateWithLifecycle()
    RewardsPage("ألعاب نباتا حسنا", onBack) {
        RewardText("لكل لعبة مكافأة نجوم مرة واحدة في اليوم.")
        val games = KidsGame.entries.filter { it != KidsGame.BONUS || status?.prize == WheelPrize.BONUS_ROUND }
        games.forEachIndexed { index, game ->
            val color = listOf(RewardLavender, RewardMint, RewardRose, RewardGold, RewardCream)[index]
            Card(
                onClick = { onGame(game) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = color),
                shape = RewardShape
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    RewardText(game.icon, size = 30, bold = true)
                    Column(Modifier.weight(1f)) {
                        RewardText(game.title, size = 23, bold = true)
                        RewardText(game.hint, size = 17)
                        if (status?.rewardedGames?.contains(game) == true) {
                            RewardText("تم جمع نجوم اليوم", size = 15, bold = true)
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun KidsGameScreen(
    game: KidsGame,
    onBack: () -> Unit,
    viewModel: KidsRewardsViewModel = platformKidsRewardsViewModel()
) {
    val account by viewModel.account.collectAsStateWithLifecycle()
    val status by viewModel.status.collectAsStateWithLifecycle()
    val payout by viewModel.payout.collectAsStateWithLifecycle()
    var round by remember { mutableIntStateOf(0) }

    RewardsPage(game.title, onBack, scroll = game != KidsGame.CATCH || round == 0 || payout != null) {
        val current = account
        val dailyStatus = status
        if (current == null || dailyStatus == null) {
            CircularProgressIndicator(color = RewardPurple)
            return@RewardsPage
        }
        if (game == KidsGame.BONUS && dailyStatus.prize != WheelPrize.BONUS_ROUND) {
            RewardText("تُفتح هذه الجولة من عجلة المكافآت.")
            return@RewardsPage
        }
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
                    KidsGame.MEMORY, KidsGame.BONUS -> MemoryMatch(game == KidsGame.BONUS, finish)
                    KidsGame.VERSE -> VerseBuilder(viewModel, finish)
                    KidsGame.TRIVIA -> SpeedTrivia(viewModel, finishScored)
                    KidsGame.CATCH -> CatchGoodDeeds(finishScored)
                }
            }
        }
    }
}
