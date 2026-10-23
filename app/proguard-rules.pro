# Gson network DTOs
-keepclasseswithmembers,allowobfuscation class com.menna.nabatan_7asena.data.* {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Firestore DTOs
-keep,allowobfuscation class com.menna.nabatan_7asena.data.remote.dto.**
-keepclassmembers class com.menna.nabatan_7asena.data.remote.dto.** {
    <init>();
    <fields>;
    public *** get*();
}

# Gson prayer-times cache
-keep,allowobfuscation class com.menna.nabatan_7asena.domain.entity.PrayerTimes
-keepclassmembers class com.menna.nabatan_7asena.domain.entity.PrayerTimes {
    <fields>;
}
