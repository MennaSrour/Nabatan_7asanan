package com.menna.nabatan_7asena.platform.animation

import androidx.compose.runtime.Composable
import com.menna.nabatan.shared.generated.resources.Res
import com.menna.nabatan_7asena.resources.ResourceCatalog
import io.github.alexzhirkevich.compottie.LottieCompositionSpec as CompottieSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition as rememberCompottie

sealed class LottieCompositionSpec {
    data class RawRes(val resourceId: Int) : LottieCompositionSpec()
    data class Asset(val filename: String) : LottieCompositionSpec()
}
object LottieConstants { const val IterateForever = Int.MAX_VALUE }
@Composable
fun rememberLottieComposition(spec: LottieCompositionSpec) = rememberCompottie(spec) {
    val path = when (spec) {
        is LottieCompositionSpec.RawRes -> ResourceCatalog.rawPaths.getValue(spec.resourceId)
        is LottieCompositionSpec.Asset -> "files/${spec.filename}"
    }
    CompottieSpec.JsonString(Res.readBytes(path).decodeToString())
}

@Composable
fun LottieAnimation(
    composition: io.github.alexzhirkevich.compottie.LottieComposition?,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    progress: (() -> Float)? = null,
    iterations: Int = 1,
    contentScale: androidx.compose.ui.layout.ContentScale = androidx.compose.ui.layout.ContentScale.Fit,
    alignment: androidx.compose.ui.Alignment = androidx.compose.ui.Alignment.Center,
    isPlaying: Boolean = true,
    speed: Float = 1f
) {
    val painter = if (progress == null) io.github.alexzhirkevich.compottie.rememberLottiePainter(
        composition = composition, iterations = iterations, isPlaying = isPlaying, speed = speed)
    else io.github.alexzhirkevich.compottie.rememberLottiePainter(composition = composition, progress = progress)
    androidx.compose.foundation.Image(painter, contentDescription = null, modifier = modifier,
        contentScale = contentScale, alignment = alignment)
}
