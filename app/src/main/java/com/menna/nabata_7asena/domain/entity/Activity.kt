package com.menna.nabata_7asena.domain.entity

sealed class Activity {
    abstract val id: Int
    abstract val isCompleted: Boolean

    data class Prayer(
        override val id: Int,
        override val isCompleted: Boolean,
        val name: String,
        val time: String
    ) : Activity()

    data class Task(
        override val id: Int,
        override val isCompleted: Boolean,
        val title: String,
        val category: TaskCategory
    ) : Activity()
}

enum class TaskCategory {
    QURAN,
    CHALLENGE,
    AZKAR,
    EXTRA,
    PRAYER
}