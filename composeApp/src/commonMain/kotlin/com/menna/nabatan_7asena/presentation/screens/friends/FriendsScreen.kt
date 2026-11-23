package com.menna.nabatan_7asena.presentation.screens.friends

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChatBubbleOutline
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material.icons.rounded.PeopleOutline
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.menna.nabatan_7asena.data.repository.FriendsActionError
import com.menna.nabatan_7asena.data.repository.FriendProfile
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.presentation.common.components.ChunkyButton
import com.menna.nabatan_7asena.resources.painterResource
import com.menna.nabatan_7asena.ui.theme.SummerTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

internal val FriendsCream: Color @Composable get() = SummerTheme.colors.dialogBackground
internal val FriendsLavender: Color @Composable get() = SummerTheme.colors.wisdomStart
internal val FriendsPurple: Color @Composable get() = SummerTheme.colors.textDarkBrown
internal val FriendsGold: Color @Composable get() = SummerTheme.colors.goldWarm
internal val FriendsGoldEdge: Color @Composable get() = SummerTheme.colors.goldDeep
internal val FriendsLavenderEdge: Color @Composable get() = SummerTheme.colors.wisdomAccent

@Composable
private fun FriendCodeCard(state: FriendsState, onRetry: () -> Unit) {
    val profile = state.account?.profile
    val code = profile?.friendCode
    val copyCode = rememberFriendCodeCopier()
    var copied by remember(code) { mutableStateOf(false) }
    val shape = RoundedCornerShape(28.dp)
    Column(
        Modifier.fillMaxWidth()
            .background(Brush.linearGradient(listOf(FriendsLavender, FriendsCream, FriendsGold.copy(alpha = 0.55f))), shape)
            .border(1.dp, SummerTheme.colors.white, shape)
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
            FriendAvatarBadge(profile, FriendsGold, Modifier.size(60.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text("أهلًا بك", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text("شارك كود الصداقة مع أصحابك", style = MaterialTheme.typography.bodyMedium)
            }
        }
        Column(
            Modifier.fillMaxWidth().background(SummerTheme.colors.surfaceElevated.copy(alpha = 0.88f), RoundedCornerShape(18.dp)).padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("كود الصداقة", style = MaterialTheme.typography.labelLarge)
            Text(
                code ?: if (state.loading) "جارٍ تجهيز الكود" else "الكود غير متاح الآن",
                style = if (code == null) MaterialTheme.typography.bodyMedium else
                    MaterialTheme.typography.headlineSmall.copy(textDirection = TextDirection.Ltr),
                fontWeight = FontWeight.ExtraBold
            )
        }
        if (code != null) {
            ChunkyButton(
                onClick = { copyCode(code); copied = true },
                color = FriendsGold,
                edgeColor = FriendsGoldEdge,
                faceBrush = Brush.verticalGradient(listOf(FriendsCream, FriendsGold)),
                depth = 4.dp,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.ContentCopy, null, Modifier.size(18.dp), tint = FriendsPurple)
                    Text(if (copied) "تم نسخ الكود" else "نسخ الكود", color = FriendsPurple, fontWeight = FontWeight.Bold)
                }
            }
        } else if (state.failed) TextButton(onClick = onRetry) { Text("إعادة المحاولة", color = FriendsPurple) }
    }
}

