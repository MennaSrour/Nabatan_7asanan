package com.menna.nabatan_7asena.presentation.screens.quran.composables

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

private val SectionGap = 3.dp

@Composable
internal fun MushafPageBody(
    ayahs: List<AyahModel>, active: Pair<Int, Int>?, actions: QuranActions,
    onFallbackChanged: (Boolean) -> Unit, modifier: Modifier = Modifier
) {
    val prefix = stringResource(R.string.quran_surah_prefix)
    val sections = remember(ayahs, prefix) { mushafSections(ayahs, prefix) }
    val quranFont = quranFontFamily()
    val bannerFont = DownloadLabelFont
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer(cacheSize = 32)
    // Invalidate fitting if bundled fonts finish loading after the initial composition.
    val resolvedQuranFont = LocalFontFamilyResolver.current.resolve(quranFont).value
    val resolvedBannerFont = LocalFontFamilyResolver.current.resolve(bannerFont).value
    BoxWithConstraints(modifier.padding(vertical = 3.dp)) {
        val width = constraints.maxWidth.coerceAtLeast(1)
        val height = constraints.maxHeight
        val fitted = remember(sections, width, height, density, resolvedQuranFont, resolvedBannerFont) {
            fitMushafTypography { size, lineHeight ->
                measuredSections(sections, size, lineHeight, width, density, quranFont, bannerFont, measurer)
                    .let { it.sum() + with(density) { SectionGap.roundToPx() } * (sections.size - 1).coerceAtLeast(0) <= height }
            }
        }
        val heights = remember(sections, fitted, width, density, resolvedQuranFont, resolvedBannerFont) {
            measuredSections(sections, fitted.fontSizeSp, fitted.lineHeightSp, width, density, quranFont, bannerFont, measurer)
        }
        SideEffect { onFallbackChanged(!fitted.fits) }
        val bodyModifier = if (fitted.fits) Modifier.fillMaxWidth()
            else Modifier.fillMaxWidth().verticalScroll(rememberScrollState())
        Column(bodyModifier, verticalArrangement = Arrangement.spacedBy(SectionGap)) {
            sections.forEachIndexed { index, section ->
                val sectionModifier = Modifier.fillMaxWidth().height(with(density) { heights[index].toDp() })
                MushafSectionContent(section, fitted, quranFont, bannerFont, active, actions, sectionModifier)
            }
        }
    }
}

private fun measuredSections(
    sections: List<MushafSection>, size: Float, lineHeight: Float, width: Int,
    density: Density, quranFont: FontFamily, bannerFont: FontFamily, measurer: TextMeasurer
): List<Int> = sections.map { section ->
    val horizontalInset = if (section is MushafSection.Banner) with(density) { 48.dp.roundToPx() } else 0
    val style = sectionStyle(section, size, lineHeight, quranFont, bannerFont)
    val measured = measurer.measure(section.text, style = style,
        constraints = Constraints(maxWidth = (width - horizontalInset).coerceAtLeast(1)),
        layoutDirection = LayoutDirection.Rtl, density = density, overflow = TextOverflow.Visible)
    measured.size.height + sectionPaddingPx(section, size, density) * 2
}

private fun sectionPaddingPx(section: MushafSection, size: Float, density: Density): Int = with(density) {
    when (section) {
        is MushafSection.Banner -> 4.dp.roundToPx()
        is MushafSection.Verses -> ceil((size * 0.75f).sp.toPx()).toInt() // Space for tall first/last-line Quranic marks.
        MushafSection.Basmalah -> 2.dp.roundToPx()
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
    active: Pair<Int, Int>?, actions: QuranActions, modifier: Modifier
) {
    val style = sectionStyle(section, fitted.fontSizeSp, fitted.lineHeightSp, quranFont, bannerFont)
    if (section is MushafSection.Banner) MushafSurahBanner(section.text, style, modifier)
    else {
        val palette = SummerTheme.quran
        val text = remember(section, active, palette) { sectionText(section, active, palette.accentSoft, actions) }
        val padding = with(LocalDensity.current) { sectionPaddingPx(section, fitted.fontSizeSp, this).toDp() }
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
            withLink(LinkAnnotation.Clickable("${ayah.suraNameAr} ${ayah.ayaNo}",
                styles = TextLinkStyles(style = SpanStyle(background = if (selected) highlight else Color.Transparent, textDecoration = TextDecoration.None)),
                linkInteractionListener = { actions.playAyahAudio(ayah.suraNo, ayah.ayaNo) })) {
                append(com.menna.nabatan_7asena.domain.util.uthmaniText(ayah.text, ayah.ayaNo))
                append(" " + com.menna.nabatan_7asena.core.util.ayahMarker(ayah.ayaNo))
            }
        }
    }
}
