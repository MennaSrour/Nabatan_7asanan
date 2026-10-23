package com.menna.nabatan_7asena.data.remote.dto

/** مستند وحدة في مجموعة quiz_questions — order بيحدد ترتيب الاختيار الأساسي */
data class QuizQuestionDto(
    val order: Int = 0,
    val text: String = "",
    val choices: List<String> = emptyList(),
    val correctIndex: Int = 0
)

/** مستند نتيجة طفل في مسابقة أسبوع معين — quiz_scores/{weekId}/entries/{userId} */
data class QuizScoreDto(
    val userId: String = "",
    val name: String = "",
    val gender: String = "BOY",
    val avatarIndex: Int = 0,
    val score: Int = 0
)
