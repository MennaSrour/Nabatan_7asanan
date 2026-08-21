package com.menna.nabata_7asena.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.menna.nabata_7asena.data.local.room.AppDao
import com.menna.nabata_7asena.domain.entity.AyahModel
import com.menna.nabata_7asena.domain.repository.QuranRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONArray
import javax.inject.Inject

class QuranRepositoryImpl @Inject constructor(
    private val dao: AppDao,
    @ApplicationContext private val context: Context
) : QuranRepository {

    override fun getLastQuranPage(): Flow<Int> = dao.getLastQuranPage()

    override suspend fun saveQuranPage(page: Int) = dao.updateQuranPage(page)

    private var cachedData: JSONArray? = null

    private fun loadData(): JSONArray? {
        if (cachedData == null) {
            val jsonString =
                context.assets.open("hafsData.json").bufferedReader().use { it.readText() }
            cachedData = JSONArray(jsonString)
        }
        return cachedData
    }

    override fun getPageText(pageNumber: Int): String {
        val data = loadData() ?: return ""
        val sb = StringBuilder()

        for (i in 0 until data.length()) {
            val item = data.getJSONObject(i)
            if (item.getInt("page") == pageNumber) {
                val text = item.getString("aya_text")
                val suraNo = item.getInt("sura_no")
                val ayaNo = item.getInt("aya_no")

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
        for (i in 0 until data.length()) {
            val item = data.getJSONObject(i)
            if (item.getInt("sura_no") == suraNumber) {
                list.add(
                    AyahModel(
                        id = item.getInt("id"),
                        suraNo = item.getInt("sura_no"),
                        ayaNo = item.getInt("aya_no"),
                        text = item.getString("aya_text"),
                        cleanText = item.optString("aya_text_emlaey", ""),
                        suraNameAr = item.optString("sura_name_ar", ""),
                        suraNameEn = item.optString("sura_name_en", ""),
                        page = item.getInt("page"),
                        jozz = item.getInt("jozz")
                    )
                )
            }
        }
        return list
    }

    override fun getBookmarkStatusFlow(): Flow<Boolean> = callbackFlow {
        val sharedPreferences = context.getSharedPreferences("quran_prefs", Context.MODE_PRIVATE)

        val currentBookmark = sharedPreferences.getInt("bookmark_surah", -1) > 0
        trySend(currentBookmark)

        val listener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
            if (key == "bookmark_surah") {
                val hasBookmark = prefs.getInt("bookmark_surah", -1) > 0
                trySend(hasBookmark)
            }
        }

        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)

        awaitClose {
            sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
        }
    }
}