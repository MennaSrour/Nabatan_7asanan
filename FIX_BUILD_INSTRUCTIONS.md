# Build Fix Instructions

## The Problem
The Kotlin compile daemon is failing, which prevents the project from building. This is usually caused by:
1. Insufficient memory allocation
2. Corrupted Gradle/Kotlin daemon caches
3. Incompatible JDK versions

## Solution Steps (Execute in Order)

### Step 1: Stop All Gradle Daemons
Open PowerShell or Command Prompt and run:
```powershell
cd "D:\the chance\AndroidCompose\Nabata_7asena"
.\gradlew --stop
```

### Step 2: Clear Gradle Caches
Delete these folders manually:
1. `D:\the chance\AndroidCompose\Nabata_7asena\.gradle`
2. `D:\the chance\AndroidCompose\Nabata_7asena\build`
3. `D:\the chance\AndroidCompose\Nabata_7asena\app\build`

Or use this command:
```powershell
cd "D:\the chance\AndroidCompose\Nabata_7asena"
.\gradlew clean --no-daemon
```

### Step 3: Clear Kotlin Compiler Caches
Delete this folder:
```
C:\Users\[YOUR_USERNAME]\.kotlin\daemon-log
```

### Step 4: Verify JDK
Make sure you're using JDK 11 or JDK 17 (NOT JDK 22 or 21 for this Kotlin version).

Check your JDK:
```powershell
java -version
```

If needed, set JAVA_HOME to Android Studio's JDK:
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
```

### Step 5: Build with Fresh Start
```powershell
cd "D:\the chance\AndroidCompose\Nabata_7asena"
.\gradlew --stop
.\gradlew clean build --no-daemon --refresh-dependencies
```

### Step 6: If Still Failing, Build with Info
```powershell
.\gradlew assembleDebug --info --stacktrace
```

## Changes Already Made

✅ **gradle.properties** - Increased memory:
- Gradle JVM: 4GB (was 2GB)
- Kotlin daemon: 2GB with 512MB Metaspace
- Enabled parallel builds and caching

✅ **MainViewModel.kt** - Added missing `prayerTimesState`:
```kotlin
private val _prayerTimesState = MutableStateFlow<PrayerTimes?>(null)
val prayerTimesState: StateFlow<PrayerTimes?> = _prayerTimesState
```

✅ **HomeScreen.kt** - Fixed StateFlow collection:
```kotlin
val prayerTimes by viewModel.prayerTimesState.collectAsStateWithLifecycle()
val isLoading by viewModel.loadingState.collectAsStateWithLifecycle()
val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
```

✅ **app/build.gradle.kts** - Added lifecycle dependency:
```kotlin
implementation("androidx.lifecycle:lifecycle-runtime-compose:2.10.0")
```

## Alternative: Use Android Studio Sync

1. Open the project in Android Studio
2. Click **File → Invalidate Caches / Restart**
3. After restart, click **File → Sync Project with Gradle Files**
4. Build the project: **Build → Rebuild Project**

## Quick Fix Command (All in One)
```powershell
cd "D:\the chance\AndroidCompose\Nabata_7asena"
.\gradlew --stop
Remove-Item -Recurse -Force .gradle, build, app\build -ErrorAction SilentlyContinue
.\gradlew clean assembleDebug --no-daemon --refresh-dependencies
```

## If Nothing Works

The error may be due to JDK mismatch. The daemon log showed it was using:
- Wanted: JDK 22
- Actual: JDK 21 (Android Studio JBR)

To fix this permanently:
1. Open `gradle.properties`
2. Add this line:
```properties
org.gradle.java.home=C:\\Program Files\\Android\\Android Studio\\jbr
```

Or in your terminal, always set:
```powershell
$env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"
```

## Expected Result

After successful build, you should see:
```
BUILD SUCCESSFUL in XXs
```

The app should now compile without the Kotlin daemon error.

