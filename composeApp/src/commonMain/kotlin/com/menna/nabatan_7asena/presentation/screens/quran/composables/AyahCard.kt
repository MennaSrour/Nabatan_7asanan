package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabatan_7asena.domain.entity.AyahModel
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
fun AyahCard(
    ayah: AyahModel,
    isBookmarked: Boolean,
    quranFont: FontFamily,
    audioState: AyahAudioState,
    onPlayAudio: () -> Unit,
    onBookmarkClick: () -> Unit
) {
    val q = SummerTheme.quran
    val isActive = audioState == AyahAudioState.PLAYING || audioState == AyahAudioState.LOADING

    Card(
        shape    = SummerTheme.Shapes.MediumRounded,
        colors   = CardDefaults.cardColors(
            containerColor = if (isActive) q.cardActive else q.card
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isActive) 6.dp else 3.dp
        ),
        border = BorderStroke(
            width = if (isBookmarked || isActive) 1.5.dp else 1.dp,
            color = when {
                isActive     -> q.accent
                isBookmarked -> q.accent
                else         -> q.border
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Surface(
                    color    = q.accent.copy(0.12f),
                    shape    = CircleShape,
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            "${ayah.ayaNo}",
                            color      = q.accentStrong,
                            fontSize   = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                            when (audioState) {
                                AyahAudioState.PLAYING ->
                                q.accent.copy(0.2f)
                                AyahAudioState.PAUSED ->
                                q.accentSoft
                                else -> Color.Transparent
                            },
                            CircleShape
                        )
                            .border(
                            1.dp,
                            when (audioState) {
                                AyahAudioState.IDLE -> Color.Transparent
                                else -> q.accent.copy(0.4f)
                            },
                            CircleShape
                        )
                            .clickable { onPlayAudio() },
                        contentAlignment = Alignment.Center
                    ) {
                        when (audioState) {
                            AyahAudioState.LOADING -> {
                                val rotation by rememberInfiniteTransition(label = "load")
                                    .animateFloat(
                                    initialValue = 0f,
                                    targetValue  = 360f,
                                    animationSpec = infiniteRepeatable(
                                        tween(900, easing = LinearEasing),
                                        RepeatMode.Restart
                                    ),
                                    label = "rotate"
                                )
                                CircularProgressIndicator(
                                    modifier    = Modifier.size(18.dp).rotate(rotation),
                                    color       = q.accentStrong,
                                    strokeWidth = 2.dp
                                )
                            }
                            AyahAudioState.PLAYING -> {
                                Icon(
                                    Icons.Rounded.Pause,
                                    null,
                                    tint     = q.accentStrong,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            AyahAudioState.PAUSED,
                            AyahAudioState.IDLE -> {
                                Icon(
                                    Icons.Rounded.VolumeUp,
                                    null,
                                    tint     = q.accent,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    IconButton(onClick = onBookmarkClick, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Default.Bookmark
                            else Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint     = if (isBookmarked) q.accentStrong else q.textMuted.copy(0.6f),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            val displayText = remember(ayah.text) {
                ayah.text.replace(Regex("[^\u0600-\u06FF\u0020\u06DD]"), "").trim()
            }

            Text(
                text       = displayText,
                fontFamily = quranFont,
                fontSize   = 24.sp,
                textAlign  = TextAlign.Right,
                lineHeight = 44.sp,
                color      = q.textPrimary,
                modifier   = Modifier.fillMaxWidth()
            )

            if (audioState == AyahAudioState.PLAYING) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AudioWaveIndicator()
                }
            }
        }
    }
}
