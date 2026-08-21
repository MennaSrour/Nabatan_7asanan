package com.menna.nabata_7asena.presentation.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.menna.nabata_7asena.ui.theme.SummerTheme
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
                        shape = SummerTheme.Shapes.MediumRounded,
                        colors = CardDefaults.cardColors(
                            containerColor = SummerTheme.Colors.BackgroundSunny
                        ),
                        border = BorderStroke(2.dp, SummerTheme.Colors.PrimaryGold)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("☀️", fontSize = 20.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = data.visuals.message,
                                color = Color(0xFF37474F),
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
                            Text("⚙️", fontSize = 24.sp)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "إعدادات رحلتي",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF37474F)
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF37474F)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = SummerTheme.Colors.BackgroundSunny
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize()) {
                SummerSettingsBackground()

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    SummerSectionTitle("الحساب 👤")
                    SummerSettingsItem(
                        icon = Icons.Rounded.Edit,
                        title = "تعديل الاسم",
                        subtitle = "غير اسمك اللي بيظهر",
                        iconColor = SummerTheme.Colors.PrimarySummerBlue,
                        onClick = { showEditNameDialog = true }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SummerSectionTitle("تنبيهات العبادات 🔔")

                    SummerSettingsItem(
                        icon = Icons.Rounded.Notifications,
                        title = "مواقيت الصلاة",
                        subtitle = "تنبيه عند كل أذان",
                        iconColor = SummerTheme.Colors.PrimaryTeal,
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

                    SummerSettingsItem(
                        icon = Icons.Rounded.Notifications,
                        title = "الأذكار",
                        subtitle = "تذكير بورد الذكر اليومي",
                        iconColor = SummerTheme.Colors.PrimaryPink,
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

                    SummerSettingsItem(
                        icon = Icons.Rounded.Notifications,
                        title = "ورد القرآن",
                        subtitle = "تذكير بقراءة ورد القرآن",
                        iconColor = SummerTheme.Colors.TaskCompletedGreen,
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

                    SummerSectionTitle("التحكم ⚙️")
                    SummerSettingsItem(
                        icon = Icons.Rounded.DeleteForever,
                        title = "بدء رحلة جديدة",
                        subtitle = "مسح كل النجوم والبدء من الصفر",
                        iconColor = SummerTheme.Colors.FlowerCoral,
                        onClick = { showResetDialog = true }
                    )
                }
            }
        }

        if (showEditNameDialog) {
            var tempName by remember { mutableStateOf("") }
            AlertDialog(
                onDismissRequest = { showEditNameDialog = false },
                containerColor = SummerTheme.Colors.BackgroundSunny,
                shape = SummerTheme.Shapes.ExtraRounded,
                icon = { Text("✏️", fontSize = 48.sp) },
                title = {
                    Text(
                        "اسمك الجديد يا بطل؟ ☀️",
                        color = Color(0xFF37474F),
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("الاسم الجديد 🌟") },
                        shape = SummerTheme.Shapes.MediumRounded,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SummerTheme.Colors.PrimaryGold,
                            unfocusedBorderColor = SummerTheme.Colors.PrimaryGold.copy(alpha = 0.5f)
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
                            containerColor = SummerTheme.Colors.PrimaryGold
                        ),
                        shape = SummerTheme.Shapes.SmallRounded
                    ) {
                        Text("حفظ ☀️", fontWeight = FontWeight.Bold, color = Color.White)
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
                shape = SummerTheme.Shapes.ExtraRounded,
                icon = { Text("⚠️", fontSize = 48.sp) },
                title = {
                    Text(
                        "متأكد يا بطل؟",
                        color = SummerTheme.Colors.FlowerCoral,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        "كل النجوم والشجرة هيرجعوا للصفر كأنك لسه بادئ التطبيق دلوقتي حالاً. رحلتك الجميلة هتبدأ من جديد! 🌱",
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
                            containerColor = SummerTheme.Colors.FlowerCoral.copy(alpha = 0.2f),
                            contentColor = SummerTheme.Colors.FlowerCoral
                        ),
                        shape = SummerTheme.Shapes.SmallRounded
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
fun SummerSettingsBackground() {
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
                    Color(0xFFFFFDE7),
                    Color(0xFFFFF9E6),
                    Color(0xFFF9FBE7).copy(alpha = 0.3f)
                )
            )
        )
        starPositions.forEach { pos ->
            drawCircle(
                color = SummerTheme.Colors.PrimaryGold.copy(alpha = 0.4f),
                radius = 3.dp.toPx(),
                center = Offset(size.width * pos.x, size.height * pos.y)
            )
        }
    }
}

@Composable
fun SummerSectionTitle(text: String) {
    Card(
        shape = SummerTheme.Shapes.SmallRounded,
        colors = CardDefaults.cardColors(
            containerColor = SummerTheme.Colors.PrimaryGold.copy(alpha = 0.15f)
        ),
        modifier = Modifier.padding(bottom = 12.dp)
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFFE65100),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}

@Composable
fun SummerSettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconColor: Color = SummerTheme.Colors.PrimarySummerBlue,
    hasSwitch: Boolean = false,
    isSwitchChecked: Boolean = false,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onClick: () -> Unit
) {
    Card(
        shape = SummerTheme.Shapes.MediumRounded,
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
                        checkedTrackColor = SummerTheme.Colors.TaskCompletedGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }
        }
    }
}