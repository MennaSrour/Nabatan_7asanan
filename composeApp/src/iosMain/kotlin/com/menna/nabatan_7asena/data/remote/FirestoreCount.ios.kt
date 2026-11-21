package com.menna.nabatan_7asena.data.remote

import cocoapods.FirebaseFirestoreInternal.FIRAggregateSource.FIRAggregateSourceServer
import dev.gitlive.firebase.firestore.Query
import dev.gitlive.firebase.firestore.ios
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
internal actual suspend fun Query.serverCount(): Long = suspendCancellableCoroutine { continuation ->
    ios.count.aggregationWithSource(FIRAggregateSourceServer) { snapshot, error ->
        if (continuation.isActive) {
            when {
                error != null -> continuation.resumeWithException(IllegalStateException(error.localizedDescription))
                snapshot != null -> continuation.resume(snapshot.count.longLongValue)
                else -> continuation.resumeWithException(IllegalStateException("Missing Firestore count result"))
            }
        }
    }
}
