package com.menna.nabata_7asena.presentation.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabata_7asena.ui.theme.RamadanTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val stats by viewModel.userStats.collectAsState()

    var showResetDialog by remember { mutableStateOf(false) }
    var showEditNameDialog by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SettingsEvent.ShowError -> coroutineScope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
                is SettingsEvent.ShowInfo -> coroutineScope.launch {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState) { data ->
                    Card(
                        shape = RamadanTheme.Shapes.MediumRounded,
                        colors = CardDefaults.cardColors(
                            containerColor = RamadanTheme.Colors.BackgroundMoon
                        ),
                        border = BorderStroke(2.dp, RamadanTheme.Colors.PrimaryGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("🌙", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = data.visuals.message,
                                color = RamadanTheme.Colors.PrimaryPurple,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🌙", fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "الإعدادات الرمضانية",
                                fontWeight = FontWeight.Bold,
                                color = RamadanTheme.Colors.PrimaryPurple
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = RamadanTheme.Colors.PrimaryPurple
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = RamadanTheme.Colors.BackgroundMoon
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                RamadanSettingsBackground()

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    RamadanSectionTitle("الحساب 👤")
                    RamadanSettingsItem(
                        icon = Icons.Rounded.Edit,
                        title = "تعديل الاسم",
                        subtitle = "غير اسمك اللي بيظهر",
                        iconColor = RamadanTheme.Colors.PrimaryPurple,
                        onClick = { showEditNameDialog = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    RamadanSectionTitle("تنبيهات العبادات 🔔")

                    RamadanSettingsItem(
                        icon = Icons.Rounded.Notifications,
                        title = "مواقيت الصلاة",
                        subtitle = "تنبيه عند كل أذان",
                        iconColor = RamadanTheme.Colors.PrimaryTeal,
                        hasSwitch = true,
                        isSwitchChecked = stats?.prayerNotifications ?: true,
                        onSwitchChange = { isChecked ->
                            viewModel.toggleNotification("prayer", isChecked)
                        },
                        onClick = {
                            val currentStatus = stats?.prayerNotifications ?: true
                            viewModel.toggleNotification("prayer", !currentStatus)
                        }
                    )

                    RamadanSettingsItem(
                        icon = Icons.Rounded.Notifications,
                        title = "الأذكار",
                        subtitle = "تذكير بورد الذكر اليومي",
                        iconColor = RamadanTheme.Colors.PrimaryPink,
                        hasSwitch = true,
                        isSwitchChecked = stats?.azkarNotifications ?: true,
                        onSwitchChange = { isChecked ->
                            viewModel.toggleNotification("azkar", isChecked)
                        },
                        onClick = {
                            val currentStatus = stats?.prayerNotifications ?: true
                            viewModel.toggleNotification("azkar", !currentStatus)
                        }
                    )

                    RamadanSettingsItem(
                        icon = Icons.Rounded.Notifications,
                        title = "ورد القرآن",
                        subtitle = "تذكير بقراءة ورد القرآن",
                        iconColor = RamadanTheme.Colors.TaskCompletedGreen,
                        hasSwitch = true,
                        isSwitchChecked = stats?.quranNotifications ?: true,
                        onSwitchChange = { isChecked ->
                            viewModel.toggleNotification("quran", isChecked)
                        },
                        onClick = {
                            val currentStatus = stats?.prayerNotifications ?: true
                            viewModel.toggleNotification("quran", !currentStatus)
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    RamadanSectionTitle("التحكم ⚙️")
                    RamadanSettingsItem(
                        icon = Icons.Rounded.DeleteForever,
                        title = "بدء رحلة جديدة",
                        subtitle = "مسح كل النجوم والبدء من الصفر",
                        iconColor = RamadanTheme.Colors.LanternRed,
                        onClick = { showResetDialog = true }
                    )
                }
            }
        }

        if (showEditNameDialog) {
            var tempName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                containerColor = RamadanTheme.Colors.BackgroundMoon,
                shape = RamadanTheme.Shapes.ExtraRounded,
                icon = { Text("✏️", fontSize = 48.sp) },
                title = {
                    Text(
                        "اسمك الجديد يا بطل؟ 🌙",
                        color = RamadanTheme.Colors.PrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("الاسم الجديد 🌟") },
                        shape = RamadanTheme.Shapes.MediumRounded,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = RamadanTheme.Colors.PrimaryGold,
                            unfocusedBorderColor = RamadanTheme.Colors.PrimaryGold.copy(alpha = 0.5f)
                        )
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempName.isNotBlank()) {
                                viewModel.updateUserName(tempName)
                                showEditNameDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RamadanTheme.Colors.PrimaryGold
                        ),
                        shape = RamadanTheme.Shapes.SmallRounded
                    ) {
                        Text("حفظ 🌙", fontWeight = FontWeight.Bold, color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showEditNameDialog = false }) {
                        Text("إلغاء", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        if (showResetDialog) {
            AlertDialog(
                onDismissRequest = { showResetDialog = false },
                containerColor = Color(0xFFFFF5F5),
                shape = RamadanTheme.Shapes.ExtraRounded,
                icon = { Text("⚠️", fontSize = 48.sp) },
                title = {
                    Text(
                        "متأكد يا بطل؟",
                        color = RamadanTheme.Colors.LanternRed,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "كل النجوم والشجرة هيرجعوا للصفر كأنك لسه بادئ التطبيق دلوقتي حالاً. الرحلة الرمضانية هتبدأ من جديد! 🌱",
                        fontSize = 15.sp,
                        color = Color(0xFF455A64),
                        lineHeight = 22.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.resetProgress()
                            showResetDialog = false
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RamadanTheme.Colors.LanternRed.copy(alpha = 0.2f),
                            contentColor = RamadanTheme.Colors.LanternRed
                        ),
                        shape = RamadanTheme.Shapes.SmallRounded
                    ) {
                        Text("نعم، امسح وابدأ من جديد", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetDialog = false }) {
                        Text("إلغاء", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    }
}

@Composable
fun RamadanSettingsBackground() {
    val starPositions = remember {
        listOf(
            Offset(0.10f, 0.15f),
            Offset(0.90f, 0.20f),
            Offset(0.30f, 0.50f),
            Offset(0.80f, 0.60f),
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    RamadanTheme.Colors.BackgroundMoon,
                    Color(0xFFFFF9E6),
                    Color(0xFFFFECB3).copy(alpha = 0.3f)
                )
            )
        )
        starPositions.forEach { pos ->
            drawCircle(
                color = RamadanTheme.Colors.StarGold.copy(alpha = 0.4f),
                radius = 3.dp.toPx(),
                center = Offset(size.width * pos.x, size.height * pos.y)
            )
        }
    }
}

@Composable
fun RamadanSectionTitle(text: String) {
    Card(
        shape = RamadanTheme.Shapes.SmallRounded,
        colors = CardDefaults.cardColors(
            containerColor = RamadanTheme.Colors.PrimaryGold.copy(alpha = 0.1f)
        ),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RamadanTheme.Colors.PrimaryPurple,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun RamadanSettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = RamadanTheme.Colors.PrimaryPurple,
    hasSwitch: Boolean = false,
    isSwitchChecked: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        shape = RamadanTheme.Shapes.MediumRounded,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(2.dp, iconColor.copy(alpha = 0.2f)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable {
                if (hasSwitch) onSwitchChange?.invoke(!isSwitchChecked) else onClick()
            }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                iconColor.copy(alpha = 0.2f),
                                iconColor.copy(alpha = 0.1f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    null,
                    tint = iconColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF2E3E5C)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    subtitle,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    lineHeight = 18.sp
                )
            }

            if (hasSwitch) {
                Switch(
                    checked = isSwitchChecked,
                    onCheckedChange = onSwitchChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = RamadanTheme.Colors.TaskCompletedGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }
        }
    }
}