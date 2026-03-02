package com.menna.nabata_7asena.domain

sealed class Resource<out T> {
    object Loading : Resource<Nothing>()
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val throwable: Throwable?, val message: String? = null) : Resource<Nothing>()
}
