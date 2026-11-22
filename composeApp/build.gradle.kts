import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.cocoapods.CocoapodsExtension
import org.jetbrains.kotlin.gradle.targets.native.tasks.PodBuildTask

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
}

kotlin {
    androidTarget {
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }
    listOf(iosX64(), iosArm64(), iosSimulatorArm64()).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
            binaryOption("bundleId", "com.menna.nabatan.shared.ComposeApp")
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)
            implementation(libs.gitlive.firestore)
            implementation(libs.gitlive.auth)
            implementation(libs.gitlive.config)
            implementation(libs.coroutines.core)
            implementation(libs.serialization.json)
            implementation(libs.datetime)
            implementation(libs.lifecycle.viewmodel.compose)
            implementation(libs.lifecycle.runtime.compose)
            implementation(libs.navigation.compose)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.compottie)
            implementation(libs.ktor.core)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.json)
        }
        androidMain.dependencies {
            implementation(project.dependencies.platform("com.google.firebase:firebase-bom:33.16.0"))
            implementation("com.google.firebase:firebase-messaging")
            implementation(libs.androidx.core.ktx)
            implementation(libs.sqlite.framework)
            implementation(libs.hilt.android)
            implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
            implementation("androidx.hilt:hilt-work:1.2.0")
            implementation("androidx.work:work-runtime-ktx:2.9.0")
            implementation(libs.play.services.location)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")
            implementation("androidx.media3:media3-exoplayer:1.2.1")
            implementation(libs.ktor.android)
        }
        iosMain.dependencies { implementation(libs.ktor.ios) }
        commonTest.dependencies { implementation(kotlin("test")) }
        // Kotlin's default hierarchy creates androidMain and shared iosMain automatically.
    }
}

android {
    namespace = "com.menna.nabatan.shared"
    compileSdk = 36
    defaultConfig { minSdk = 24 }
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.resources {
    publicResClass = true
    packageOfResClass = "com.menna.nabatan.shared.generated.resources"
    generateResClass = always
}

dependencies {
    add("kspAndroid", libs.room.compiler)
    add("kspAndroid", libs.hilt.compiler)
    add("kspAndroid", "androidx.hilt:hilt-compiler:1.2.0")
    add("kspIosX64", libs.room.compiler)
    add("kspIosArm64", libs.room.compiler)
    add("kspIosSimulatorArm64", libs.room.compiler)
}

// Standalone test executables need native SDK linking; the app continues to use SPM.
if (providers.gradleProperty("firebaseNativeTests").orNull == "true") {
    apply(plugin = "org.jetbrains.kotlin.native.cocoapods")
    providers.gradleProperty("nativeTestArchitecture").orNull?.let { architecture ->
        require(architecture in setOf("arm64", "x86_64")) { "Unsupported simulator architecture: $architecture" }
        tasks.withType<PodBuildTask>().configureEach {
            xcodeBuildSettings.put("ARCHS", architecture)
            xcodeBuildSettings.put("ONLY_ACTIVE_ARCH", "YES")
        }
    }
    (kotlin as ExtensionAware).extensions.configure<CocoapodsExtension> {
        version = "1.0"
        ios.deploymentTarget = "15.0"
        listOf("FirebaseCore", "FirebaseAuth", "FirebaseFirestore", "FirebaseRemoteConfig").forEach { sdk ->
            pod(sdk) {
                version = libs.versions.firebaseIos.get()
                linkOnly = true
            }
        }
    }
}
