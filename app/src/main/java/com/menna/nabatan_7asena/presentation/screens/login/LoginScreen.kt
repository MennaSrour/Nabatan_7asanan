package com.menna.nabatan_7asena.presentation.screens.login

import android.media.MediaPlayer
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.presentation.common.AvatarCatalog
import com.menna.nabatan_7asena.ui.theme.LocalIsNight
import com.menna.nabatan_7asena.ui.theme.SummerTheme

private val ShapeMedium = RoundedCornerShape(20.dp)
private val ShapeSmall = RoundedCornerShape(16.dp)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onNavigateToHome: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var showAvatarSheet by remember { mutableStateOf(false) }

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

    val currentAvatarList = AvatarCatalog.listFor(state.selectedGender)
    val currentAvatarIndex = if (state.selectedGender == User.Gender.BOY)
        state.selectedBoyAvatarIndex else state.selectedGirlAvatarIndex
    val currentAvatarRes = currentAvatarList.getOrElse(currentAvatarIndex) { currentAvatarList.first() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Card(
                    shape = ShapeMedium,
                    colors = CardDefaults.cardColors(
                        containerColor = SummerTheme.colors.cardBackground
                    ),
                    border = BorderStroke(2.dp, SummerTheme.colors.goldWarm),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = data.visuals.message,
                            color = SummerTheme.colors.textPrimary,
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
            NabatanLoginBackground()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "أهلاً بيك يا بطل!",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SummerTheme.colors.wisdomAccent
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "عرّفنا بنفسك عشان نبدأ رحلتنا مع بعض",
                        fontSize = 16.sp,
                        color = SummerTheme.colors.textBrown,
                        fontWeight = FontWeight.Bold
                    )
                }

                AvatarPreview(
                    avatarRes = currentAvatarRes,
                    onEditClick = { showAvatarSheet = true }
                )

                Text(
                    "اضغط عشان تختار شخصيتك",
                    fontSize = 13.sp,
                    color = SummerTheme.colors.wisdomAccent,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showAvatarSheet = true }
                )

                GenderToggleRow(
                    selectedGender = state.selectedGender,
                    onGenderSelected = { viewModel.onGenderSelected(it) }
                )

                Card(
                    shape = ShapeMedium,
                    colors = CardDefaults.cardColors(containerColor = SummerTheme.colors.cardBackground),
                    elevation = CardDefaults.cardElevation(6.dp),
                    border = BorderStroke(
                        2.dp,
                        if (state.name.isNotBlank()) SummerTheme.colors.goldWarm else SummerTheme.colors.chipUnselected
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.name,
                        onValueChange = { viewModel.onNameChange(it) },
                        label = { Text("اكتب اسمك هنا") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            textDirection = TextDirection.ContentOrRtl
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor = SummerTheme.colors.cardBackground,
                            unfocusedContainerColor = SummerTheme.colors.cardBackground,
                           focusedTextColor = SummerTheme.colors.textPrimary,
                            unfocusedTextColor = SummerTheme.colors.textPrimary,
                            cursorColor = SummerTheme.colors.wisdomAccent,
                            focusedLabelColor = SummerTheme.colors.wisdomAccent,
                            unfocusedLabelColor = SummerTheme.colors.textBrown
                        ),
                        shape = ShapeMedium
                    )
                }

                StartJourneyButton(
                    isLoading = state.isLoading,
                    onClick = { viewModel.onStartClicked() }
                )
            }
        }
    }

    if (showAvatarSheet) {
        AvatarPickerSheet(
            gender = state.selectedGender,
            avatars = currentAvatarList,
            selectedIndex = currentAvatarIndex,
            onAvatarPicked = { index ->
                viewModel.onAvatarSelected(index)
                showAvatarSheet = false
            },
            onDismiss = { showAvatarSheet = false }
        )
    }
}
@Composable
private fun AvatarPreview(avatarRes: Int, onEditClick: () -> Unit) {
    Box(contentAlignment = Alignment.BottomEnd) {
        Surface(
            shape = CircleShape,
            color = SummerTheme.colors.cardBackground,
            border = BorderStroke(3.dp, SummerTheme.colors.goldWarm),
            shadowElevation = 6.dp,
            modifier = Modifier
                .size(130.dp)
                .clickable { onEditClick() }
        ) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        Surface(
            shape = CircleShape,
            color = SummerTheme.colors.wisdomAccent,
            shadowElevation = 4.dp,
            modifier = Modifier
                .size(36.dp)
                .clickable { onEditClick() }
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "تغيير الشخصية",
                    tint = SummerTheme.colors.white,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
private fun GenderToggleRow(
    selectedGender: User.Gender,
    onGenderSelected: (User.Gender) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GenderPill(
            label = "ولد",
            isSelected = selectedGender == User.Gender.BOY,
            onClick = { onGenderSelected(User.Gender.BOY) }
        )
        GenderPill(
            label = "بنت",
            isSelected = selectedGender == User.Gender.GIRL,
            onClick = { onGenderSelected(User.Gender.GIRL) }
        )
    }
}

@Composable
private fun GenderPill(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (isSelected) SummerTheme.colors.secondaryAccent.copy(alpha = 0.16f) else SummerTheme.colors.cardBackground,
        border = BorderStroke(
            2.dp,
            if (isSelected) SummerTheme.colors.secondaryAccent else SummerTheme.colors.chipUnselected
        ),
        modifier = Modifier
            .height(44.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(horizontal = 22.dp)
        ) {
            Text(
                label,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = if (isSelected) SummerTheme.colors.secondaryAccent else SummerTheme.colors.textBrown
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AvatarPickerSheet(
    gender: User.Gender,
    avatars: List<Int>,
    selectedIndex: Int,
    onAvatarPicked: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SummerTheme.colors.cardBackground,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            Text(
                text = if (gender == User.Gender.BOY) "اختار شخصيتك يا بطل 👦" else "اختاري شخصيتك يا بطلة 👧",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = SummerTheme.colors.textDarkBrown,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(avatars.size) { index ->
                    AvatarThumbnail(
                        avatarRes = avatars[index],
                        isSelected = index == selectedIndex,
                        onClick = { onAvatarPicked(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun AvatarThumbnail(avatarRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.TopEnd) {
        Surface(
            onClick = onClick,
            shape = RoundedCornerShape(18.dp),
            color = if (isSelected) SummerTheme.colors.goldWarm.copy(alpha = 0.15f) else SummerTheme.colors.dayBackground,
            border = BorderStroke(
                if (isSelected) 3.dp else 1.dp,
                if (isSelected) SummerTheme.colors.goldWarm else SummerTheme.colors.chipUnselected
            ),
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        ) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }

        if (isSelected) {
            Surface(
                shape = CircleShape,
                color = SummerTheme.colors.successGreen,
                modifier = Modifier
                    .padding(6.dp)
                    .size(22.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = SummerTheme.colors.white,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun StartJourneyButton(isLoading: Boolean, onClick: () -> Unit) {
    val buttonPulse = rememberGentlePulse()

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = ShapeSmall,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 10.dp
        ),
        enabled = !isLoading
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(SummerTheme.colors.goldWarm, SummerTheme.colors.goldDeep)
                    ),
                    shape = ShapeSmall
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = SummerTheme.colors.white,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    "ابدأ الرحلة",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = SummerTheme.colors.overlayDark,
                    modifier = Modifier.scale(buttonPulse)
                )
            }
        }
    }
}

@Composable
fun NabatanLoginBackground() {
    val isNight = LocalIsNight.current
    val gradientStart = SummerTheme.colors.wisdomStart
    val gradientEnd = SummerTheme.colors.dayBackground

    Box(modifier = Modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(gradientStart, gradientEnd)
                )
            )
        }

        if (isNight) {
            NightStarsAnimation(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.6f)
            )
        } else {
            DaySparkleParticles(modifier = Modifier.fillMaxSize())
        }
    }
}


@Composable
private fun DaySparkleParticles(modifier: Modifier = Modifier) {
    val sparkleAlphas = SummerTheme.rememberParticleAlphas(count = 5)
    val sparkleColor = SummerTheme.colors.goldWarm
    val positions = remember {
        listOf(
            Offset(0.10f, 0.08f), Offset(0.88f, 0.12f), Offset(0.50f, 0.05f),
            Offset(0.20f, 0.20f), Offset(0.80f, 0.25f)
        )
    }

    Canvas(modifier = modifier) {
        positions.forEachIndexed { i, off ->
            drawCircle(
                color = sparkleColor,
                radius = 3.dp.toPx(),
                center = Offset(size.width * off.x, size.height * off.y),
                alpha = sparkleAlphas.getOrElse(i) { 0.5f }
            )
        }
    }
}

@Composable
private fun NightStarsAnimation(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("night_stars.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        speed = 1f
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun rememberGentlePulse(): Float {
    val infiniteTransition = rememberInfiniteTransition(label = "button_pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    return scale
}