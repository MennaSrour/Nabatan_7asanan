package com.menna.nabatan_7asena.domain.entity

enum class PresetCategory(val title: String) {
    GREETING("تحية وتشجيع"),
    WORSHIP("عبادة وقرآن"),
    GARDEN("هدية الحديقة")
}

enum class PresetMessage(val id: String, val text: String, val category: PresetCategory) {
    PEACE("peace", "السلام عليكم ورحمة الله", PresetCategory.GREETING),
    PEACE_REPLY("peace_reply", "وعليكم السلام ورحمة الله وبركاته", PresetCategory.GREETING),
    PRAISE("praise", "ما شاء الله عليك يا بطل", PresetCategory.GREETING),
    KEEP_GOING("keep_going", "أحسنت، استمر في التفوق", PresetCategory.GREETING),
    ACCEPTANCE("acceptance", "تقبل الله منا ومنكم", PresetCategory.GREETING),
    THANKS("thanks", "جزاك الله خيراً", PresetCategory.GREETING),
    PRAYER("prayer", "هل صليت الصلاة؟", PresetCategory.WORSHIP),
    READ_QURAN("read_quran", "هيا نقرأ ورد القرآن اليوم", PresetCategory.WORSHIP),
    FINISHED_QURAN("finished_quran", "أتممت وردي اليوم بحمد الله", PresetCategory.WORSHIP),
    REMEMBRANCE("remembrance", "لا تنسَ أذكار اليوم", PresetCategory.WORSHIP),
    FRIDAY("friday", "تحدي الجمعة ينتظرنا", PresetCategory.WORSHIP),
    WATER("water", "أرسلت لك قطرة ماء لحديقتك", PresetCategory.GARDEN)
}
