package com.menna.nabatan_7asena.presentation.screens.update.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.menna.nabatan_7asena.domain.entity.UpdateInfo
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource
import com.menna.nabatan_7asena.presentation.screens.update.UpdateActions

@Composable
fun UpdateContent(info: UpdateInfo, actions: UpdateActions) {
    Dialog(
        onDismissRequest = { if (!info.isForceUpdate) actions.dismissUpdate() },
        properties = DialogProperties(dismissOnBackPress = !info.isForceUpdate, dismissOnClickOutside = !info.isForceUpdate)
    ) {
        Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), elevation = CardDefaults.cardElevation(8.dp), modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                UpdateHeading(info)
                UpdateDetails(info)
                UpdateButtons(info.isForceUpdate, actions)
            }
        }
    }
}

@Composable
private fun UpdateHeading(info: UpdateInfo) {
    Text(genderedStringResource(if (info.isForceUpdate) GenderedStrings.updateRequired else GenderedStrings.updateAvailable), fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    Text(genderedStringResource(GenderedStrings.updateVersion, info.latestVersionName), fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(top = 12.dp))
}

@Composable
private fun UpdateDetails(info: UpdateInfo) {
    Text(info.updateMessage, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 12.dp))
    if (info.whatsNew.isNotBlank()) {
        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.padding(top = 16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
            Column(Modifier.padding(12.dp)) {
                Text(genderedStringResource(GenderedStrings.updateNotes), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                Text(info.whatsNew, color = MaterialTheme.colorScheme.onPrimaryContainer, fontSize = 14.sp, modifier = Modifier.padding(top = 8.dp))
            }
        }
    }
}

@Composable
private fun UpdateButtons(isRequired: Boolean, actions: UpdateActions) {
    Button(onClick = actions::onUpdateClicked, modifier = Modifier.fillMaxWidth().padding(top = 24.dp).height(50.dp), shape = RoundedCornerShape(16.dp)) {
        Text(genderedStringResource(GenderedStrings.updateDownload), fontSize = 16.sp)
    }
    if (!isRequired) TextButton(onClick = actions::dismissUpdate, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Text(genderedStringResource(GenderedStrings.laterAlternative))
    }
}
