package com.menna.nabatan_7asena.domain.entity

import kotlinx.serialization.Serializable

@Serializable
data class QuranPlaylist(val id: String, val name: String, val surahIds: List<Int> = emptyList())
