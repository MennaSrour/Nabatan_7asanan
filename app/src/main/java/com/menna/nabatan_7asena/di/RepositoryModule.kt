package com.menna.nabatan_7asena.di

import com.menna.nabatan_7asena.data.LocationTrackerImpl
import com.menna.nabatan_7asena.data.local.FirstLaunchManager
import com.menna.nabatan_7asena.data.repository.DailyActivityRepositoryImpl
import com.menna.nabatan_7asena.data.repository.PrayerTimesRepositoryImpl
import com.menna.nabatan_7asena.data.repository.QuizRepositoryImpl
import com.menna.nabatan_7asena.data.repository.QuranRepositoryImpl
import com.menna.nabatan_7asena.data.repository.UserRepositoryImpl
import com.menna.nabatan_7asena.domain.LocationTracker
import com.menna.nabatan_7asena.domain.PrayerTimesRepository
import com.menna.nabatan_7asena.domain.repository.DailyActivityRepository
import com.menna.nabatan_7asena.domain.repository.FirstLaunchRepository
import com.menna.nabatan_7asena.domain.repository.QuizRepository
import com.menna.nabatan_7asena.domain.repository.QuranRepository
import com.menna.nabatan_7asena.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindFirstLaunchRepository(impl: FirstLaunchManager): FirstLaunchRepository

    @Binds
    @Singleton
    abstract fun bindPrayerRepository(
        impl: PrayerTimesRepositoryImpl
    ): PrayerTimesRepository

    @Binds
    @Singleton
    abstract fun bindLocationTracker(
        impl: LocationTrackerImpl
    ): LocationTracker

    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindDailyActivityRepository(
        impl: DailyActivityRepositoryImpl
    ): DailyActivityRepository

    @Binds
    @Singleton
    abstract fun bindQuranRepository(
        quranRepositoryImpl: QuranRepositoryImpl
    ): QuranRepository

    @Module
    @InstallIn(SingletonComponent::class)
    abstract class RepositoryModule {

        @Binds
        @Singleton
        abstract fun bindQuizRepository(
            quizRepositoryImpl: QuizRepositoryImpl
        ): QuizRepository
    }
}