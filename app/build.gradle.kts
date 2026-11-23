import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.nio.file.StandardOpenOption
import java.util.Properties
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.google.services)
}

data class AndroidVersion(val major: Int, val minor: Int, val patch: Int, val code: Int) {
    val name: String get() = if (patch == 0) "$major.$minor" else "$major.$minor.$patch"

    fun nextPatch(): AndroidVersion = copy(patch = Math.incrementExact(patch), code = Math.incrementExact(code))

    fun serialize(): String = """
        VERSION_MAJOR=$major
        VERSION_MINOR=$minor
        VERSION_PATCH=$patch
        VERSION_CODE=$code
    """.trimIndent() + "\n"

    companion object {
        fun parse(contents: String): AndroidVersion {
            val properties = Properties().apply { contents.reader().use { load(it) } }
            fun number(key: String): Int = requireNotNull(properties.getProperty(key)?.toIntOrNull()?.takeIf { it >= 0 }) {
                "Missing or invalid $key in app/version.properties"
            }
            return AndroidVersion(number("VERSION_MAJOR"), number("VERSION_MINOR"), number("VERSION_PATCH"), number("VERSION_CODE"))
                .also { require(it.code in 1..2_100_000_000) { "VERSION_CODE must be in Google's allowed range" } }
        }
    }
}

val versionFile = layout.projectDirectory.file("version.properties")
val versionAtStart = AndroidVersion.parse(providers.fileContents(versionFile).asText.get())
val releaseTasks = setOf("assembleRelease", "bundleRelease")
val releaseRequested = gradle.startParameter.taskNames.any {
    it in releaseTasks || it.removePrefix(":").let { path ->
        path.startsWith("app:") && path.removePrefix("app:") in releaseTasks
    }
}
val versionForRelease = if (releaseRequested) versionAtStart.nextPatch().also {
    require(it.code <= 2_100_000_000) { "VERSION_CODE has reached Google's limit" }
} else versionAtStart

val releaseSigningVariables = listOf(
    "ANDROID_UPLOAD_STORE_FILE",
    "ANDROID_UPLOAD_STORE_PASSWORD",
    "ANDROID_UPLOAD_KEY_ALIAS",
    "ANDROID_UPLOAD_KEY_PASSWORD"
)
val releaseSigningValues = releaseSigningVariables.associateWith {
    providers.environmentVariable(it).orNull?.takeIf(String::isNotBlank)
}
val configureReleaseSigning = releaseSigningValues.values.any { it != null }
if (configureReleaseSigning) {
    val missing = releaseSigningVariables.filter { releaseSigningValues[it] == null }
    require(missing.isEmpty()) { "Missing Android upload signing variables: ${missing.joinToString()}" }
}

android {
    namespace = "com.menna.nabatan_7asena"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.menna.nabatan_7asena"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = versionAtStart.code
        versionName = versionAtStart.name

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (configureReleaseSigning) {
        signingConfigs.create("upload") {
            storeFile = rootProject.file(requireNotNull(releaseSigningValues["ANDROID_UPLOAD_STORE_FILE"]))
            storePassword = releaseSigningValues["ANDROID_UPLOAD_STORE_PASSWORD"]
            keyAlias = releaseSigningValues["ANDROID_UPLOAD_KEY_ALIAS"]
            keyPassword = releaseSigningValues["ANDROID_UPLOAD_KEY_PASSWORD"]
        }
    }

    buildTypes {
        debug {
            applicationIdSuffix = ".debug"
            resValue("string", "app_name", "App (Debug)")
        }
        release {
            signingConfig = signingConfigs.findByName("upload")
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        variant.outputs.forEach { output ->
            output.versionCode.set(versionForRelease.code)
            output.versionName.set(versionForRelease.name)
        }
    }
}

if (releaseRequested) {
    val versionPath = versionFile.asFile.toPath()
    val lockFile = rootProject.layout.projectDirectory.file(".gradle/android-release-version.lock").asFile
    var persisted = false
    tasks.matching { it.name in releaseTasks }.configureEach {
        doLast {
            if (!persisted) {
                lockFile.parentFile.mkdirs()
                FileChannel.open(lockFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE).use { channel ->
                    channel.lock().use {
                        check(AndroidVersion.parse(Files.readString(versionPath)) == versionAtStart) {
                            "app/version.properties changed during this build. Rebuild before distributing the artifact."
                        }
                        val temporary = Files.createTempFile(versionPath.parent, "version-", ".properties")
                        try {
                            Files.writeString(temporary, versionForRelease.serialize())
                            Files.move(temporary, versionPath, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                        } finally {
                            Files.deleteIfExists(temporary)
                        }
                    }
                }
                persisted = true
                logger.lifecycle("Android release version saved: ${versionForRelease.name} (${versionForRelease.code})")
            }
        }
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

dependencies {
    implementation(project(":composeApp"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.google.material)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.androidx.hilt.work)
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    ksp(libs.androidx.hilt.compiler)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.common)
    implementation(libs.firebase.analytics)
    coreLibraryDesugaring(libs.android.desugar.jdk)
    testImplementation(libs.junit)
    testImplementation(libs.datetime)
    testImplementation(libs.coroutines.test)
    androidTestImplementation(libs.androidx.compose.material3)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.room.runtime)
    androidTestImplementation(libs.datetime)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
