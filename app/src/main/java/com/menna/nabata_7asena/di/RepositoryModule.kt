package com.menna.nabata_7asena.di

import com.menna.nabata_7asena.data.LocationTrackerImpl
import com.menna.nabata_7asena.data.repository.DailyActivityRepositoryImpl
import com.menna.nabata_7asena.data.repository.PrayerTimesRepositoryImpl
import com.menna.nabata_7asena.data.repository.QuranRepositoryImpl
import com.menna.nabata_7asena.data.repository.UserRepositoryImpl
import com.menna.nabata_7asena.domain.LocationTracker
import com.menna.nabata_7asena.domain.PrayerTimesRepository
import com.menna.nabata_7asena.domain.repository.DailyActivityRepository
import com.menna.nabata_7asena.domain.repository.QuranRepository
import com.menna.nabata_7asena.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

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
}