@Composable
internal fun FriendAvatarBadge(profile: FriendProfile?, accent: Color, modifier: Modifier = Modifier) {
    Box(
        modifier.background(Brush.verticalGradient(listOf(SummerTheme.colors.surfaceElevated, accent)), CircleShape)
            .border(2.dp, SummerTheme.colors.white, CircleShape).padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (profile == null) {
            Icon(Icons.Rounded.PeopleOutline, null, Modifier.fillMaxSize().padding(4.dp), tint = FriendsPurple)
        } else {
            Image(
                painterResource(AvatarCatalog.resFor(profile.avatarGender, profile.avatarIndex)),
                contentDescription = null,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        }
    }
}

@Composable
internal expect fun rememberFriendCodeCopier(): (String) -> Unit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FriendsScaffold(title: String, onBack: () -> Unit, snackbar: SnackbarHostState, content: @Composable (PaddingValues) -> Unit) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = SummerTheme.colors.backgroundLight,
            contentColor = SummerTheme.colors.textPrimary,
            topBar = {
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.ExtraBold) },
                    navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Rounded.ArrowBack, "رجوع") } },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = FriendsPurple,
                        navigationIconContentColor = FriendsPurple
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbar) },
            content = { insets ->
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(FriendsLavender, FriendsCream)))) {
                    content(insets)
                }
            }
        )
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
            item { FriendCodeCard(state, model::retry) }
            item {
                ChunkyButton(
                    onClick = { showDialog = true },
                    enabled = state.account != null && !state.failed && !busy,
                    color = FriendsLavender,
                    edgeColor = FriendsLavenderEdge,
                    faceBrush = Brush.verticalGradient(listOf(SummerTheme.colors.surfaceElevated, FriendsLavender))
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.PersonAdd, null, tint = FriendsPurple)
                        Text("إضافة صديق", color = FriendsPurple, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (state.failed) item {
                Text("تعذر تحميل قائمة الأصحاب")
                TextButton(onClick = model::retry) { Text("إعادة المحاولة") }
            }
            if (state.loading) item { CircularProgressIndicator(color = FriendsPurple) }
            if (state.incoming.isNotEmpty()) item { FriendsSectionTitle("طلبات الصداقة") }
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
            if (state.outgoing.isNotEmpty()) item { FriendsSectionTitle("الطلبات المرسلة") }
            items(state.outgoing, key = { it.id }) { relationship ->
                FriendRow(relationship.recipientCode) { Text("بانتظار القبول") }
            }
            item { FriendsSectionTitle("قائمة الأصحاب") }
            if (!state.loading && !state.failed && state.accepted.isEmpty()) item {
                Surface(
                    Modifier.fillMaxWidth(),
                    color = FriendsCream,
                    shape = RoundedCornerShape(22.dp),
                    border = BorderStroke(1.dp, FriendsGold)
                ) {
                    Text("أضف أول صديق باستخدام كود الصداقة", Modifier.padding(16.dp), color = FriendsPurple)
                }
            }
            items(state.accepted, key = { it.id }) { relationship ->
                val account = state.account ?: return@items
                val friendProfile by produceState<FriendProfile?>(null, relationship.id, account.uid) {
                    value = try {
                        model.peer(relationship, account)
                    } catch (e: CancellationException) {
                        throw e
                    } catch (_: Exception) {
                        null
                    }
                }
                FriendRow(relationship.peerCode(account.uid), friendProfile, onClick = { onChat(relationship.id) }) {
                    Icon(Icons.Rounded.ChatBubbleOutline, "افتح المحادثة", tint = FriendsPurple)
                }
            }
        }
        if (showDialog) AddFriendDialog(model) { showDialog = false }
    }
}

@Composable
private fun FriendsSectionTitle(title: String) {
    Text(title, Modifier.padding(top = 6.dp), color = FriendsPurple,
        style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun FriendRow(
    code: String,
    profile: FriendProfile? = null,
    onClick: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit
) {
    val shape = RoundedCornerShape(22.dp)
    val stroke = BorderStroke(1.dp, FriendsGold.copy(alpha = 0.75f))
    val content: @Composable () -> Unit = {
        Column(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FriendAvatarBadge(profile, if (onClick == null) FriendsLavender else FriendsGold, Modifier.size(46.dp))
                Text(code, Modifier.weight(1f), style = MaterialTheme.typography.titleMedium.copy(textDirection = TextDirection.Ltr),
                    fontWeight = FontWeight.Bold)
                if (onClick != null) actions()
            }
            if (onClick == null) {
                Row(
                    Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                    content = actions
                )
            }
        }
    }
    if (onClick == null) Surface(shape = shape, color = FriendsCream, border = stroke, content = content)
    else Surface(onClick, shape = shape, color = SummerTheme.colors.cardBackground, border = stroke, shadowElevation = 2.dp, content = content)
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
