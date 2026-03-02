package com.menna.nabata_7asena.di

import android.content.Context
import androidx.room.Room
import com.menna.nabata_7asena.data.local.room.AppDao
import com.menna.nabata_7asena.data.local.room.AppDatabase
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
            .fallbackToDestructiveMigration() 
            .build()
    }

    @Provides
    @Singleton
    fun provideAppDao(db: AppDatabase): AppDao {
        return db.appDao()
    }
}