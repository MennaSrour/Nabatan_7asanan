package com.menna.nabatan_7asena.data.remote

import com.google.firebase.firestore.AggregateSource
import dev.gitlive.firebase.firestore.Query
import dev.gitlive.firebase.firestore.android
import kotlinx.coroutines.tasks.await

internal actual suspend fun Query.serverCount(): Long = android.count().get(AggregateSource.SERVER).await().count
