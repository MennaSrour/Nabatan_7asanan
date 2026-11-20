package com.menna.nabatan_7asena.resources

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.Font as composeFont
import org.jetbrains.compose.resources.painterResource as composePainter
import org.jetbrains.compose.resources.stringResource as composeString

@Composable fun painterResource(id: Int): Painter = composePainter(ResourceCatalog.drawables.getValue(id))
@Composable fun stringResource(id: Int, vararg args: Any): String = composeString(ResourceCatalog.strings.getValue(id), *args)
suspend fun getResourceString(id: Int, vararg args: Any): String = getString(ResourceCatalog.strings.getValue(id), *args)
@Composable fun Font(id: Int, weight: FontWeight = FontWeight.Normal): androidx.compose.ui.text.font.Font = composeFont(ResourceCatalog.fonts.getValue(id), weight)
