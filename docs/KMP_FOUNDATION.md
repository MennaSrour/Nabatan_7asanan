# Shared Android and iOS application

## Module boundaries

- `app` is the Android host: `MainActivity`, `NabataApplication`, manifest, launcher
  resources, Google Services configuration and application packaging.
- `composeApp/src/commonMain` contains all screens, navigation, UI state,
  ViewModels, themes, models, repositories, use cases, Room entities/DAOs,
  Firestore and Remote Config integration, prayer HTTP client and Compose resources.
- `composeApp/src/androidMain` supplies Hilt ViewModel factories, Android Room
  creation/migration, preferences, permissions, location, audio, downloads,
  workers, alarms, notifications and system intents.
- `composeApp/src/iosMain` supplies the UIKit entry point, lifecycle ViewModel
  factories, Room/SQLite, NSUserDefaults, Darwin HTTP and platform adapters.
- `iosApp` is the SwiftUI/Xcode host. Both hosts render the same `App()`.

The static `ComposeApp` framework targets `iosX64`, `iosArm64` and
`iosSimulatorArm64`. Kotlin/Compose compiler is 2.2.20; Compose Multiplatform is
1.9.1. Navigation and lifecycle use their JetBrains multiplatform distributions.
Hilt remains an Android implementation detail. Lifecycle 2.9.5 and Navigation
2.9.1 align with Compose 1.9.1 and use SavedState 1.3.5, which delegates the
native Compose state implementation to AndroidX instead of linking duplicate
`LocalSavedStateRegistryOwner` symbols.

## Firebase

Shared Firestore and Remote Config use GitLive 2.1.0. This release still publishes
Intel iOS simulator artifacts; GitLive 2.3 does not. Its Android KTX dependencies
require Firebase BOM 33.16.0 (BOM 34 removed KTX modules) and desugaring 2.1.5.
No Firebase Auth dependency is added because the application does not use it.

Android keeps its existing Firebase initialization, automatic Analytics collection
and `app/google-services.json`.
For a new environment, provide a development Firebase configuration matching the
Android application ID (including `.debug` for debug). Do not introduce production
credentials into CI. Firebase configuration filenames are ignored for new files;
any previously tracked configuration is unaffected by that ignore rule.

For iOS, register `com.menna.nabatan7asena.ios` in the **same intended Firebase
project** and place its `GoogleService-Info.plist` in `iosApp/iosApp/`. The Xcode
build copies the optional file to the bundle; Swift initializes Firebase before
creating the Kotlin controller. Without the file, shared Firestore requests fail
as recoverable data errors instead of accessing an unconfigured native SDK.
Remote Config's Android Play Store update policy is disabled on iOS.

Standalone Gradle iOS tests opt into CocoaPods **link-only** SDK dependencies:
`./gradlew -PfirebaseNativeTests=true :composeApp:iosSimulatorArm64Test`. Install
CocoaPods first (`pod --version`); the hosted macOS runner already includes it.
This provides Firebase 11.15 frameworks and runtime search paths for test
executables without generating duplicate GitLive cinterops. The flag is not used
for Xcode app builds, which retain Swift Package Manager integration.

The Xcode project links FirebaseCore, FirebaseFirestore and FirebaseRemoteConfig
through the official Firebase Swift package (11.x, compatible with GitLive 2.1's
native SDK baseline). No service-account keys belong in either client.

## Persistence and resources

Android retains `nabata_db`, Room schema version 5, migration 4→5, SharedPreferences
filenames/keys, and JSON cache field names. The generated Room identity hash is
unchanged. iOS creates its own local database in Documents and uses namespaced
NSUserDefaults. Network DTOs and prayer caches use generated Kotlin serializers.

Strings (including male/female Arabic variants), fonts, images, animations,
Quran text and bundled audio live under `commonMain/composeResources`.
`AppResources` is a platform-neutral compatibility catalog for the existing
integer-based UI/audio contracts; it resolves to typed Compose resources and file
paths, without Android resource reflection. Its negative IDs are stable hashes
of resource names. After adding or renaming a resource, run:

```sh
python3 tools/generate_resource_catalog.py
```

Android recovers sounds for pre-migration alarm PendingIntents from their prayer/
zekr content and destination. Old Android numeric IDs vary between releases and
are deliberately not interpreted as a fixed resource table. New alarms use the
stable shared IDs.
Arabic RTL, bundled fonts and user-gender resource selection stay in shared UI.

## Platform capabilities

| Capability | Android | iOS |
| --- | --- | --- |
| Shared UI, navigation, business logic | Shared implementation | Shared implementation |
| Firestore, Room, preferences, prayer HTTP | Working platform services | GitLive, bundled SQLite, NSUserDefaults, Darwin |
| Notification/worker scheduling and permissions | Existing implementation retained | Explicit no-op; Android permission onboarding skipped |
| Location | Play Services | Unavailable; existing Cairo fallback applies |
| Bundled interaction sounds | MediaPlayer | Silent completion to keep interactions responsive |
| Quran playback and audio downloads | ExoPlayer and existing downloads | Explicit unsupported errors, never fake successful downloads |
| APK updates/store intents | Existing Android behavior | Android update policy disabled |

The iOS platform adapters are deliberate compiling fallbacks, not a claim of
notification/audio/location feature parity. Implement native capabilities behind
the existing ports without changing shared screen or domain contracts.

## Verification

Use JDK 21 and Android SDK 36:

```sh
./gradlew :composeApp:compileCommonMainKotlinMetadata :app:assembleDebug
./gradlew :composeApp:testDebugUnitTest :app:testDebugUnitTest
./gradlew :app:compileDebugAndroidTestKotlin :composeApp:lintDebug :app:lintDebug
```

On macOS with Xcode and JDK 21, open `iosApp/iosApp.xcodeproj`, select the `iosApp`
scheme and run on a simulator. The framework build phase runs Gradle before Swift
compilation. Select a development team for physical devices. Keep Android SDK 36
configured because Gradle also configures the Android host.

```sh
./gradlew :composeApp:compileKotlinIosX64 :composeApp:compileKotlinIosArm64 \
  :composeApp:compileKotlinIosSimulatorArm64
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -configuration Debug -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build
```

Linux cannot compile or link Apple targets. A successful common metadata build
and an Android build do not substitute for the macOS checks above. Run device
smoke tests for existing notifications, cached profiles and Quran audio before
release; JVM checks cannot exercise OS permission or playback behavior.
