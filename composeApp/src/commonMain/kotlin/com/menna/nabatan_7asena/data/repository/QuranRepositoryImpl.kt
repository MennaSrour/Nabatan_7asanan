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
    private var cachedData: JsonArray? = null
    private fun loadData(): JsonArray? {
        if (cachedData == null) cachedData = Json.parseToJsonElement(readResourceText("files/hafsData.json")).jsonArray
        return cachedData
    }
    override fun getPageText(pageNumber: Int): String {
        val data = loadData() ?: return ""
        val sb = StringBuilder()

        for (i in 0 until data.size) {
            val item = data[i].jsonObject
            if (item.int("page") == pageNumber) {
                val text = item.text("aya_text")
                val suraNo = item.int("sura_no")
                val ayaNo = item.int("aya_no")

                if (ayaNo == 1 && suraNo != 1 && suraNo != 9) {
                    sb.append("بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ\n\n")
                }

                val cleanedText = text.replace(Regex("[^\u0600-\u06FF\u0020]"), "").trim()
                sb.append(cleanedText)

                sb.append("\u200D\u06DD\u200F")

                sb.append(" ")
            }
        }
        return sb.toString()
    }

    override fun getSurahAyahs(suraNumber: Int): List<AyahModel> {
        val data = loadData() ?: return emptyList()
        val list = mutableListOf<AyahModel>()
        for (i in 0 until data.size) {
            val item = data[i].jsonObject
            if (item.int("sura_no") == suraNumber) {
                list.add(
                    AyahModel(
                        id = item.int("id"),
                        suraNo = item.int("sura_no"),
                        ayaNo = item.int("aya_no"),
                        text = item.text("aya_text"),
                        cleanText = item.text("aya_text_emlaey", ""),
                        suraNameAr = item.text("sura_name_ar", ""),
                        suraNameEn = item.text("sura_name_en", ""),
                        page = item.int("page"),
                        jozz = item.int("jozz")
                    )
                )
            }
        }
        return list
    }


    override fun getBookmarkStatusFlow() = bookmarks.bookmark.map { it != null }.distinctUntilChanged()
}
private fun JsonObject.int(key: String) = getValue(key).jsonPrimitive.int
private fun JsonObject.text(key: String, default: String = "") = get(key)?.jsonPrimitive?.contentOrNull ?: default
