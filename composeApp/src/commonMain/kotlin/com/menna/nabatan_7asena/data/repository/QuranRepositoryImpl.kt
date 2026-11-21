package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.local.room.AppDao
import com.menna.nabatan_7asena.data.local.QuranBookmarkStore
import com.menna.nabatan_7asena.domain.entity.AyahModel
import com.menna.nabatan_7asena.domain.repository.QuranRepository
import com.menna.nabatan_7asena.platform.readResourceText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.serialization.json.*

class QuranRepositoryImpl(private val dao: AppDao, private val bookmarks: QuranBookmarkStore) : QuranRepository {
    override fun getLastQuranPage(): Flow<Int> = dao.getLastQuranPage()
    override suspend fun saveQuranPage(page: Int) = dao.updateQuranPage(page)
    private val ayahsBySurah by lazy {
        Json.parseToJsonElement(readResourceText("files/hafsData.json")).jsonArray.map { entry ->
            val item = entry.jsonObject
            AyahModel(item.int("id"), item.int("sura_no"), item.int("aya_no"),
                item.text("aya_text"), item.text("aya_text_emlaey"), item.text("sura_name_ar"),
                item.text("sura_name_en"), item.int("page"), item.int("jozz"))
        }.groupBy { it.suraNo }
    }
    override fun getSurahAyahs(suraNumber: Int): List<AyahModel> = ayahsBySurah[suraNumber].orEmpty()
    override fun getPageText(pageNumber: Int): String = ayahsBySurah.values.flatten()
        .filter { it.page == pageNumber }.joinToString(" ") {
            val basmala = if (it.ayaNo == 1 && it.suraNo !in listOf(1, 9)) "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ\n" else ""
            basmala + com.menna.nabatan_7asena.domain.util.uthmaniText(it.text, it.ayaNo) +
                " " + com.menna.nabatan_7asena.core.util.ayahMarker(it.ayaNo)
        }


    override fun getBookmarkStatusFlow() = bookmarks.bookmark.map { it != null }.distinctUntilChanged()
}
private fun JsonObject.int(key: String) = getValue(key).jsonPrimitive.int
private fun JsonObject.text(key: String, default: String = "") = get(key)?.jsonPrimitive?.contentOrNull ?: default
