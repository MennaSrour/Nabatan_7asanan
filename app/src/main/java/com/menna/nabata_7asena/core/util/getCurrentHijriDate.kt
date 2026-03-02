package com.menna.nabata_7asena.core.util

import android.os.Build
import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter
import java.util.Locale

object DateUtils {

    fun getCurrentHijriDate(offsetDays: Long = 0): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {

                val today = LocalDate.now().plusDays(offsetDays)

                val hijrahDate = HijrahDate.from(today)

                val formatter = DateTimeFormatter.ofPattern(
                    "d MMMM yyyy",
                    Locale("ar", "EG")
                )

                formatter.format(hijrahDate)

            } catch (e: Exception) {
                "التاريخ الهجري"
            }
        } else {
            "التاريخ الهجري"
        }
    }
}