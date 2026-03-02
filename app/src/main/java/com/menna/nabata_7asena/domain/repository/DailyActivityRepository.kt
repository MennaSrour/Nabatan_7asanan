package com.menna.nabata_7asena.domain.repository

import com.menna.nabata_7asena.domain.entity.Activity
import com.menna.nabata_7asena.domain.entity.DailyContent
import com.menna.nabata_7asena.domain.entity.ExtraTasks
import kotlinx.coroutines.flow.Flow

interface DailyActivityRepository {
    fun getDailyActivities(date: String, city: String,country:String): Flow<List<Activity>>
    suspend fun completeDailyActivity(activity: Activity)
    suspend fun updateActivityStatus(date: String, activityId: Int, isCompleted: Boolean)
    suspend fun getSuggestedExtraTasks(): ExtraTasks
    suspend fun getTodayRiddle(): DailyContent?
    suspend fun addExtraTask(title: String)
    suspend fun getPlanForDay(dayId: Int): DailyContent?
}