package com.menna.nabata_7asena.data.update

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApkDownloader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun canInstallApks(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun requestInstallPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                data = Uri.parse("package:${context.packageName}")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    suspend fun downloadApk(
        url: String,
        onProgress: (Int) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val file = File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                "update.apk"
            )

            if (file.exists()) {
                file.delete()
            }

            val connection = URL(url).openConnection() as HttpURLConnection
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("User-Agent", "Mozilla/5.0")
            connection.connect()

            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(
                    Exception("فشل الاتصال بالسيرفر: ${connection.responseCode}")
                )
            }

            val fileLength = connection.contentLength

            if (fileLength <= 0) {
                return@withContext Result.failure(
                    Exception("حجم الملف غير صحيح")
                )
            }

            val input = connection.inputStream
            val output = FileOutputStream(file)
            val buffer = ByteArray(8192)
            var total: Long = 0
            var count: Int

            while (input.read(buffer).also { count = it } != -1) {
                total += count
                val progress = (total * 100 / fileLength).toInt()
                onProgress(progress)
                output.write(buffer, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            if (!file.exists() || file.length() <= 0) {
                file.delete()
                return@withContext Result.failure(
                    Exception("فشل التحميل - الملف فارغ")
                )
            }

            Result.success(file)

        } catch (e: Exception) {
            Result.failure(
                Exception("خطأ في التحميل: ${e.message}", e)
            )
        }
    }

    fun installApk(file: File): Result<Unit> {
        return try {
            if (!file.exists() || file.length() <= 0) {
                return Result.failure(Exception("الملف غير موجود أو فارغ"))
            }

            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(intent)
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(
                Exception("فشل فتح شاشة التثبيت: ${e.message}", e)
            )
        }
    }
}