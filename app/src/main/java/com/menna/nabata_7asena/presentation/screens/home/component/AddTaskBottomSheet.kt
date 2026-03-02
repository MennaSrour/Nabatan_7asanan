package com.menna.nabata_7asena.presentation.screens.home.component

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.menna.nabata_7asena.domain.entity.ExtraTasks
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
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp)
        ) {
            Text(
                "إضافة عمل صالح 🌿",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2E3E5C),
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )

            
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                listOf("مهمات", "نوافل", "قرآن", "أذكار").forEach { category ->
                    CategoryChip(category, selectedCategory == category) {
                        selectedCategory = category
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            
            LazyColumn(
                modifier = Modifier.heightIn(max = 400.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val items = when (selectedCategory) {
                    "نوافل" -> listOf(
                        "سنة الفجر (ركعتين)",
                        "صلاة الضحى",
                        "سنة الظهر",
                        "سنة المغرب",
                        "سنة العشاء",
                        "قيام الليل",
                        "صلاة الوتر"
                    )

                    "قرآن" -> suggestions.werd
                    "مهمات" -> suggestions.challenges
                    else -> listOf(
                        "أذكار الصباح",
                        "أذكار المساء",
                        "أذكار النوم",
                        "استغفار (100)",
                        "صلاة على النبي (100)"
                    )
                }

                items(items) { taskTitle ->
                    ExtraTaskItem(taskTitle) {
                        onTaskSelected(taskTitle)
                        onDismiss()
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryChip(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (isSelected) Color(0xFF66BB6A) else Color(0xFFF5F5F5))
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Text(
            text,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ExtraTaskItem(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFFAFAFA))
            .border(1.dp, Color(0xFFEEEEEE), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF455A64))
        Icon(Icons.Filled.Add, null, tint = Color(0xFF66BB6A))
    }
}