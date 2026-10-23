-keep,allowobfuscation,allowshrinking interface retrofit2.Call
-keep,allowobfuscation,allowshrinking class retrofit2.Response
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation
-keepattributes Signature, Exceptions, *Annotation*, InnerClasses, EnclosingMethod

-keep class com.menna.nabatan_7asena.data.remote.** { *; }


-keep interface com.menna.nabatan_7asena.data.PrayerTimesApi { *; }
-keep class com.menna.nabatan_7asena.data.PrayerApiResponse { *; }
-keep class com.menna.nabatan_7asena.data.PrayerData { *; }
-keep class com.menna.nabatan_7asena.data.Timings { *; }
-keep class com.menna.nabatan_7asena.data.Hijri { *; }
-keep class com.menna.nabatan_7asena.data.Date { *; }

-keep interface com.menna.nabatan_7asena.**Api { *; }

-keep class com.menna.nabatan_7asena.domain.entity.PrayerTimes { *; }
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken


-keepclassmembers class com.menna.nabatan_7asena.data.remote.dto.** {
    <init>();
}
-keepattributes *Annotation*
-keep class com.google.firebase.firestore.** { *; }
-dontwarn com.google.firebase.firestore.**


-keepclassmembers enum com.menna.nabatan_7asena.domain.entity.** {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-dontwarn kotlinx.coroutines.**