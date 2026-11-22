package com.menna.nabatan_7asena.presentation.screens.prayerguide

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform

internal enum class GuideScene(val isWudu: Boolean = true) {
    Intention, WashHands, RinseMouth, RinseNose, WashFace, WashArms, WipeHead, WashFeet, Dua,
    Qibla(false), Takbir(false), Recitation(false), Bow(false), Rise(false), Prostration(false),
    Sit(false), Tashahhud(false), Taslim(false)
}

private val Skin = Color(0xFFFFBA88)
private val SkinShadow = Color(0xFFDC8864)
private val Navy = Color(0xFF454477)
private val Lilac = Color(0xFF9483DA)
private val Mint = Color(0xFF61C6AE)
private val Water = Color(0xFF36B5DE)
private val Gold = Color(0xFFFFC45B)

@Composable
internal fun GuideArtwork(scene: GuideScene, modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val unit = minOf(size.width / 200f, size.height / 120f)
        withTransform({
            translate((size.width - 200f * unit) / 2f, (size.height - 120f * unit) / 2f)
            scale(unit, unit, pivot = Offset.Zero)
        }) {
            drawOval(Color.White.copy(alpha = .64f), pt(29, 7), Size(142f, 104f))
            sparkle(28, 29, 5, Gold)
            sparkle(171, 31, 7, Gold)
            sparkle(160, 14, 3, Lilac)
            sparkle(36, 93, 4, if (scene.isWudu) Water else Lilac)
            if (scene.isWudu) drawWudu(scene) else drawPrayer(scene)
        }
    }
}

private fun pt(x: Int, y: Int) = Offset(x.toFloat(), y.toFloat())

private fun DrawScope.line(color: Color, width: Float, points: List<Offset>) {
    val path = Path().apply {
        moveTo(points.first().x, points.first().y)
        points.drop(1).forEach { lineTo(it.x, it.y) }
    }
    drawPath(path, color, style = Stroke(width, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round))
}

private fun DrawScope.sparkle(x: Int, y: Int, radius: Int, color: Color) {
    line(color, 2f, listOf(pt(x - radius, y), pt(x + radius, y)))
    line(color, 2f, listOf(pt(x, y - radius), pt(x, y + radius)))
}

private fun DrawScope.drop(x: Int, y: Int, scale: Float = 1f) {
    val at = pt(x, y)
    val tip = Path().apply {
        moveTo(at.x, at.y - 10 * scale)
        cubicTo(at.x + 18 * scale, at.y + 9 * scale, at.x - 18 * scale, at.y + 9 * scale, at.x, at.y - 10 * scale)
        close()
    }
    drawPath(tip, Water)
    drawCircle(Color.White.copy(alpha = .85f), 1.5f * scale, at + Offset(-2f * scale, -1f * scale))
}

private fun DrawScope.hand(x: Int, y: Int, inward: Int) {
    drawOval(SkinShadow, pt(x - 12, y - 1), Size(24f, 29f))
    drawOval(Skin, pt(x - 12, y - 4), Size(24f, 28f))
    for (finger in -1..1) {
        line(Skin, 5f, listOf(pt(x + finger * 6, y + 1), pt(x + finger * 7, y - 12 + finger * inward)))
    }
    line(Skin, 7f, listOf(pt(x + 8 * inward, y + 13), pt(x + 15 * inward, y + 4)))
}

private fun DrawScope.frontFace(x: Int = 100, y: Int = 55) {
    drawCircle(SkinShadow, 7f, pt(x - 28, y))
    drawCircle(SkinShadow, 7f, pt(x + 28, y))
    drawCircle(Skin, 28f, pt(x, y))
    drawArc(Navy, 180f, 180f, true, pt(x - 28, y - 33), Size(56f, 45f))
    drawCircle(Navy, 2.6f, pt(x - 11, y + 4))
    drawCircle(Navy, 2.6f, pt(x + 11, y + 4))
    drawArc(SkinShadow, 5f, 170f, false, pt(x - 7, y + 10), Size(14f, 8f), style = Stroke(2f))
}

private fun DrawScope.profileFace() {
    drawOval(Skin, pt(68, 26), Size(59f, 64f))
    drawArc(Navy, 160f, 200f, true, pt(67, 21), Size(55f, 48f))
    drawCircle(Skin, 14f, pt(111, 52))
    line(Skin, 8f, listOf(pt(120, 49), pt(133, 58), pt(123, 62)))
    drawCircle(Navy, 3f, pt(116, 48))
    drawCircle(SkinShadow, 6f, pt(89, 60))
    line(Navy, 3f, listOf(pt(119, 71), pt(126, 71)))
}

