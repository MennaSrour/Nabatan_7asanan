package com.menna.nabatan_7asena.presentation.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.data.repository.FriendsActionError
import com.menna.nabatan_7asena.data.repository.Friendship
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

internal val FriendsCream = Color(0xFFFFF8EC)
internal val FriendsLavender = Color(0xFFEDE3FB)
internal val FriendsPurple = Color(0xFF533681)
internal val FriendsGold = Color(0xFFF6D786)

@Composable
internal fun FriendCodeCard() {
    val model = LocalFriendsViewModel.current ?: return
    val state by model.state.collectAsStateWithLifecycle()
    val code = state.account?.profile?.friendCode
    val copyCode = rememberFriendCodeCopier()
    var copied by remember(code) { mutableStateOf(false) }
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = FriendsLavender,
            contentColor = FriendsPurple,
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("كود الصداقة", style = MaterialTheme.typography.titleMedium)
                    if (code != null) Text(code, style = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Ltr))
                    else Text(if (state.loading) "جارٍ تجهيز الكود" else "الكود غير متاح الآن")
                }
                if (code != null) TextButton(onClick = {
                    copyCode(code)
                    copied = true
                }) { Text(if (copied) "تم نسخ الكود" else "نسخ الكود", color = FriendsPurple) }
                else if (state.failed) TextButton(onClick = model::retry) { Text("إعادة المحاولة", color = FriendsPurple) }
            }
        }
    }
}

@Composable
internal expect fun rememberFriendCodeCopier(): (String) -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FriendsScaffold(title: String, onBack: () -> Unit, snackbar: SnackbarHostState, content: @Composable (PaddingValues) -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = FriendsPurple,
                onPrimary = Color.White,
                primaryContainer = FriendsLavender,
                onPrimaryContainer = FriendsPurple,
                surface = FriendsCream,
                onSurface = FriendsPurple,
                onSurfaceVariant = FriendsPurple,
                outline = FriendsPurple
            )
        ) {
            Scaffold(
                containerColor = FriendsCream,
                contentColor = FriendsPurple,
                topBar = {
                    TopAppBar(
                        title = { Text(title) },
                        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "رجوع") } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = FriendsLavender,
                            titleContentColor = FriendsPurple, navigationIconContentColor = FriendsPurple)
                    )
                },
                snackbarHost = { SnackbarHost(snackbar) },
                content = content
            )
        }
    }
}

@Composable
internal fun FriendsScreen(onBack: () -> Unit, onChat: (String) -> Unit) {
    val model = LocalFriendsViewModel.current ?: return
    val state by model.state.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showDialog by rememberSaveable { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    FriendsScaffold("أصحابي", onBack, snackbar) { insets ->
        LazyColumn(
            Modifier.fillMaxSize().padding(insets),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { FriendCodeCard() }
            item {
                Button(
                    onClick = { showDialog = true },
                    enabled = state.account != null && !state.failed && !busy,
                    colors = ButtonDefaults.buttonColors(containerColor = FriendsPurple)
                ) { Text("إضافة صديق") }
            }
            if (state.failed) item {
                Text("تعذر تحميل قائمة الأصحاب")
                TextButton(onClick = model::retry) { Text("إعادة المحاولة") }
            }
            if (state.loading) item { CircularProgressIndicator(color = FriendsPurple) }
            if (state.incoming.isNotEmpty()) item { Text("طلبات الصداقة", style = MaterialTheme.typography.titleLarge) }
            items(state.incoming, key = { it.id }) { relationship ->
                FriendRow(relationship.senderCode) {
                    listOf(true, false).forEach { accept ->
                        TextButton(enabled = !busy, onClick = {
                            busy = true
                            scope.launch {
                                try {
                                    model.respond(relationship, accept)
                                } catch (e: CancellationException) {
                                    throw e
                                } catch (_: Exception) {
                                    snackbar.showSnackbar("تعذر تحديث طلب الصداقة")
                                } finally {
                                    busy = false
                                }
                            }
                        }) { Text(if (accept) "قبول" else "رفض", color = FriendsPurple) }
                    }
                }
            }
            if (state.outgoing.isNotEmpty()) item { Text("الطلبات المرسلة", style = MaterialTheme.typography.titleLarge) }
            items(state.outgoing, key = { it.id }) { relationship ->
                FriendRow(relationship.recipientCode) { Text("بانتظار القبول") }
            }
            item { Text("قائمة الأصحاب", style = MaterialTheme.typography.titleLarge) }
            if (!state.loading && !state.failed && state.accepted.isEmpty()) item {
                Text("أضف أول صديق باستخدام كود الصداقة")
            }
            items(state.accepted, key = { it.id }) { relationship ->
                FriendRow(relationship.peerCode(requireNotNull(state.account).uid), onClick = { onChat(relationship.id) }) {
                    Icon(Icons.Rounded.ChatBubbleOutline, "افتح المحادثة", tint = FriendsPurple)
                }
            }
        }
        if (showDialog) AddFriendDialog(model) { showDialog = false }
    }
}

@Composable
private fun FriendRow(code: String, onClick: (() -> Unit)? = null, actions: @Composable RowScope.() -> Unit) {
    val content: @Composable () -> Unit = {
        Row(Modifier.fillMaxWidth().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(code, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium.copy(textDirection = TextDirection.Ltr))
            actions()
        }
    }
    val shape = RoundedCornerShape(16.dp)
    if (onClick == null) Surface(shape = shape, color = Color.White, contentColor = FriendsPurple, content = content)
    else Surface(onClick, shape = shape, color = Color.White, contentColor = FriendsPurple, content = content)
}

@Composable
private fun AddFriendDialog(model: FriendsViewModel, onDismiss: () -> Unit) {
    var code by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    AlertDialog(
        onDismissRequest = { if (!busy) onDismiss() },
        containerColor = FriendsCream,
        title = { Text("إضافة صديق", color = FriendsPurple) },
        text = {
            OutlinedTextField(
                value = code,
                onValueChange = { if (it.length <= 20) { code = it; error = null } },
                label = { Text("كود الصداقة") },
                singleLine = true,
                enabled = !busy,
                isError = error != null,
                supportingText = { error?.let { Text(it) } },
                textStyle = MaterialTheme.typography.bodyLarge.copy(textDirection = TextDirection.Ltr)
            )
        },
        confirmButton = {
            TextButton(enabled = !busy && code.isNotBlank(), onClick = {
                busy = true
                scope.launch {
                    try {
                        model.request(code)
                        onDismiss()
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: FriendsActionError) {
                        error = e.message
                    } catch (_: Exception) {
                        error = "تعذر إرسال الطلب، حاول مرة أخرى"
                    } finally {
                        busy = false
                    }
                }
            }) { Text("إرسال الطلب", color = FriendsPurple) }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !busy) { Text("إلغاء", color = FriendsPurple) } }
    )
}
