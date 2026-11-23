@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.menna.nabatan_7asena.presentation.screens.friends

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.data.repository.*
import com.menna.nabatan_7asena.domain.entity.PresetCategory
import com.menna.nabatan_7asena.domain.entity.PresetMessage
import com.menna.nabatan_7asena.presentation.common.components.ChunkyButton
import com.menna.nabatan_7asena.core.util.arabicIndic
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import com.menna.nabatan_7asena.ui.theme.SummerTheme

@Composable
internal fun PresetChatScreen(chatId: String, onBack: () -> Unit) {
    val model = LocalFriendsViewModel.current ?: return
    val state by model.state.collectAsStateWithLifecycle()
    val friendship = state.accepted.firstOrNull { it.id == chatId }
    val account = state.account
    val snackbar = remember { SnackbarHostState() }
    FriendsScaffold("محادثة مع صديق", onBack, snackbar) { insets ->
        Box(Modifier.fillMaxSize().padding(insets)) {
            if (friendship != null && account != null) {
                PresetChatContent(model, account, friendship, snackbar)
            } else if (state.loading) {
                CircularProgressIndicator(Modifier.align(Alignment.Center), color = FriendsPurple)
            } else {
                Text("المحادثة غير متاحة", Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun PresetChatContent(
    model: FriendsViewModel,
    account: FriendsAccount,
    friendship: Friendship,
    snackbar: SnackbarHostState
) {
    var messages by remember(friendship.id) { mutableStateOf<List<ChatMessage>?>(null) }
    var peer by remember(friendship.id) { mutableStateOf<FriendProfile?>(null) }
    var failed by remember { mutableStateOf(false) }
    var retry by remember { mutableIntStateOf(0) }
    var picker by rememberSaveable { mutableStateOf(false) }
    var sending by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val list = rememberLazyListState()
    LaunchedEffect(friendship.id, account.uid) {
        try {
            peer = model.peer(friendship, account)
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            // A missing avatar uses the default avatar.
        }
    }
    LaunchedEffect(friendship.id, retry) {
        failed = false
        try {
            model.messages(friendship).collect { messages = it }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            failed = true
        }
    }
    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
    val latestMessage = messages?.lastOrNull().takeUnless { failed }
    LaunchedEffect(account.uid, friendship.id, latestMessage, lifecycleState) {
        if (latestMessage != null && lifecycleState == Lifecycle.State.RESUMED) {
            model.markRead(account, friendship, latestMessage)
        }
    }
    LaunchedEffect(messages?.lastOrNull()?.id) {
        messages?.takeIf { it.isNotEmpty() }?.let { list.scrollToItem(it.lastIndex) }
    }
    Column(Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(Modifier.weight(1f).fillMaxWidth()) {
            val history = messages
            when {
                failed -> Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("تعذر تحميل الرسائل")
                    TextButton(onClick = { retry++ }) { Text("إعادة المحاولة", color = FriendsPurple) }
                }
                history == null -> CircularProgressIndicator(Modifier.align(Alignment.Center), color = FriendsPurple)
                history.isEmpty() -> Surface(
                    Modifier.align(Alignment.Center),
                    color = FriendsCream,
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, FriendsGold)
                ) {
                    Column(Modifier.padding(22.dp), horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(Icons.Rounded.ChatBubbleOutline, null, tint = FriendsPurple)
                        Text("ابدأ بتحية لطيفة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
                else -> LazyColumn(Modifier.fillMaxSize(), state = list, verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(history, key = { it.id }) { message ->
                        val sent = message.senderId == account.uid
                        MessageBubble(message, sent, if (sent) account.profile else peer)
                    }
                }
            }
        }
        ChunkyButton(
            onClick = { picker = true },
            modifier = Modifier.fillMaxWidth(),
            enabled = !sending && !failed && messages != null,
            color = FriendsGold,
            edgeColor = FriendsGoldEdge,
            faceBrush = Brush.verticalGradient(listOf(FriendsCream, FriendsGold))
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.ChatBubbleOutline, null, tint = FriendsPurple)
                Text("اختر رسالة", color = FriendsPurple, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
        }
    }
    if (picker) PresetPicker(onDismiss = { picker = false }) { preset ->
        picker = false
        sending = true
        scope.launch {
            try {
                model.send(friendship, preset)
            } catch (e: CancellationException) {
                throw e
            } catch (e: FriendsActionError) {
                snackbar.showSnackbar(e.message.orEmpty())
            } catch (_: Exception) {
                snackbar.showSnackbar("تعذر إرسال الرسالة، حاول مرة أخرى")
            } finally {
                sending = false
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage, sent: Boolean, profile: FriendProfile?) {
    val dateTime = Instant.fromEpochSeconds(message.time.seconds, message.time.nanoseconds.toLong())
        .toLocalDateTime(TimeZone.currentSystemDefault())
    val date = "${arabicIndic(dateTime.day)}/${arabicIndic(dateTime.month.number)}"
    val time = "${arabicIndic(dateTime.hour).padStart(2, '٠')}:${arabicIndic(dateTime.minute).padStart(2, '٠')}"
    val accent = if (sent) FriendsLavender else FriendsGold
    val bubbleShape = RoundedCornerShape(
        topStart = 20.dp, topEnd = 20.dp,
        bottomStart = if (sent) 5.dp else 20.dp,
        bottomEnd = if (sent) 20.dp else 5.dp
    )
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, if (sent) Alignment.Start else Alignment.End),
        verticalAlignment = Alignment.Bottom
    ) {
        if (sent) FriendAvatarBadge(profile, accent, Modifier.size(40.dp))
        Surface(
            Modifier.weight(1f, fill = false).widthIn(max = 310.dp),
            shape = bubbleShape,
            color = if (sent) FriendsLavender else FriendsCream,
            contentColor = FriendsPurple,
            border = BorderStroke(1.dp, if (sent) SummerTheme.colors.white else accent),
            shadowElevation = 1.dp
        ) {
            Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(message.preset.text, style = MaterialTheme.typography.bodyLarge)
                Text("$date  $time", style = MaterialTheme.typography.labelSmall, color = FriendsPurple.copy(alpha = 0.85f))
            }
        }
        if (!sent) FriendAvatarBadge(profile, accent, Modifier.size(40.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PresetPicker(onDismiss: () -> Unit, onSend: (PresetMessage) -> Unit) {
    var category by rememberSaveable { mutableStateOf(PresetCategory.GREETING) }
    ModalBottomSheet(onDismissRequest = onDismiss, containerColor = FriendsCream, contentColor = FriendsPurple) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp).navigationBarsPadding()) {
            Text("اختر رسالة", style = MaterialTheme.typography.titleLarge)
            Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                PresetCategory.entries.forEach { option ->
                    FilterChip(selected = category == option, onClick = { category = option }, label = { Text(option.title) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = FriendsLavender))
                }
            }
            LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
                items(PresetMessage.entries.filter { it.category == category }, key = { it.id }) { preset ->
                    OutlinedButton(onClick = { onSend(preset) }, modifier = Modifier.fillMaxWidth()) {
                        Text(preset.text, color = FriendsPurple)
                    }
                }
                if (category == PresetCategory.GARDEN) item { Text("قطرة ماء واحدة كل يوم لكل صديق") }
            }
        }
    }
}
