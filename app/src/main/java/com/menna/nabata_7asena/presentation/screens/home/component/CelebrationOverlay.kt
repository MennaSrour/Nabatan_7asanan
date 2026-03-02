package com.menna.nabata_7asena.presentation.screens.home.component

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.menna.nabata_7asena.R

@Composable
fun CelebrationOverlay(onDismiss: () -> Unit) {
    val context = LocalContext.current

    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    LaunchedEffect(Unit) {
        try {
            mediaPlayer = MediaPlayer.create(context, R.raw.big_star_voice)?.apply {
                setOnCompletionListener {
                    try {
                        release()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    mediaPlayer = null
                }
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                mediaPlayer?.let {
                    if (it.isPlaying) {
                        it.stop()
                    }
                    it.release()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                mediaPlayer = null
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.7f))
                .clickable { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                DoubleLottieCelebration(
                    LottieCompositionSpec.RawRes(R.raw.win_celebration),
                    LottieCompositionSpec.RawRes(R.raw.big_star)
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    "أخدت أكبر نجمة يا بطل",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}