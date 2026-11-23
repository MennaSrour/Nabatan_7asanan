package com.menna.nabatan_7asena.domain.entity

internal data class KidsShort(
    val id: String,
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val category: String,
    val order: Long,
    val bundled: Boolean = false
)
