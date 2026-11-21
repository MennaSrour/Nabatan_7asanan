package com.menna.nabatan_7asena.data.remote

import dev.gitlive.firebase.firestore.Query

/** Server aggregation only: never materializes the matching documents. */
internal expect suspend fun Query.serverCount(): Long
