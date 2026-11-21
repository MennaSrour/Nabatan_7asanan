package com.menna.nabatan_7asena.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import com.menna.nabatan_7asena.core.audio.*
import com.menna.nabatan_7asena.core.alarm.*
import com.menna.nabatan_7asena.data.LocationTrackerImpl
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.domain.*
import com.menna.nabatan_7asena.domain.repository.*
import com.menna.nabatan_7asena.platform.*
import com.menna.nabatan_7asena.presentation.platform.AndroidScreenAudioPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun fused(@ApplicationContext context: Context): FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    @Provides @Singleton fun location(impl: LocationTrackerImpl): LocationTracker = impl
    @Provides @Singleton fun audio(impl: AndroidAudioPlayerManager): AudioPlayerManager = impl
    @Provides @Singleton fun quranAudio(impl: QuranAudioPlayerImpl): QuranAudioPlayer = impl
    @Provides @Singleton fun downloads(impl: AndroidQuranAudioDownloadManager): QuranAudioDownloadManager = impl
    @Provides @Singleton fun alarms(impl: AndroidGlobalAlarmManager): GlobalAlarmManager = impl
    @Provides @Singleton fun services(dao: AppDao, location: LocationTracker, audio: AudioPlayerManager,
        quranAudio: QuranAudioPlayer, downloads: QuranAudioDownloadManager, alarms: GlobalAlarmManager,
        @ApplicationContext context: Context): SharedServices = SharedServices(dao, location, audio, quranAudio,
            downloads, alarms, AndroidPermissions(), AndroidScheduling(), { AndroidScreenAudioPlayer(context) })
    @Provides fun prayerTimes(services: SharedServices): GetPrayerTimesUseCase = services.getPrayerTimesUseCase
    @Provides fun daily(services: SharedServices): DailyActivityRepository = services.dailyRepository
    @Provides fun user(services: SharedServices): UserRepository = services.userRepository
    @Provides fun quran(services: SharedServices): QuranRepository = services.quranRepository
}