private fun DrawScope.drawWudu(scene: GuideScene) {
    when (scene) {
        GuideScene.Intention -> {
            val heart = Path().apply {
                moveTo(100f, 91f)
                cubicTo(47f, 57f, 61f, 23f, 83f, 30f)
                quadraticTo(94f, 31f, 100f, 43f)
                quadraticTo(106f, 31f, 117f, 30f)
                cubicTo(139f, 23f, 153f, 57f, 100f, 91f)
                close()
            }
            drawPath(heart, Color(0xFFF68DAB))
            sparkle(85, 49, 8, Color.White)
            drop(144, 78)
        }
        GuideScene.WashHands -> {
            line(Mint, 16f, listOf(pt(56, 96), pt(77, 75)))
            line(Lilac, 16f, listOf(pt(145, 92), pt(122, 72)))
            hand(82, 57, 1)
            hand(117, 54, -1)
            drop(98, 26)
            drop(103, 56, .8f)
            drop(105, 80)
        }
        GuideScene.RinseMouth, GuideScene.RinseNose -> {
            profileFace()
            val nose = scene == GuideScene.RinseNose
            val level = if (nose) 58 else 75
            drawCircle(Water.copy(alpha = .45f), 10f, pt(130, level), style = Stroke(2f))
            drop(147, level + 2)
            drop(157, level + 14, .7f)
            line(Water, 3f, listOf(pt(151, level + 10), pt(142, level + 6), pt(136, level)))
        }
        GuideScene.WashFace -> {
            frontFace(y = 49)
            drop(67, 56, .9f)
            drop(82, 70, .7f)
            drop(119, 65)
            drop(135, 52, .8f)
            hand(63, 82, 1)
            hand(137, 82, -1)
        }
        GuideScene.WashArms -> {
            line(SkinShadow, 23f, listOf(pt(55, 78), pt(102, 79), pt(141, 51)))
            line(Skin, 18f, listOf(pt(55, 74), pt(101, 75), pt(138, 47)))
            line(Lilac, 25f, listOf(pt(49, 74), pt(66, 74)))
            hand(140, 39, -1)
            drawCircle(Gold, 14f, pt(103, 76), style = Stroke(2.5f))
            drop(96, 44)
            drop(112, 56, .8f)
            drop(118, 89, .75f)
        }
        GuideScene.WipeHead -> {
            frontFace(y = 58)
            line(Mint, 13f, listOf(pt(48, 81), pt(65, 61), pt(79, 42)))
            line(Mint, 13f, listOf(pt(152, 81), pt(135, 61), pt(121, 42)))
            hand(80, 37, 1)
            hand(120, 37, -1)
            drawCircle(Water, 8f, pt(72, 60), style = Stroke(2f))
            drawCircle(Water, 8f, pt(128, 60), style = Stroke(2f))
            drop(101, 25, .7f)
        }
        GuideScene.WashFeet -> {
            for (x in listOf(81, 119)) {
                line(Lilac, 18f, listOf(pt(x - 4, 25), pt(x - 4, 53)))
                line(SkinShadow, 19f, listOf(pt(x - 4, 54), pt(x - 1, 76), pt(x + 12, 86)))
                line(Skin, 16f, listOf(pt(x - 4, 54), pt(x - 1, 74), pt(x + 12, 83)))
                drop(x - 17, 72, .8f)
            }
            line(Water, 3f, listOf(pt(60, 100), pt(140, 100)))
        }
        GuideScene.Dua -> {
            line(Lilac, 15f, listOf(pt(75, 101), pt(79, 69)))
            line(Lilac, 15f, listOf(pt(125, 101), pt(121, 69)))
            hand(78, 52, 1)
            hand(122, 52, -1)
            sparkle(100, 32, 12, Gold)
            sparkle(101, 60, 5, Gold)
        }
        else -> Unit
    }
}

