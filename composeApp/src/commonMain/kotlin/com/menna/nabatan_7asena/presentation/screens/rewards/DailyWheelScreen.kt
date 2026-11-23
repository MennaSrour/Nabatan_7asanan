package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.domain.entity.DailyContent
import com.menna.nabatan_7asena.domain.entity.WheelPrize
import com.menna.nabatan_7asena.presentation.screens.home.HomeViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.math.abs

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
    val scope = rememberCoroutineScope()
    var openingReward by remember { mutableStateOf(false) }
    var starting by remember { mutableStateOf(false) }
    var roundUsedHere by remember { mutableStateOf(false) }
    var launchFailed by remember { mutableStateOf(false) }
    var revealed by remember { mutableStateOf(false) }
    val waitingForPrize = won == null && preparing

    LaunchedEffect(won, waitingForPrize) {
        if (won != null) {
            if (abs(rotation.value) < 1f) {
                rotation.animateTo(-12f, tween(190, easing = CubicBezierEasing(.26f, .45f, .50f, 1f)))
            }
            val stop = (rotation.value / 360f).toInt() * 360f + 1440f + prizeAngle(won)
            rotation.animateTo(stop, tween(3300, easing = CubicBezierEasing(.14f, .14f, .21f, 1f)))
            revealed = true
            celebration.snapTo(0f)
            celebration.animateTo(1f, tween(1100, easing = LinearEasing))
            viewModel.dismissSpin()
        } else if (waitingForPrize) {
            revealed = false
            rotation.animateTo(-12f, tween(190, easing = CubicBezierEasing(.26f, .45f, .50f, 1f)))
            rotation.animateTo(135f, tween(620, easing = CubicBezierEasing(.4f, 0f, .65f, .45f)))
            while (true) rotation.animateTo(rotation.value + 360f, tween(880, easing = LinearEasing))
        }
    }
    val game = prize?.game
    if (openingReward && game != null) {
        DailyGiftGame(game, current, dailyRiddle, onCorrectAnswer,
            onBack = { openingReward = false }, viewModel = viewModel)
        return
    }

    RewardsPage("عجلة هدية النهارده", onBack = { viewModel.dismissSpin(); onBack() }) {
        RewardText("نباتًا حسنًا", size = 28, bold = true)
        val settled = won == null || revealed
        val spinning = preparing || (won != null && !revealed)
        BotanicalWheel(
            rotation = if (spinning || won != null) rotation.value else prizeAngle(prize),
            celebration = celebration.value,
            winner = prize?.takeIf { settled },
            spinning = spinning,
            enabled = status != null && status?.clockRolledBack == false && prize == null && !preparing,
            onSpin = onSpin
        )
        when {
            settled && prize != null -> Surface(color = RewardCream, shape = RewardShape) {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    RewardText(prize.label, size = 26, bold = true)
                    when {
                        status?.earnedHere == false -> RewardText("سُجّلت لفة اليوم على جهاز آخر")
                        game != null && (roundUsedHere || game in status?.usedRounds.orEmpty()) ->
                            RewardText("تم استخدام جولة هذه اللفة")
                        game != null -> {
                            if (launchFailed) RewardText("تعذّر فتح الجولة. جرّب مرة تانية.")
                            RewardButton(
                                if (prize == WheelPrize.RIDDLE) "ابدأ الفزورة" else "ابدأ الجولة",
                                {
                                    if (!starting && !roundUsedHere) {
                                        starting = true
                                        launchFailed = false
                                        scope.launch {
                                            try {
                                                if (viewModel.startRound(current, game)) {
                                                    roundUsedHere = true
                                                    openingReward = true
                                                } else launchFailed = true
                                            } catch (e: CancellationException) {
                                                throw e
                                            } catch (_: Exception) {
                                                launchFailed = true
                                            } finally {
                                                starting = false
                                            }
                                        }
                                    }
                                },
                                Modifier.fillMaxWidth(),
                                enabled = !starting && status?.earnedHere == true,
                                color = RewardGold
                            )
                        }
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

}

private fun prizeAngle(prize: WheelPrize?): Float =
    -WheelPrize.segments.indexOf(prize).coerceAtLeast(0) * wheelSlice
