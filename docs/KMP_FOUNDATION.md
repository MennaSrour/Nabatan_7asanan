# KMP foundation — Phase 1

## Module boundaries

- `app`: the existing Android application, screens, resources, Hilt, Room, Firebase,
  workers and notifications remain in `app/src/main`. It consumes `:composeApp`.
- `composeApp/src/commonMain`: a minimal shared Compose `App` placeholder; no existing
  screens or Firebase code have been migrated.
- `composeApp/src/androidMain`: Android library manifest; common code also compiles
  for the Android target.
- `composeApp/src/iosMain`: the UIKit `MainViewController` entry point.
- `iosApp`: a SwiftUI application and checked-in Xcode project importing the static
  `ComposeApp` framework. Kotlin's default hierarchy shares `iosMain` across
  `iosX64`, `iosArm64`, and `iosSimulatorArm64`.

Kotlin and the Compose compiler stay at **2.2.10**. Compose Multiplatform is pinned
at **1.9.0** (requires Kotlin 2.1 or newer). Existing Android dependency versions
and application IDs are retained. The static framework uses its own bundle ID.

## Android

Use JDK 21 and Android SDK 36. Gradle now uses the IDE/JAVA_HOME JDK instead of a
machine-specific Windows path. Keep the existing development Firebase configuration
in `app/google-services.json`; Firebase is not required by the shared module or iOS host.

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug
```

The app dependency ensures the shared module's Android code is built too. The
existing Android launcher and navigation continue to show the existing screens.

## iOS — requires macOS, Xcode and JDK 21

1. Install Xcode, accept its license, and select its command-line tools. Keep Android
   SDK 36 configured via `ANDROID_HOME` or root `local.properties`, because the
   Gradle project also configures the existing Android host.
2. Set `JAVA_HOME` to JDK 21 (or install it so `/usr/libexec/java_home -v 21` finds it).
3. Open `iosApp/iosApp.xcodeproj`; select the shared `iosApp` scheme.
4. Select a simulator and Run. For a physical device, choose your development team
   in Signing & Capabilities and an available bundle identifier.
5. The first build downloads Kotlin/Native and Compose dependencies. The first build
   phase calls `:composeApp:embedAndSignAppleFrameworkForXcode`, producing the correct
   simulator/device framework before Swift compilation. It is statically linked.

The iOS host displays only the foundation placeholder, not the Android application.
No existing screens, persistence, permissions, audio or Firebase services are shared yet.

Optional command-line checks on macOS:

```sh
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
xcodebuild -project iosApp/iosApp.xcodeproj -scheme iosApp \
  -configuration Debug -sdk iphonesimulator \
  -destination 'generic/platform=iOS Simulator' CODE_SIGNING_ALLOWED=NO build
```

Use `linkDebugFrameworkIosX64` for an Intel simulator or
`linkDebugFrameworkIosArm64` for a device framework. Apple targets cannot be built
or run on Linux; validate both a framework and the SwiftUI host on macOS before merging.
