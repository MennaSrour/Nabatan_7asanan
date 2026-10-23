package com.menna.nabatan_7asena.presentation.common

import com.menna.nabatan_7asena.R
import com.menna.nabatan_7asena.domain.entity.User

/**
 * المصدر الوحيد لقوائم صور الأفاتار. أي شاشة عايزة تعرض أفاتار المستخدم
 * (تسجيل الدخول، الليدر بورد، الهوم لاحقًا...) لازم تستخدم الكاتالوج ده
 * بدل ما تعمل نسخة خاصة بيها من القايمة — عشان لو ضفنا أفاتار جديد
 * يظهر في كل مكان مرة واحدة، من غير ما ننسى نحدّث أكتر من ملف.
 *
 * ⚠️ لاحظي: كل عناصر boyAvatars حاليًا بتشاور على avatar_girl_1 (زي ما كانت
 * في LoginScreen الأصلية) — ده على الأرجح كان placeholder مؤقت. حدّثيه
 * بصور الولاد الحقيقية لما تجهز عندك في res/drawable.
 */
object AvatarCatalog {
    val boyAvatars = listOf(
        R.drawable.avatar_boy_1,
        R.drawable.avatar_boy_2,
        R.drawable.avatar_boy_3,
        R.drawable.avatar_boy_4,
        R.drawable.avatar_boy_5,
        R.drawable.avatar_boy_6
    )

    val girlAvatars = listOf(
        R.drawable.avatar_girl_1,
        R.drawable.avatar_girl_2,
        R.drawable.avatar_girl_3,
        R.drawable.avatar_girl_4,
        R.drawable.avatar_girl_5,
        R.drawable.avatar_girl_6
    )

    fun listFor(gender: User.Gender): List<Int> =
        if (gender == User.Gender.BOY) boyAvatars else girlAvatars

    fun resFor(gender: User.Gender, index: Int): Int {
        val list = listFor(gender)
        return list.getOrElse(index) { list.first() }
    }
}
