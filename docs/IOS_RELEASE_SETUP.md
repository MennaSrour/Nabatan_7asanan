# Signed iOS release builds

In **GitHub → Settings → Secrets and variables → Actions → Repository secrets**, add:

| Secret | Value |
| --- | --- |
| `APPLE_CERT_BASE64` | Base64 of a password-protected `.p12` exported from Keychain Access, containing the signing certificate **and its private key**. Usually an Apple Distribution identity. |
| `APPLE_CERT_PASSWORD` | The non-empty password used to export that `.p12`. |
| `APPLE_PROVISIONING_PROFILE_BASE64` | Base64 of an unexpired `.mobileprovision` for the same team, certificate and application ID. |

Generate each Base64 value on macOS without line wrapping:

```sh
base64 -i distribution.p12 | tr -d '\n' | pbcopy
base64 -i App.mobileprovision | tr -d '\n' | pbcopy
```

The current Xcode bundle identifier is `com.menna.nabatan7asena.ios`. Register that
App ID in Apple Developer, create its profile, and include the exported certificate
in that profile. If you change the bundle ID, update the Xcode project and profile
together. Ad hoc/development profiles must include the intended device UDIDs.

No team-ID, keychain-password or export-options secrets are needed: CI derives the
team, profile UUID and export method from the profile and generates a temporary
keychain password. App Store profiles export with `app-store-connect`; ad hoc
profiles use `release-testing`; development profiles use `debugging`; enterprise
profiles use `enterprise`. Choose a matching certificate/profile pair. An App
Store IPA is for TestFlight/App Store submission, not direct device installation.

After setting **all three** secrets, push to `main` or manually run **KMP CI**.
`ios-release` runs after Android and iOS checks pass and uploads `ios-release-ipa`
(retained for seven days). With missing secrets the entire release job is skipped;
pull requests never receive signing secrets or run release signing. Invalid,
expired or mismatched credentials fail the release job with a diagnostic.

Certificates and profiles are decoded into runner temporary storage, installed in
an ephemeral keychain, and removed on script exit. Release DerivedData and signed
archives are not cached. This workflow exports an IPA; it does not upload it to
App Store Connect and needs no App Store Connect API key.

## Unsigned simulator build

Every selected iOS CI run also uploads `ios-simulator-app`, a ZIP containing
`iosApp.app` at its root, built with `iphonesimulator` and signing disabled. Upload
that ZIP to Appetize.io using a compatible simulator architecture. It is separate
from the signed device IPA. The app uses Swift Package Manager for Firebase.
Standalone native tests use CocoaPods link-only dependencies through
`-PfirebaseNativeTests=true`; the hosted macOS image includes CocoaPods. Caches
cover Swift packages, DerivedData, Kotlin/Native and CocoaPods SDK build/download
outputs. Native SDK caches are saved even when a later build step fails.

CI uses JDK 17 and installs Android tooling on both hosts because the shared Gradle
project configures its Android target even during Apple builds. Android CI always
overwrites `google-services.json` with an isolated, nonfunctional placeholder; no
production Firebase keys are required. iOS can build without a Firebase plist and
uses the existing recoverable unconfigured-Firebase behavior. Real Firebase
integration and device smoke tests remain separate from these unit/build checks.