private fun DrawScope.drawPrayer(scene: GuideScene) {
    drawRoundRect(Mint, pt(39, 96), Size(122f, 14f), CornerRadius(6f))
    drawRoundRect(Color(0xFFD2F7E5), pt(47, 98), Size(106f, 5f), CornerRadius(2f))
    when (scene) {
        GuideScene.Bow -> {
            body(
                hip = pt(79, 62), shoulder = pt(113, 66), head = pt(130, 70),
                legs = listOf(pt(76, 82), pt(75, 95)),
                arm = listOf(pt(107, 78), pt(83, 84))
            )
        }
        GuideScene.Prostration -> {
            body(
                hip = pt(76, 68), shoulder = pt(113, 84), head = pt(133, 89),
                legs = listOf(pt(91, 94), pt(58, 96)),
                arm = listOf(pt(110, 89), pt(119, 96))
            )
        }
        GuideScene.Sit, GuideScene.Tashahhud, GuideScene.Taslim -> {
            body(
                hip = pt(82, 85), shoulder = pt(91, 62),
                head = if (scene == GuideScene.Taslim) pt(89, 47) else pt(93, 47),
                legs = listOf(pt(116, 89), pt(107, 96), pt(66, 96)),
                arm = listOf(pt(104, 78), pt(117, 83)),
                faceDirection = if (scene == GuideScene.Taslim) -1 else 1
            )
            if (scene == GuideScene.Tashahhud) line(Skin, 3f, listOf(pt(118, 83), pt(129, 78)))
            if (scene == GuideScene.Taslim) {
                arrow(73, 33, -1)
                arrow(107, 33, 1)
            }
        }
        GuideScene.Takbir, GuideScene.Qibla, GuideScene.Recitation, GuideScene.Rise -> {
            val center = if (scene == GuideScene.Qibla) 77 else 100
            val shoulders = pt(center, 49)
            val arm = when (scene) {
                GuideScene.Takbir -> listOf(pt(center + 20, 58), pt(center + 23, 37))
                GuideScene.Recitation -> listOf(pt(center + 19, 64), pt(center + 3, 66))
                else -> listOf(pt(center + 13, 60), pt(center + 13, 74))
            }
            body(pt(center, 77), shoulders, pt(center, 33), listOf(pt(center, 91), pt(center + 7, 96)), arm)
            when (scene) {
                GuideScene.Takbir -> {
                    line(Lilac, 8f, listOf(shoulders, pt(center - 20, 58), pt(center - 22, 39)))
                    drawCircle(Skin, 5f, pt(center - 22, 36))
                }
                GuideScene.Qibla -> {
                    drawRoundRect(Navy, pt(126, 54), Size(32f, 38f), CornerRadius(3f))
                    drawRoundRect(Gold, pt(126, 63), Size(32f, 6f), CornerRadius(1f))
                    drawRoundRect(Gold, pt(145, 76), Size(7f, 12f), CornerRadius(1f))
                    arrow(101, 66, 1)
                }
                GuideScene.Recitation -> {
                    line(Lilac, 7f, listOf(shoulders, pt(center - 13, 65), pt(center + 3, 69)))
                    drawCircle(Skin, 4f, pt(center + 3, 69))
                    for (n in 0..2) line(Gold, 2.5f, listOf(pt(123, 27 + n * 6), pt(141 - n * 3, 27 + n * 6)))
                }
                GuideScene.Rise -> {
                    line(Lilac, 8f, listOf(shoulders, pt(center - 13, 60), pt(center - 13, 74)))
                    drawCircle(Skin, 5f, pt(center - 13, 74))
                    line(Gold, 3f, listOf(pt(130, 70), pt(130, 38), pt(124, 45)))
                    line(Gold, 3f, listOf(pt(130, 38), pt(136, 45)))
                }
                else -> Unit
            }
        }
        else -> Unit
    }
}

private fun DrawScope.body(
    hip: Offset, shoulder: Offset, head: Offset, legs: List<Offset>, arm: List<Offset>,
    faceDirection: Int = 1
) {
    line(Navy, 11f, listOf(hip) + legs)
    line(Lilac, 20f, listOf(hip, shoulder))
    line(Lilac, 9f, listOf(shoulder) + arm)
    drawCircle(Skin, 5f, arm.last())
    drawCircle(Skin, 12f, head)
    drawArc(Navy, 180f, 180f, true, head - Offset(12f, 13f), Size(24f, 17f))
    drawCircle(Navy, 1.8f, head + Offset(faceDirection * 6f, 0f))
    drawCircle(SkinShadow, 2.5f, head + Offset(faceDirection * 11f, 4f))
}

private fun DrawScope.arrow(x: Int, y: Int, direction: Int) {
    val tip = x + 11 * direction
    line(Gold, 2.5f, listOf(pt(x, y), pt(tip, y), pt(tip - 5 * direction, y - 4)))
    line(Gold, 2.5f, listOf(pt(tip, y), pt(tip - 5 * direction, y + 4)))
}
