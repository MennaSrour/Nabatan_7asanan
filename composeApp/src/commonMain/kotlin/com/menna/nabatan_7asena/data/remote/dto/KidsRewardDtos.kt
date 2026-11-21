package com.menna.nabatan_7asena.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class VerseBuilderDto(val reference: String, val words: List<String>)

@Serializable
data class SpeedQuestionDto(val question: String, val choices: List<String>, val correctIndex: Int)

@Serializable
data class WheelClaimDto(val prize: String)
