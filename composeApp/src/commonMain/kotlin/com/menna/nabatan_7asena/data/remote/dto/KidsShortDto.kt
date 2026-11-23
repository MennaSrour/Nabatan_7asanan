package com.menna.nabatan_7asena.data.remote.dto

import com.menna.nabatan_7asena.domain.entity.KidsShort
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import kotlinx.serialization.Serializable

@Serializable
internal data class KidsShortDto(
    val id: String = "",
    val title: String,
    val videoUrl: String,
    val thumbnailUrl: String,
    val category: String,
    val order: Long,
    val isActive: Boolean = false
) {
    fun toDomain(documentId: String): KidsShort? {
        if (!isActive || title.isBlank() || category.isBlank()) return null
        if (!videoUrl.isHttpsUrl() || !thumbnailUrl.isHttpsUrl()) return null
        return KidsShort(id.ifBlank { documentId }, title, videoUrl, thumbnailUrl, category, order)
    }
}

private fun String.isHttpsUrl(): Boolean {
    if (!startsWith("https://", ignoreCase = true)) return false
    val url = runCatching { Url(this) }.getOrNull() ?: return false
    return url.protocol == URLProtocol.HTTPS && url.host.isNotBlank() &&
        url.user == null && url.password == null
}
