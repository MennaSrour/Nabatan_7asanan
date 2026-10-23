package com.menna.nabatan_7asena.presentation.platform

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal abstract class ScreenAudioModule {
    @Binds abstract fun bindScreenAudioPlayer(player: AndroidScreenAudioPlayer): ScreenAudioPlayer
}
