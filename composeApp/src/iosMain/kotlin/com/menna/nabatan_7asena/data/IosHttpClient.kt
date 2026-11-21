package com.menna.nabatan_7asena.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
actual fun platformHttpClient() = HttpClient(Darwin)
