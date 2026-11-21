package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private val memoryPairs = listOf(
    "☾" to "هلال", "✦" to "أمانة", "❋" to "رحمة", "♡" to "محبة",
    "◈" to "الإخلاص", "✿" to "إحسان", "❖" to "الفلق", "✧" to "صدق"
)

@Composable
internal fun MemoryMatch(bonus: Boolean, onFinish: (Int) -> Unit) {
    val pairCount = if (bonus) 8 else 6
    val deck = remember { (0 until pairCount).flatMap { listOf(it, it) }.shuffled() }
    var faceUp by remember { mutableStateOf(emptyList<Int>()) }
    var matched by remember { mutableStateOf(emptySet<Int>()) }
    var moves by remember { mutableIntStateOf(0) }

    LaunchedEffect(faceUp) {
        if (faceUp.size == 2) {
            val (first, second) = faceUp
            val same = deck[first] == deck[second]
            delay(if (same) 420L else 850L)
            if (same) matched = matched + first + second
            faceUp = emptyList()
            if (matched.size == deck.size) onFinish(if (moves <= pairCount + 3) 10 else 6)
        }
    }

    RewardText("المحاولات ${arabicNumber(moves)}  ·  الأزواج ${arabicNumber(matched.size / 2)} / ${arabicNumber(pairCount)}", bold = true)
    deck.indices.chunked(4).forEach { row ->
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            row.forEach { index ->
                val isMatched = index in matched
                val show = index in faceUp || isMatched
                val (icon, label) = memoryPairs[deck[index]]
                val flip by animateFloatAsState(if (show) 180f else 0f, tween(220))
                val front = flip >= 90f
                Surface(
                    Modifier.weight(1f).heightIn(min = 70.dp).aspectRatio(.88f).graphicsLayer {
                        rotationY = flip
                        cameraDistance = 14f * density
                    }.clip(RewardShape).clickable(
                        enabled = !show && faceUp.size < 2,
                        role = Role.Button,
                        onClickLabel = "اقلب البطاقة"
                    ) {
                        if (faceUp.size == 1) moves++
                        faceUp = faceUp + index
                    }.semantics {
                        contentDescription = when {
                            isMatched -> "زوج مكتمل، $label"
                            show -> label
                            else -> "بطاقة مخفية ${arabicNumber(index + 1)}"
                        }
                    },
                    color = when {
                        isMatched -> RewardMint
                        front -> RewardGold
                        else -> RewardLavender
                    },
                    shape = RewardShape
                ) {
                    Column(
                        Modifier.padding(4.dp).graphicsLayer { rotationY = if (front) 180f else 0f },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RewardText(if (front) icon else "؟", size = 24, bold = true)
                        if (front) RewardText(label, size = 13, bold = true)
                    }
                }
            }
        }
    }
}
