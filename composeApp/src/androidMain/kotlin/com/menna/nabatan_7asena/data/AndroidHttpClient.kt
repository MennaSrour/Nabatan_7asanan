package com.menna.nabatan_7asena.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
actual fun platformHttpClient() = HttpClient(OkHttp)
