package com.menna.nabata_7asena.presentation.screens.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.menna.nabata_7asena.domain.entity.ExtraTasks
import com.menna.nabata_7asena.ui.theme.SummerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskBottomSheet(
    onDismiss: () -> Unit,
    onTaskSelected: (String) -> Unit,
    suggestions: ExtraTasks
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedCategory by remember { mutableStateOf("نوافل") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SummerTheme.colors.backgroundLight,
        shape = SummerTheme.shapes.bottomSheet
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = SummerTheme.dimensions.paddingScreenHorizontal)
                .padding(bottom = SummerTheme.dimensions.paddingScreenBottom)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = SummerTheme.dimensions.paddingLarge),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = SummerTheme.colors.goldWarm,
                    modifier = Modifier.size(SummerTheme.dimensions.iconLarge)
                )
                Spacer(Modifier.width(SummerTheme.dimensions.paddingSmall))
                Text(
                    text = "إضافة عمل صالح",
                    style = SummerTheme.typography.bottomSheetTitle,
                    color = SummerTheme.colors.textPrimary
                )
                Spacer(Modifier.width(SummerTheme.dimensions.paddingSmall))
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = SummerTheme.colors.goldWarm,
                    modifier = Modifier.size(SummerTheme.dimensions.iconLarge)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(SummerTheme.dimensions.paddingMedium)
            ) {
                listOf("نوافل", "قرآن", "مهمات", "أذكار").forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val backgroundColor = if (isSelected) SummerTheme.colors.goldWarm else SummerTheme.colors.chipUnselected
                    val borderColor = if (isSelected) SummerTheme.colors.goldDeep else SummerTheme.colors.transparent
                    val textColor = if (isSelected) SummerTheme.colors.white else SummerTheme.colors.textBrown

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(SummerTheme.shapes.chip)
                            .background(backgroundColor)
                            .border(2.dp, borderColor, SummerTheme.shapes.chip)
                            .clickable { selectedCategory = cat }
                            .padding(vertical = 9.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = cat,
                            color = textColor,
                            style = SummerTheme.typography.chipText
                        )
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.heightIn(max = SummerTheme.dimensions.bottomSheetMaxHeight),
                verticalArrangement = Arrangement.spacedBy(SummerTheme.dimensions.paddingMedium)
            ) {
                val items = when (selectedCategory) {
                    "نوافل" -> listOf(
                        "سنة الفجر ركعتين", "صلاة الضحى", "سنة الظهر",
                        "سنة المغرب", "سنة العشاء", "قيام الليل", "صلاة الوتر"
                    )
                    "قرآن" -> suggestions.werd
                    "مهمات" -> listOf(
                        "تعلم دعاء جديد", "ساعد والديك اليوم", "ابتسم وتحدث بلين",
                        "تصدق بصدقة بسيطة", "اقرأ حديثاً شريفاً", "تأمل في نعم الله عليك",
                        "احفظ آية جديدة", "اقرأ قصة إسلامية"
                    )
                    else -> listOf(
                        "أذكار الصباح", "أذكار المساء", "أذكار النوم",
                        "استغفار 100", "صلاة على النبي 100"
                    )
                }

                items(items) { title ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(SummerTheme.shapes.taskCard)
                            .background(SummerTheme.colors.cardBackground)
                            .border(1.5.dp, SummerTheme.colors.goldWarm.copy(alpha = 0.3f), SummerTheme.shapes.taskCard)
                            .clickable { onTaskSelected(title); onDismiss() }
                            .padding(SummerTheme.dimensions.paddingLarge),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = title,
                            style = SummerTheme.typography.taskTitle,
                            color = SummerTheme.colors.textDarkBrown
                        )
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = null,
                            tint = SummerTheme.colors.goldWarm,
                            modifier = Modifier.size(SummerTheme.dimensions.iconSmall)
                        )
                    }
                }
            }
        }
    }
}