package com.menna.nabatan_7asena.presentation.screens.rewards

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.VerseBuilderItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun VerseBuilder(viewModel: KidsRewardsViewModel, onFinish: (Int) -> Unit) {
    val verses by produceState<List<VerseBuilderItem>>(emptyList()) {
        value = viewModel.verses().distinctBy { it.words }.shuffled().take(5)
    }
    if (verses.isEmpty()) {
        CircularProgressIndicator(color = RewardPurple)
        return
    }
    var index by remember { mutableIntStateOf(0) }
    val verse = verses[index]
    val tokens = remember(verse) {
        val shuffled = verse.words.indices.shuffled()
        if (shuffled.map(verse.words::get) != verse.words) shuffled else {
            val other = shuffled.indexOfFirst { verse.words[it] != verse.words[shuffled.first()] }
            shuffled.toMutableList().apply { this[0] = shuffled[other]; this[other] = shuffled[0] }
        }
    }
    var used by remember(verse) { mutableStateOf(emptyList<Int>()) }
    var checked by remember(verse) { mutableStateOf<Boolean?>(null) }

    RewardText("${verse.reference}  ·  ${arabicNumber(index + 1)} / ${arabicNumber(verses.size)}", bold = true)
    RoundProgress((index + if (checked == true) 1 else 0).toFloat() / verses.size)
    Surface(
        color = when (checked) { true -> RewardMint; false -> RewardRose; null -> RewardCream },
        shape = RewardShape
    ) {
        Box(Modifier.fillMaxWidth().heightIn(min = 64.dp).padding(12.dp)) {
            RewardText(if (used.isEmpty()) "ستظهر كلمات الآية هنا" else used.joinToString(" ") { verse.words[it] },
                size = 25, bold = used.isNotEmpty())
        }
    }
    if (checked != true) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            tokens.filterNot(used::contains).forEach { token ->
                key(token) { RewardButton(verse.words[token], { used = used + token; checked = null }) }
            }
        }
        if (checked == false) RewardText("راجع ترتيب الكلمات وحاول مرة أخرى.")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RewardButton("تراجع", { used = used.dropLast(1); checked = null }, enabled = used.isNotEmpty())
            RewardButton("تحقّق", { checked = used.map(verse.words::get) == verse.words },
                enabled = used.size == verse.words.size && checked == null, color = RewardGold)
        }
    } else {
        RewardText("أحسنت! ترتيب الكلمات صحيح.", bold = true)
        val last = index == verses.lastIndex
        RewardButton(if (last) "اجمع النجوم" else "الآية التالية",
            { if (last) onFinish(10) else index++ }, color = RewardGold)
    }
}
