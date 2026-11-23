package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.*
import androidx.compose.ui.unit.*
import com.menna.nabatan_7asena.domain.entity.AyahModel
import com.menna.nabatan_7asena.presentation.screens.quran.QuranActions
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlin.math.ceil
import kotlin.math.roundToInt

@Composable
internal fun MushafPageBody(
    ayahs: List<AyahModel>, active: Pair<Int, Int>?, actions: QuranActions,
    modifier: Modifier = Modifier
) {
    val prefix = stringResource(R.string.quran_surah_prefix)
    val sections = remember(ayahs, prefix) { mushafSections(ayahs, prefix) }
    val quranFont = quranFontFamily()
    val bannerFont = DownloadLabelFont
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer(cacheSize = 0)
    // Invalidate fitting if bundled fonts finish loading after the initial composition.
    val resolvedQuranFont = LocalFontFamilyResolver.current.resolve(quranFont).value
    val resolvedBannerFont = LocalFontFamilyResolver.current.resolve(bannerFont).value
    BoxWithConstraints(modifier.padding(vertical = 3.dp), contentAlignment = Alignment.Center) {
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        if (width == 0 || height == 0 || sections.isEmpty()) return@BoxWithConstraints
        val bannerInset = minOf(24.dp, maxWidth / 12)
        val bannerInsetPx = with(density) { bannerInset.roundToPx() }
        val maximumSp = (maxWidth.value / 11f).coerceIn(26f, 72f)
        val fitted = remember(sections, width, height, density, resolvedQuranFont, resolvedBannerFont) {
            fitMushafTypography(maximumSp) { size, lineHeight ->
                val gap = sectionGapPx(size, density)
                var remaining = height - gap * (sections.size - 1)
                sections.all { section ->
                    val measured = measurer.measure(
                        section.text,
                        style = sectionStyle(section, size, lineHeight, quranFont, bannerFont),
                        constraints = Constraints(maxWidth = if (section is MushafSection.Banner) {
                            (width - 2 * bannerInsetPx).coerceAtLeast(1)
                        } else width),
                        layoutDirection = LayoutDirection.Rtl,
                        density = density
                    )
                    remaining -= measured.size.height + 2 * sectionPaddingPx(section, size, density)
                    !measured.didOverflowWidth && remaining >= 0
                }
            }
        }
        val gap = with(density) { sectionGapPx(fitted.fontSizeSp, density).toDp() }
        val pageContent: @Composable () -> Unit = {
            Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(gap)) {
                sections.forEach { section ->
                    MushafSectionContent(
                        section, fitted, quranFont, bannerFont, active, actions,
                        bannerInset, Modifier.fillMaxWidth()
                    )
                }
            }
        }
        if (fitted.fits) {
            pageContent()
        } else {
            // Even a very short split-screen window must retain every section.
            Layout(content = pageContent) { measurables, frame ->
                val page = measurables.single().measure(frame.copy(minHeight = 0, maxHeight = Constraints.Infinity))
                val scale = (frame.maxHeight.toFloat() / page.height.coerceAtLeast(1)).coerceAtMost(1f)
                layout(page.width, frame.constrainHeight((page.height * scale).roundToInt())) {
                    page.placeWithLayer(0, 0) {
                        transformOrigin = TransformOrigin(0.5f, 0f)
                        scaleX = scale
                        scaleY = scale
                    }
                }
            }
        }
    }
}

private fun sectionGapPx(size: Float, density: Density): Int =
    with(density) { minOf(2.dp.toPx(), (size * 0.12f).sp.toPx()).toInt() }

private fun sectionPaddingPx(section: MushafSection, size: Float, density: Density): Int = with(density) {
    when (section) {
        is MushafSection.Banner -> minOf(4.dp.toPx(), (size * 0.2f).sp.toPx()).toInt()
        is MushafSection.Verses -> ceil((size * 0.5f).sp.toPx()).toInt()
        MushafSection.Basmalah -> minOf(2.dp.toPx(), (size * 0.12f).sp.toPx()).toInt()
    }
}

private fun sectionStyle(section: MushafSection, size: Float, lineHeight: Float, quranFont: FontFamily, bannerFont: FontFamily) = TextStyle(
    fontFamily = if (section is MushafSection.Banner) bannerFont else quranFont,
    fontWeight = FontWeight.Normal, fontSize = size.sp, letterSpacing = 0.sp,
    lineHeight = lineHeight.sp, textDirection = TextDirection.Rtl,
    textAlign = if (section is MushafSection.Verses && !section.centered) TextAlign.Justify else TextAlign.Center,
    lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None)
)

@Composable
private fun MushafSectionContent(
    section: MushafSection, fitted: MushafTypography, quranFont: FontFamily, bannerFont: FontFamily,
    active: Pair<Int, Int>?, actions: QuranActions, bannerInset: Dp, modifier: Modifier
) {
    val style = sectionStyle(section, fitted.fontSizeSp, fitted.lineHeightSp, quranFont, bannerFont)
    val padding = with(LocalDensity.current) { sectionPaddingPx(section, fitted.fontSizeSp, this).toDp() }
    if (section is MushafSection.Banner) MushafSurahBanner(section.text, style, bannerInset, padding, modifier)
    else {
        val palette = SummerTheme.quran
        val text = remember(section, active, palette) { sectionText(section, active, palette.accentSoft, actions) }
        BasicText(text, style = style.copy(color = palette.textPrimary), overflow = TextOverflow.Visible,
            modifier = modifier.padding(vertical = padding))
    }
}

private fun sectionText(section: MushafSection, active: Pair<Int, Int>?, highlight: Color, actions: QuranActions): AnnotatedString {
    if (section !is MushafSection.Verses) return AnnotatedString(section.text)
    return buildAnnotatedString {
        section.ayahs.forEachIndexed { index, ayah ->
            if (index > 0) append(" ")
            val selected = active == (ayah.suraNo to ayah.ayaNo)
            withLink(LinkAnnotation.Clickable("${ayah.suraNameAr} ${com.menna.nabatan_7asena.core.util.arabicIndic(ayah.ayaNo)}",
                styles = TextLinkStyles(style = SpanStyle(background = if (selected) highlight else Color.Transparent, textDecoration = TextDecoration.None)),
                linkInteractionListener = { actions.playAyahAudio(ayah.suraNo, ayah.ayaNo) })) {
                append(com.menna.nabatan_7asena.domain.util.uthmaniText(ayah.text, ayah.ayaNo))
                append(" " + com.menna.nabatan_7asena.core.util.ayahMarker(ayah.ayaNo))
            }
        }
    }
}
