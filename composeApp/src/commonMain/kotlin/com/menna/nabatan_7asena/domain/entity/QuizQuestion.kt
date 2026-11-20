package com.menna.nabatan_7asena.domain.entity

data class QuizQuestion(
    val id: Int,              // = order من الفايرستور، بنستخدمها كهوية ثابتة للسؤال
    val text: String,
    val choices: List<String>,
    val correctIndex: Int
)

data class QuizWinner(
    val userId: String,
    val name: String,
    val gender: User.Gender,
    val avatarIndex: Int,
    val score: Int
)
