package com.menna.nabatan_7asena.presentation.screens.login.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.menna.nabatan_7asena.domain.entity.User
import com.menna.nabatan_7asena.presentation.common.GenderedStrings
import com.menna.nabatan_7asena.presentation.common.genderedStringResource

@Composable
internal fun GenderToggleRow(
    selectedGender: User.Gender,
    onGenderSelected: (User.Gender) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        GenderPill(
            label = genderedStringResource(GenderedStrings.genderBoy),
            isSelected = selectedGender == User.Gender.BOY,
            onClick = { onGenderSelected(User.Gender.BOY) }
        )
        GenderPill(
            label = genderedStringResource(GenderedStrings.genderGirl),
            isSelected = selectedGender == User.Gender.GIRL,
            onClick = { onGenderSelected(User.Gender.GIRL) }
        )
    }
}
