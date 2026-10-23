package com.menna.nabatan_7asena.presentation.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.res.stringResource
import com.menna.nabatan_7asena.domain.entity.User

/** Presentation-only references; the domain Gender contains no Android resources. */
data class GenderedString(
    @param:StringRes @get:StringRes val maleResId: Int,
    @param:StringRes @get:StringRes val femaleResId: Int
) {
    @StringRes
    fun resourceId(gender: User.Gender): Int = when (gender) {
        User.Gender.BOY -> maleResId
        User.Gender.GIRL -> femaleResId
    }
}

/** Supplied from MainUiState; login overrides it with LoginUiState.selectedGender. */
val LocalUserGender = compositionLocalOf { User.Gender.BOY }

/**
* Resolves on every composition so gender and locale changes update visible text.
* Pass a screen state's gender explicitly, or inherit the app/login state provider.
* Example: genderedStringResource(GenderedStrings.taskComplete, title, gender = state.gender).
*/
@Composable
@ReadOnlyComposable
fun genderedStringResource(
    text: GenderedString,
    vararg formatArgs: Any,
    gender: User.Gender = LocalUserGender.current
): String {
    val resourceId = text.resourceId(gender)
    return if (formatArgs.isEmpty()) stringResource(resourceId)
    else stringResource(resourceId, *formatArgs)
}
