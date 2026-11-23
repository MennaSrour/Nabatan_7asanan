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
            implementation(project.dependencies.platform(libs.firebase.bom))
            implementation(libs.firebase.messaging)
            implementation(libs.androidx.core.ktx)
            implementation(libs.sqlite.framework)
            implementation(libs.hilt.android)
            implementation(libs.androidx.hilt.navigation.compose)
            implementation(libs.androidx.hilt.work)
            implementation(libs.androidx.work.runtime.ktx)
            implementation(libs.play.services.location)
            implementation(libs.coroutines.play.services)
            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.ktor.android)
        }
        iosMain.dependencies { implementation(libs.ktor.ios) }
        commonTest.dependencies { implementation(kotlin("test")) }
        // Kotlin's default hierarchy creates androidMain and shared iosMain automatically.
    }
}

android {
    namespace = "com.menna.nabatan.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig { minSdk = libs.versions.android.minSdk.get().toInt() }
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
    listOf("kspAndroid", "kspIosX64", "kspIosArm64", "kspIosSimulatorArm64").forEach {
        add(it, libs.room.compiler)
    }
    add("kspAndroid", libs.hilt.compiler)
    add("kspAndroid", libs.androidx.hilt.compiler)
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
        version = libs.versions.ios.cocoapodsFramework.get()
        ios.deploymentTarget = libs.versions.ios.deploymentTarget.get()
        listOf("FirebaseCore", "FirebaseAuth", "FirebaseFirestore", "FirebaseRemoteConfig").forEach { sdk ->
            pod(sdk) {
                version = libs.versions.firebaseIos.get()
                linkOnly = true
            }
        }
    }
}
