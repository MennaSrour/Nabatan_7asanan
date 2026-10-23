package com.menna.nabatan_7asena.core.util

import java.util.Calendar

/**
 * حالة الصلاة بالنسبة للوقت الحالي جوه نفس اليوم.
 *
 * - [UPCOMING]: وقتها لسه ما دخلش النهارده  → مينفعش تتعلّم.
 * - [CURRENT]:  احنا جوه وقتها (من أذانها لحد أذان اللي بعدها).
 * - [PASSED]:   وقتها دخل وعدّى (أو وقتها مش معروف) → ينفع تتعلّم.
 */
enum class PrayerPhase { UPCOMING, CURRENT, PASSED }

/** [minutes] = الوقت بالدقايق من بداية اليوم، أو null لو الوقت مش موجود/مش مفهوم. */
data class PrayerSlot(val id: Int, val minutes: Int?)

/**
 * [isFocused]: الصلاة اللي عليها الدور وتتعرض بارزة —
 * الحالية لو موجودة، وإلا الجاية (وده بيحصل بس بين منتصف الليل والفجر).
 */
data class PrayerStatus(val phase: PrayerPhase, val isFocused: Boolean)

/**
 * مصدر الحقيقة الوحيد لمواقيت الصلاة في الشاشة. كان فيه نسختين من الـ parsing
 * ونسختين من "الوقت وصل ولا لأ" (واحدة في الـ ViewModel وواحدة في الـ UI)،
 * وكانوا بيختلفوا في الحالات الحدّية. دلوقتي كله بيعدّي من هنا.
 *
 * الدوال هنا pure (بتاخد الوقت كـ parameter) عشان تتختبر بسهولة.
 *
 * ملاحظة مهمة: الـ "يوم" هنا هو اليوم الميلادي (نفس مفتاح dd-MM-yyyy اللي بيتخزّن بيه
 * كل شيء). مفيش "بكرة" جوه الموديل ده — الفجر بتاع بكرة مش صلاة النهارده، فمبيتعرضش
 * ولا بيتعلّم قبل ما اليوم يتغيّر فعلاً وتتحمّل قايمة اليوم الجديد.
 */
object PrayerTimeline {

    fun resolve(prayers: List<PrayerSlot>, nowMinutes: Int): Map<Int, PrayerStatus> {
        val timed: List<Pair<Int, Int>> = prayers.mapNotNull { p -> p.minutes?.let { p.id to it } }

        // الحالية = آخر صلاة دخل وقتها. لو لسه قبل الفجر → مفيش حالية.
        val currentId = timed.filter { (_, m) -> m <= nowMinutes }.maxByOrNull { (_, m) -> m }?.first
        // الجاية = أول صلاة وقتها لسه ما جاش النهارده (null بعد العشاء — ومفيش لفّة لبكرة).
        val nextId = timed.filter { (_, m) -> m > nowMinutes }.minByOrNull { (_, m) -> m }?.first
        val focusedId = currentId ?: nextId

        return prayers.associate { slot ->
            val minutes = slot.minutes
            val phase = when {
                minutes == null -> PrayerPhase.PASSED
                slot.id == currentId -> PrayerPhase.CURRENT
                minutes <= nowMinutes -> PrayerPhase.PASSED
                else -> PrayerPhase.UPCOMING
            }
            slot.id to PrayerStatus(phase = phase, isFocused = slot.id == focusedId)
        }
    }

    /** بيقبل "05:18" و"5:18 PM" و"5:18 م" وكمان الأرقام العربية (٠٥:١٨). */
    fun parseMinutes(raw: String?): Int? {
        if (raw.isNullOrBlank()) return null
        val match = TIME_REGEX.find(raw.toAsciiDigits()) ?: return null

        var hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].toInt()
        if (hour > 23 || minute > 59) return null

        when (match.groupValues[3].lowercase()) {
            "p", "م" -> if (hour < 12) hour += 12
            "a", "ص" -> if (hour == 12) hour = 0
        }
        return hour * 60 + minute
    }

    fun nowMinutes(): Int {
        val now = Calendar.getInstance()
        return now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)
    }

    private val TIME_REGEX = Regex("""(\d{1,2})\s*:\s*(\d{2})\s*([AaPpصم])?""")

    private fun String.toAsciiDigits(): String = buildString(length) {
        for (c in this@toAsciiDigits) {
            append(
                when (c) {
                    in '\u0660'..'\u0669' -> '0' + (c - '\u0660')
                    in '\u06F0'..'\u06F9' -> '0' + (c - '\u06F0')
                    else -> c
                }
            )
        }
    }
}
