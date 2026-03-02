package com.menna.nabata_7asena.data.mappers

import com.menna.nabata_7asena.data.local.room.DailyPlanEntity
import com.menna.nabata_7asena.data.local.room.UserStatsEntity
import com.menna.nabata_7asena.data.remote.dto.DailyPlanDto
import com.menna.nabata_7asena.domain.entity.DailyContent
import com.menna.nabata_7asena.domain.entity.DailyContent.QuranWerd
import com.menna.nabata_7asena.domain.entity.DailyContent.Riddle
import com.menna.nabata_7asena.domain.entity.User

fun DailyPlanDto.toEntity(dayId: Int): DailyPlanEntity {
    return DailyPlanEntity(
        dayId = dayId,
        challenge = this.challenge,
        wisdom = this.wisdom.ifEmpty { "قال رسول الله ﷺ: تبسمك في وجه أخيك صدقة" },
        quranDhuhr = this.quranDhuhr,
        quranAsr = this.quranAsr,
        quranMaghrib = this.quranMaghrib,
        riddleQuestion = this.riddleQuestion,
        riddleAnswer = this.riddleAnswer,
        riddleOptions = this.riddleOptions
    )
}

// في ملف Mappers.kt
fun UserStatsEntity.toDomain(): User {
    return User(
        name = this.name,
        gender = if (this.gender.equals("BOY", ignoreCase = true)) User.Gender.BOY else User.Gender.GIRL,
        totalStars = this.totalStars,
        currentStreak = this.currentStreak,
        treeStage = this.treeStage,
        quranPartsFinished = this.quranPartsFinished,
        id = this.userId,
        prayerNotifications = this.prayerNotifications,
        azkarNotifications = this.azkarNotifications,
        quranNotifications = this.quranNotifications
    )
}

fun DailyPlanEntity.toDomain(): DailyContent {
    return DailyContent(
        dayId = this.dayId,
        challengeTitle = this.challenge,
        quranWerd = QuranWerd(this.quranDhuhr, this.quranAsr, this.quranMaghrib),
        wisdom = this.wisdom,
        riddle = if (this.riddleQuestion.isNotEmpty()) Riddle(
            this.riddleQuestion,
            this.riddleAnswer,
            this.riddleOptions.split(",")
        ) else null
    )
}