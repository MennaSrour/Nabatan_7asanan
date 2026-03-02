package com.menna.nabata_7asena.presentation.screens.home.component

import android.media.MediaPlayer
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.domain.entity.DailyContent

@Composable
fun DailyChallengeDialog(
    riddle: DailyContent.Riddle,
    onDismiss: () -> Unit,
    onAnswerSelected: (Boolean) -> Unit
) {
    var selectedAnswerIndex by remember { mutableStateOf<Int?>(null) }
    var isSolved by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun checkAnswer(index: Int) {
        selectedAnswerIndex = index
        
        if (riddle.options[index] == riddle.answer) {
            isSolved = true
            MediaPlayer.create(context, R.raw.good).start()
            onAnswerSelected(true)
        } else {
            
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(Color.White)
                .border(4.dp, Color(0xFFFFD54F), RoundedCornerShape(32.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🌟 فزورة اليوم 🌟",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFFF6F00)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE3F2FD), RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = riddle.question,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = Color(0xFF1565C0),
                        lineHeight = 26.sp,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                
                riddle.options.forEachIndexed { index, answerOption ->

                    
                    val isCorrectOption = answerOption == riddle.answer 

                    val backgroundColor by animateColorAsState(
                        targetValue = when {
                            isSolved && isCorrectOption -> Color(0xFF4CAF50) 
                            selectedAnswerIndex == index && !isCorrectOption -> Color(0xFFEF5350) 
                            else -> Color(0xFFF5F5F5)
                        },
                        label = "color"
                    )

                    val textColor = if (selectedAnswerIndex == index || (isSolved && isCorrectOption)) Color.White else Color.Black

                    Button(
                        onClick = { if (!isSolved) checkAnswer(index) },
                        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .height(56.dp)
                            .shadow(if(isSolved) 0.dp else 4.dp, RoundedCornerShape(16.dp))
                    ) {
                        Text(
                            text = answerOption,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                }

                if (isSolved) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("أحسنت! 🎉", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
            }

            if (isSolved) {
                val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.win_celebration))
                LottieAnimation(
                    composition = composition,
                    iterations = 1,
                    modifier = Modifier.matchParentSize().scale(1.2f)
                )
            }
        }
    }
}