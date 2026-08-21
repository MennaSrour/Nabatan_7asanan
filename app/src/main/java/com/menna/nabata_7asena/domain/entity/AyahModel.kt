package com.menna.nabata_7asena.domain.entity

data class AyahModel(
    val id: Int,
    val suraNo: Int,
    val ayaNo: Int,
    val text: String,
    val cleanText: String,
    val suraNameAr: String,
    val suraNameEn: String,
    val page: Int,
    val jozz: Int
)
