package com.menna.nabatan_7asena.presentation.screens.start.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun GrowingTreeWithTimur(
    trunkGrowth: Float,
    leaf1Scale: Float,
    leaf2Scale: Float,
    leaf3Scale: Float,
    timurScale: Float,
    timurRotation: Float,
    idleFloat: Float,
    leafSway: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .width(20.dp)
                .height(78.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 34.dp)
                .graphicsLayer {
                scaleY = trunkGrowth
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
                .background(ColorTrunk, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        )

        Box(
            modifier = Modifier
                .size(78.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 34.dp, y = (-96).dp)
                .graphicsLayer {
                scaleX = leaf1Scale
                scaleY = leaf1Scale
                rotationZ = leafSway
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
                .background(SummerTheme.colors.statGreenEnd, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(58.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 6.dp, y = (-72).dp)
                .graphicsLayer {
                scaleX = leaf2Scale
                scaleY = leaf2Scale
                rotationZ = -leafSway
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
                .background(SummerTheme.colors.statGreenStart, CircleShape)
        )
        Box(
            modifier = Modifier
                .size(58.dp)
                .align(Alignment.BottomCenter)
                .offset(x = 62.dp, y = (-72).dp)
                .graphicsLayer {
                scaleX = leaf3Scale
                scaleY = leaf3Scale
                rotationZ = leafSway
                transformOrigin = TransformOrigin(0.5f, 1f)
            }
                .background(
                lerp(SummerTheme.colors.statGreenStart, SummerTheme.colors.goldWarm, 0.35f),
                CircleShape
            )
        )

        Box(
            modifier = Modifier
                .size(190.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-6).dp)
                .graphicsLayer {
                translationY = idleFloat
                rotationZ = timurRotation
                scaleX = timurScale
                scaleY = timurScale
            },
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(
                    Brush.radialGradient(
                        colors = listOf(
                            SummerTheme.colors.goldWarm.copy(alpha = 0.35f),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
            )
            Image(
                painter = painterResource(id = R.drawable.welcome),
                contentDescription = "Timur",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(170.dp)
            )
        }
    }
}
