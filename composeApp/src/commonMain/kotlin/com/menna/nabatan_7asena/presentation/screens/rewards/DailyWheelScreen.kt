package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.domain.entity.DailyContent
import com.menna.nabatan_7asena.domain.entity.WheelPrize
import com.menna.nabatan_7asena.presentation.screens.home.HomeViewModel
import com.menna.nabatan_7asena.presentation.screens.home.composables.DailyChallengeDialog

internal val wheelSlice = 360f / WheelPrize.segments.size

@Composable
internal fun DailyWheelScreen(onBack: () -> Unit, viewModel: KidsRewardsViewModel, home: HomeViewModel) {
    val account by viewModel.account.collectAsStateWithLifecycle()
    val homeState by home.uiState.collectAsStateWithLifecycle()
    val current = account
    if (current == null || homeState.isLoading) {
        RewardsPage("هدية النهارده", onBack) { CircularProgressIndicator(color = RewardPurple) }
        return
    }
    if (!home.canOpenDailyGift(current.profileId, current.day)) {
        LaunchedEffect(current, homeState.dailyGiftRemaining) { onBack() }
        return
    }
    key(current) {
        DailyGiftWheel(
            current = current,
            viewModel = viewModel,
            dailyRiddle = homeState.dailyRiddle,
            onCorrectAnswer = home::onCorrectAnswer,
            onBack = onBack,
            onSpin = {
                if (home.canOpenDailyGift(current.profileId, current.day)) viewModel.spinWheel(current)
            }
        )
    }
}

@Composable
private fun DailyGiftWheel(
    current: RewardAccount,
    viewModel: KidsRewardsViewModel,
    dailyRiddle: DailyContent.Riddle?,
    onCorrectAnswer: () -> Unit,
    onBack: () -> Unit,
    onSpin: () -> Unit
) {
    val dayStatus by viewModel.status.collectAsStateWithLifecycle()
    val status = dayStatus?.takeIf { it.account == current }?.status
    val spin by viewModel.spin.collectAsStateWithLifecycle()
    val rotation = remember { Animatable(0f) }
    val celebration = remember { Animatable(1f) }
    val won = (spin as? SpinState.Won)?.takeIf { it.account == current }?.prize
    val preparing = spin == SpinState.Saving || status?.spinPending == true
    val prize = if (preparing) null else won ?: status?.prize
    var openingReward by rememberSaveable { mutableStateOf(false) }
    var revealed by remember { mutableStateOf(false) }

    LaunchedEffect(won, preparing) {
        if (won != null) {
            val stop = (rotation.value / 360f).toInt() * 360f + 1440f + prizeAngle(won)
            rotation.animateTo(stop + 5f, tween(2850, easing = CubicBezierEasing(.10f, .63f, .18f, 1f)))
            rotation.animateTo(stop, tween(240, easing = CubicBezierEasing(.24f, 1.24f, .62f, 1f)))
            revealed = true
            celebration.snapTo(0f)
            celebration.animateTo(1f, tween(820, easing = LinearEasing))
            openingReward = won.game != null || won == WheelPrize.RIDDLE
            viewModel.dismissSpin()
        } else if (preparing) {
            while (true) rotation.animateTo(rotation.value + 360f, tween(850, easing = LinearEasing))
        }
    }
    val game = prize?.game
    if (openingReward && game != null) {
        DailyGiftGame(
            game = game,
            current = current,
            viewModel = viewModel,
            onBack = { viewModel.resetGame(); openingReward = false }
        )
        return
    }

    RewardsPage("عجلة هدية النهارده", onBack = { viewModel.dismissSpin(); onBack() }) {
        RewardText("نباتًا حسنًا", size = 28, bold = true)
        val settled = prize != null && (won == null || revealed)
        val spinning = preparing || (won != null && !revealed)
        BotanicalWheel(
            rotation = if (spinning || won != null) rotation.value else prizeAngle(prize),
            celebration = celebration.value,
            spinning = spinning,
            enabled = status != null && status?.clockRolledBack == false && prize == null && !preparing,
            onSpin = onSpin
        )
        when {
            settled && prize != null -> Surface(color = RewardCream, shape = RewardShape) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    RewardText(prize.label, size = 26, bold = true)
                    if (game != null && game in status?.rewardedGames.orEmpty()) {
                        RewardText("نجوم الجولة في رصيدك")
                    }
                    when {
                        game != null || prize == WheelPrize.RIDDLE -> RewardButton(
                            if (game == null) "افتح فزورتك" else "افتح جولتك",
                            { viewModel.resetGame(); openingReward = true },
                            Modifier.fillMaxWidth(),
                            color = RewardGold
                        )
                        status?.earnedHere == false -> RewardText("سُجّلت لفة اليوم على جهاز آخر")
                        prize.water > 0 && status?.waterDelivered != true -> RewardText("جارٍ إضافة القطرات إلى حديقتك")
                        prize.water > 0 -> RewardText("قطرات لري شجرتك")
                        else -> RewardText("النجوم في رصيدك، ويمكنك استخدامها في متجر الحديقة")
                    }
                }
            }
            spin == SpinState.Failed -> RewardText("تعذّر حفظ الهدية. جرّب مرة تانية.")
            spinning -> RewardText("يا ترى الهدية إيه؟")
            status?.clockRolledBack == true -> RewardText("لفة اليوم محفوظة. راجع تاريخ الجهاز.")
            status == null -> CircularProgressIndicator(color = RewardPurple)
            else -> RewardText("لفّة واحدة بعد ما تكمّل مهام يومك")
        }
    }

    if (openingReward && prize == WheelPrize.RIDDLE) {
        DailyChallengeDialog(
            riddle = remember {
                dailyRiddle?.takeIf {
                    it.question.isNotBlank() && it.options.size in 2..4 &&
                        it.options.none(String::isBlank) && it.options.any(it::isCorrectAnswer)
                } ?: offlineRiddle
            },
            onDismiss = { openingReward = false },
            onAnswerSelected = { if (it) onCorrectAnswer() }
        )
    }
}

private fun prizeAngle(prize: WheelPrize?): Float =
    -WheelPrize.segments.indexOf(prize).coerceAtLeast(0) * wheelSlice

private val offlineRiddle = DailyContent.Riddle(
    question = "ما اسم الشهر اللي بنصوم فيه؟",
    answer = "رمضان",
    options = listOf("شعبان", "رمضان", "شوال")
)
