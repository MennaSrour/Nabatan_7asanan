package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.domain.entity.DailyContent
import com.menna.nabata_7asena.ui.theme.SummerTheme

@Composable
fun DailyChallengeDialog(
    riddle: DailyContent.Riddle,
    onDismiss: () -> Unit,
    onAnswerSelected: (Boolean) -> Unit
) {
    var selectedIdx by remember { mutableStateOf<Int?>(null) }
    var isSolved by remember { mutableStateOf(false) }

    fun check(index: Int) {
        selectedIdx = index
        val isCorrect = riddle.options[index] == riddle.answer
        if (isCorrect) {
            isSolved = true
        }
        onAnswerSelected(isCorrect)
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(SummerTheme.colors.dialogBackground)
                .border(3.dp, SummerTheme.colors.goldWarm, RoundedCornerShape(24.dp))
        ) {
            Column(
                modifier = Modifier.padding(22.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.HelpOutline,
                        contentDescription = null,
                        tint = SummerTheme.colors.textPrimary,
                        modifier = Modifier.size(SummerTheme.dimensions.iconLarge)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "سؤال اليوم",
                        style = SummerTheme.typography.dialogTitle,
                        color = SummerTheme.colors.textPrimary
                    )
                    Spacer(Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = SummerTheme.colors.goldWarm,
                        modifier = Modifier.size(SummerTheme.dimensions.iconLarge)
                    )
                }

                Spacer(Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SummerTheme.colors.white, SummerTheme.shapes.taskCard)
                        .border(1.5.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.4f), SummerTheme.shapes.taskCard)
                        .padding(SummerTheme.dimensions.paddingLarge)
                ) {
                    Text(
                        text = riddle.question,
                        style = SummerTheme.typography.dialogQuestion,
                        textAlign = TextAlign.Center,
                        color = SummerTheme.colors.textPrimary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(Modifier.height(18.dp))

                riddle.options.forEachIndexed { index, option ->
                    val isCorrect = option == riddle.answer
                    val bgColor by animateColorAsState(
                        when {
                            isSolved && isCorrect -> SummerTheme.colors.successGreen
                            selectedIdx == index && !isCorrect -> SummerTheme.colors.errorRed
                            else -> SummerTheme.colors.white
                        }, label = "color"
                    )

                    Button(
                        onClick = { if (!isSolved) check(index) },
                        colors = ButtonDefaults.buttonColors(containerColor = bgColor),
                        shape = SummerTheme.shapes.taskCard,
                        border = BorderStroke(
                            1.5.dp,
                            if (bgColor == SummerTheme.colors.white) SummerTheme.colors.goldWarm.copy(alpha = 0.3f) else SummerTheme.colors.transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .height(52.dp)
                    ) {
                        Text(
                            text = option,
                            style = SummerTheme.typography.buttonText,
                            color = if (bgColor == SummerTheme.colors.white) SummerTheme.colors.textPrimary else SummerTheme.colors.white
                        )
                    }
                }

                if (isSolved) {
                    Spacer(Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            tint = SummerTheme.colors.successGreen,
                            modifier = Modifier.size(SummerTheme.dimensions.iconSmall)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "ما شاء الله أحسنت",
                            color = SummerTheme.colors.successGreen,
                            style = SummerTheme.typography.taskTitle
                        )
                    }
                }
            }

            if (isSolved) {
                val comp by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.win_celebration))
                LottieAnimation(
                    comp,
                    iterations = 1,
                    modifier = Modifier
                        .matchParentSize()
                        .scale(1.2f)
                )
            }
        }
    }
}