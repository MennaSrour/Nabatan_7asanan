package com.menna.nabatan_7asena.di

import android.content.Context
import androidx.room.Room
import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.data.local.room.AppDatabase
import com.menna.nabatan_7asena.data.local.room.ANDROID_MIGRATION_5_6
import com.menna.nabatan_7asena.data.local.room.MIGRATION_4_5
import com.menna.nabatan_7asena.data.local.room.ANDROID_MIGRATION_6_7
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nabata_db"
        )
            .addMigrations(MIGRATION_4_5, ANDROID_MIGRATION_5_6, ANDROID_MIGRATION_6_7)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideAppDao(db: AppDatabase): AppDao {
        return db.appDao()
    }
}