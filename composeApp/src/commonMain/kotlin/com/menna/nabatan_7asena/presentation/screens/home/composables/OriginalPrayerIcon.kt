package com.menna.nabatan_7asena.presentation.screens.home.composables

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import com.menna.nabatan_7asena.resources.AppResources as R
import com.menna.nabatan_7asena.resources.ResourceCatalog
import com.menna.nabatan_7asena.resources.painterResource

@Composable
internal fun OriginalPrayerIcon(name: String, modifier: Modifier = Modifier) {
    val resource = ResourceCatalog.drawableIdsByName[prayerIconRes(name.removePrefix("صلاة ").trim())] ?: R.drawable.time
    Image(painterResource(resource), contentDescription = null, contentScale = ContentScale.Fit, modifier = modifier)
}
