package com.menna.nabatan_7asena.presentation.screens.home.composables.garden

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.home.HomeUiState
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource

@Composable
fun GardenCard(state: HomeUiState, onEnter: () -> Unit) {
    val garden = state.garden
    if (garden == null && !state.isGardenUnavailable) return
    val night = state.isNightTheme
    val text = if (night) Color(0xFFF6EBD8) else Color(0xFF264C3D)
    val shape = RoundedCornerShape(26.dp)
    Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 9.dp).padding(bottom = 6.dp)) {
        Box(Modifier.matchParentSize().offset(y = 6.dp).background(Color(0xFF796044), shape))
        Surface(onClick = onEnter, shape = shape, color = Color.Transparent, border = BorderStroke(2.dp, Color(0xFFA2865E))) {
            Row(Modifier.fillMaxWidth().background(Brush.horizontalGradient(if (night) listOf(Color(0xFF2B4845), Color(0xFF3E5D50))
                else listOf(Color(0xFFDDF1CE), Color(0xFFB3D9BA)))).padding(14.dp), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GardenEntryIllustration(night, Modifier.size(85.dp))
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    Text(stringResource(R.string.garden_title), color = text, fontWeight = FontWeight.ExtraBold)
                    Text(if (garden == null) stringResource(R.string.garden_unavailable)
                        else stringResource(R.string.world_entry_stats, garden.trees.count { it.plotIndex != null }, garden.waterDrops),
                        color = text, style = MaterialTheme.typography.bodySmall)
                    Surface(color = Color(0xFFF6D58A), shape = RoundedCornerShape(12.dp)) {
                        Text(genderedStringResource(GenderedStrings.gardenEnter), color = Color(0xFF604B32), fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun GardenEntryIllustration(night: Boolean, modifier: Modifier) {
    val phase = rememberGardenPhase()
    Canvas(modifier) {
        val s = size.minDimension / 120f
        drawCircle((if (night) Color(0xFF354C66) else Color(0xFFFFE9BB)), size.minDimension / 2)
        val trunk = Path().apply { moveTo(58f * s, 103f * s); quadraticTo(52f * s, 78f * s, 63f * s, 49f * s) }
        drawPath(trunk, Color(0xFF896348), style = Stroke(9f * s, cap = StrokeCap.Round))
        listOf(Offset(40f, 55f), Offset(76f, 53f), Offset(58f, 36f), Offset(60f, 65f)).forEachIndexed { i, p ->
            drawCircle(if (i % 2 == 0) Color(0xFF4E9568) else Color(0xFF77B575), 23f * s, (p + Offset(kotlin.math.sin(phase.value * kotlin.math.PI.toFloat() * 6f + i) * 1.5f, 0f)) * s)
        }
        drawOval(Color(0xFF91B87A), Offset(17f * s, 99f * s), Size(85f * s, 13f * s))
        val light = Offset(95f * s, 23f * s)
        if (!night) drawCircle(Color(0xFFFFCA62), 14f * s, light)
        else {
            val moon = Path.combine(PathOperation.Difference, Path().apply { addOval(Rect(light - Offset(14f * s, 14f * s), light + Offset(14f * s, 14f * s))) },
                Path().apply { addOval(Rect(light + Offset(-4f * s, -18f * s), light + Offset(18f * s, 3f * s))) })
            drawPath(moon, Color(0xFFFFE5A9))
        }
        repeat(7) { i -> drawLine(Color(0xFF497C50), Offset((17 + i * 13f) * s, 108f * s), Offset((12 + i * 13f) * s, (96f - i % 2 * 5) * s), 2f * s) }
    }
}
