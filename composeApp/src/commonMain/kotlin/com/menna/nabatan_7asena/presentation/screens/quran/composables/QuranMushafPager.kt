package com.menna.nabatan_7asena.presentation.screens.quran.composables

import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.*
import androidx.compose.ui.text.font.FontWeight
import com.menna.nabatan_7asena.presentation.screens.quran.*
import com.menna.nabatan_7asena.domain.QuranConstants
import com.menna.nabatan_7asena.core.util.arabicIndic
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.stringResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.*
import com.menna.nabatan_7asena.domain.entity.AyahModel
import com.menna.nabatan_7asena.domain.util.uthmaniText
import com.menna.nabatan_7asena.core.util.ayahMarker

@Composable
fun QuranMushafPager(state: QuranUiState, actions: QuranActions, layout: QuranLayoutState, padding: PaddingValues) {
    HorizontalPager(state = layout.mushafPagerState, beyondViewportPageCount = 1,
        modifier = Modifier.padding(padding).fillMaxSize()) { index ->
        MushafPage(index + 1, state.mushafPages[index + 1].orEmpty(), state, actions)
    }
}

@Composable
private fun MushafPage(page: Int, ayahs: List<AyahModel>, state: QuranUiState, actions: QuranActions) {
    val q = SummerTheme.quran
    val active = audioAyah(state.audioState)
    val bookmark = active?.let { selected -> ayahs.firstOrNull { it.suraNo == selected.first && it.ayaNo == selected.second } } ?: ayahs.firstOrNull()
    Surface(color = Color(0xFFFFFAEF), shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, q.border), modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Column(Modifier.padding(16.dp)) {
            MushafHeader(ayahs, actions)
            MushafAyahText(ayahs, active, actions, Modifier.weight(1f))
            HorizontalDivider(color = q.border)
            Text(stringResource(R.string.quran_page_label, arabicIndic(page)), color = q.accentStrong,
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(8.dp))
            bookmark?.let { ayah ->
                TextButton(onClick = { actions.saveBookmark(ayah.suraNo, ayah.ayaNo) }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                    Text(stringResource(R.string.quran_bookmark_page, arabicIndic(ayah.ayaNo)))
                }
            }
        }
    }
}

@Composable
private fun MushafHeader(ayahs: List<AyahModel>, actions: QuranActions) {
    val first = ayahs.firstOrNull() ?: return
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(ayahs.map { it.suraNameAr }.distinct().joinToString(" · "), color = SummerTheme.quran.accentStrong)
            Text(stringResource(R.string.quran_juz_label, arabicIndic(first.jozz)), color = SummerTheme.quran.textMuted)
        }
        IconButton(onClick = { actions.onOpenRepeatBottomSheet(first.suraNo) }) {
            Icon(Icons.Rounded.Repeat, genderedStringResource(GenderedStrings.quranRepeatMemorize))
        }
        IconButton(onClick = { actions.downloadSurah(first.suraNo) }) {
            Icon(Icons.Rounded.Download, stringResource(R.string.quran_download_surah))
        }
    }
}

@Composable
private fun MushafAyahText(ayahs: List<AyahModel>, active: Pair<Int, Int>?, actions: QuranActions, modifier: Modifier) {
    val q = SummerTheme.quran
    val content = remember(ayahs, active, q) {
        buildAnnotatedString {
            ayahs.forEach { ayah ->
                if (ayah.ayaNo == 1 && ayah.suraNo !in listOf(1, 9)) {
                    if (length > 0) append("\n")
                    append("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\n")
                }
                val selected = active == (ayah.suraNo to ayah.ayaNo)
                val span = SpanStyle(background = if (selected) q.accentSoft else Color.Transparent, textDecoration = TextDecoration.None)
                withLink(LinkAnnotation.Clickable("${ayah.suraNameAr} ${arabicIndic(ayah.ayaNo)}",
                    styles = TextLinkStyles(style = span), linkInteractionListener = { actions.playAyahAudio(ayah.suraNo, ayah.ayaNo) })) {
                    append(uthmaniText(ayah.text, ayah.ayaNo)); append(" "); append(ayahMarker(ayah.ayaNo))
                }
                append(" ")
            }
        }
    }
    Column(modifier.verticalScroll(rememberScrollState()).padding(vertical = 18.dp)) {
        Text(content, fontFamily = quranFontFamily(), fontSize = 26.sp, lineHeight = 84.sp,
            color = q.textPrimary, textAlign = TextAlign.Justify,
            style = TextStyle(textDirection = TextDirection.Rtl,
                lineHeightStyle = LineHeightStyle(LineHeightStyle.Alignment.Center, LineHeightStyle.Trim.None)), modifier = Modifier.fillMaxWidth())
    }
}

fun audioAyah(audio: QuranAudioState): Pair<Int, Int>? = when (audio) {
    is QuranAudioState.Playing -> audio.suraNo to audio.ayaNo
    is QuranAudioState.Loading -> audio.suraNo to audio.ayaNo
    is QuranAudioState.Paused -> audio.suraNo to audio.ayaNo
    else -> null
}
