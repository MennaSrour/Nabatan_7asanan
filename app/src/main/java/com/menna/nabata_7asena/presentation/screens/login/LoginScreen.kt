package com.menna.nabata_7asena.presentation.screens.login

import android.media.MediaPlayer
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.menna.nabata_7asena.R
import com.menna.nabata_7asena.domain.entity.User
import com.menna.nabata_7asena.ui.theme.RamadanTheme

@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is LoginEvent.NavigateToHome -> onNavigateToHome()
                is LoginEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    DisposableEffect(Unit) {
        val mediaPlayer = MediaPlayer.create(context, R.raw.name)
        mediaPlayer.start()
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Card(
                    shape = RamadanTheme.Shapes.MediumRounded,
                    colors = CardDefaults.cardColors(
                        containerColor = RamadanTheme.Colors.BackgroundMoon
                    ),
                    border = BorderStroke(2.dp, RamadanTheme.Colors.PrimaryGold),
                    elevation = CardDefaults.cardElevation(8.dp)
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            RamadanLoginBackground()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🌙", fontSize = 32.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "أهلاً بيك يا بطل!",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = RamadanTheme.Colors.PrimaryPurple
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("⭐", fontSize = 32.sp)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "عرّفنا بنفسك عشان نبدأ الرحلة الرمضانية",
                        fontSize = 16.sp,
                        color = RamadanTheme.Colors.PrimaryPurple.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RamadanGenderCard(
                        gender = User.Gender.BOY,
                        isSelected = state.selectedGender == User.Gender.BOY,
                        onClick = { viewModel.onGenderSelected(User.Gender.BOY) }
                    )
                    RamadanGenderCard(
                        gender = User.Gender.GIRL,
                        isSelected = state.selectedGender == User.Gender.GIRL,
                        onClick = { viewModel.onGenderSelected(User.Gender.GIRL) }
                    )
                }

                Card(
                    shape = RamadanTheme.Shapes.MediumRounded,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(6.dp),
                    border = BorderStroke(
                        2.dp,
                        if (state.name.isNotBlank())
                            RamadanTheme.Colors.PrimaryGold
                        else
                            Color.LightGray
                    )
                ) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("اكتب اسمك هنا 🌟") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        ),
                        shape = RamadanTheme.Shapes.MediumRounded
                    )
                }

                val buttonPulse = RamadanTheme.rememberPulseAnimation()

                Button(
                    onClick = { viewModel.onStartClicked() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .scale(if (!state.isLoading) buttonPulse else 1f),
                    shape = RamadanTheme.Shapes.SmallRounded,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = RamadanTheme.Colors.PrimaryGold
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 8.dp,
                        pressedElevation = 12.dp
                    ),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "ابدأ الرحلة الرمضانية",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.width(8.dp))
                            Text("🌙", fontSize = 20.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RamadanLoginBackground() {
    val starAlpha = RamadanTheme.rememberStarTwinkleAnimation()

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFFF9E6),
                    Color(0xFFFFECB3),
                    Color(0xFFFFE082)
                )
            )
        )

        val stars = listOf(
            androidx.compose.ui.geometry.Offset(size.width * 0.15f, size.height * 0.20f),
            androidx.compose.ui.geometry.Offset(size.width * 0.85f, size.height * 0.15f),
            androidx.compose.ui.geometry.Offset(size.width * 0.50f, size.height * 0.10f),
            androidx.compose.ui.geometry.Offset(size.width * 0.30f, size.height * 0.25f),
            androidx.compose.ui.geometry.Offset(size.width * 0.70f, size.height * 0.30f),
        )

        stars.forEach { offset ->
            drawCircle(
                color = RamadanTheme.Colors.StarGold,
                radius = 4.dp.toPx(),
                center = offset,
                alpha = starAlpha
            )
        }
    }
}

@Composable
fun RamadanGenderCard(
    gender: User.Gender,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        if (isSelected) 1.1f else 1f,
        label = "scale"
    )

    val label = if (gender == User.Gender.BOY) "ولد" else "بنت"

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .scale(scale)
            .scale(pulseScale)
            .clickable { onClick() }
    ) {
        Card(
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    RamadanTheme.Colors.PrimaryGold.copy(alpha = 0.2f)
                else
                    Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = if (isSelected) 8.dp else 4.dp
            ),
            border = BorderStroke(
                width = if (isSelected) 4.dp else 2.dp,
                color = if (isSelected)
                    RamadanTheme.Colors.PrimaryGold
                else
                    Color.LightGray
            ),
            modifier = Modifier.size(110.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                if (isSelected) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = RamadanTheme.Colors.StarGold.copy(alpha = 0.3f),
                            radius = size.width * 0.5f
                        )
                    }
                }

                Image(
                    painter = androidx.compose.ui.res.painterResource(
                        id = if (gender == User.Gender.BOY)
                            R.drawable.boy_avatar
                        else
                            R.drawable.girl_avatar
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )

                if (isSelected) {
                    Text(
                        "⭐",
                        fontSize = 24.sp,
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = (-8).dp, y = 8.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isSelected)
                    RamadanTheme.Colors.PrimaryGold
                else
                    Color.White
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    label,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isSelected) Color.White else Color.Gray
                )
                if (isSelected) {
                    Spacer(Modifier.width(4.dp))
                    Text("🌙", fontSize = 14.sp)
                }
            }
        }
    }
}