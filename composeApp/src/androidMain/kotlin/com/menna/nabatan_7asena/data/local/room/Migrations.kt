package com.menna.nabatan_7asena.data.local.room

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * إضافة عمود avatarIndex لجدول user_stats من غير ما تتمسح أي بيانات
 * موجودة عند المستخدمين الحاليين. القيمة الافتراضية 0 (أول أفاتار في
 * الكتالوج) لأي مستخدم قديم مكانش اختار أفاتار أصلاً.
 */
val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            "ALTER TABLE user_stats ADD COLUMN avatarIndex INTEGER NOT NULL DEFAULT 0"
        )
    }
}
