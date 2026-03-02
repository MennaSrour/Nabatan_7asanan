package com.menna.nabata_7asena.di

import com.menna.nabata_7asena.data.update.UpdateRemoteDataSource
import com.menna.nabata_7asena.data.update.UpdateRepositoryImpl
import com.menna.nabata_7asena.domain.repository.UpdateRepository
import com.menna.nabata_7asena.domain.usecase.CheckForUpdateUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UpdateModule {

    @Provides
    @Singleton
    fun provideUpdateRepository(
        remoteDataSource: UpdateRemoteDataSource
    ): UpdateRepository {
        return UpdateRepositoryImpl(remoteDataSource)
    }

    @Provides
    fun provideCheckForUpdateUseCase(
        repository: UpdateRepository
    ): CheckForUpdateUseCase {
        return CheckForUpdateUseCase(repository)
    }
}