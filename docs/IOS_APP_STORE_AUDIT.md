# iOS App Store readiness audit

Status: **not yet ready for submission**. This audit fixes concrete host, permission,
audio and layout defects. A successful build or privacy plist does not certify Kids
Category compliance or replace device/parental/privacy review.

## Platform contract audit

| Boundary | Finding and resulting behavior |
| --- | --- |
| Preferences | NSUserDefaults, app-prefixed keys; clearing only removes this app's named scope. Privacy manifest declares UserDefaults reason CA92.1. |
| Resources | Compose resources resolve via bundled file URLs; invalid resource/audio IDs end the waiting UI safely. |
| Location | When-In-Use only; no background-location entitlement. Requests run on Main, serialize authorization/fixes, reject invalid/older fixes, and time out safely. Cancellation cleanup checks continuation identity so it cannot clear a replacement request. Saved/manual-city fallback remains available. |
| Notifications | Launch only checks existing authorization. Permission is requested after the explicit onboarding/Settings action and localized purpose explanation. Authorized APNs registration enables FCM auto-init; missing/mismatched Firebase config leaves the host offline. |
| Haptics | Shared UI uses Compose LocalHapticFeedback; there is no app-specific haptics expect/actual. Compose supplies the native implementation; no microphone/vibration permission plist key is required. Hardware/silent/accessibility behavior needs device testing. |
| Bundled audio | Replaces immediate-success stubs with retained AVAudioPlayer/delegate instances. Replacement/stop discards prior callbacks; decode/missing-resource failure releases waiting screens once. No recording/microphone access requested. |
| Quran streaming/downloads | Still explicitly unsupported on iOS. Streaming reports onError; downloads return Failed. These remain release blockers for advertised Quran playback/offline features. |
| Local prayer/azkar/Quran alarms | Scheduling and cancellation adapters remain no-ops on iOS. APNs broadcasts are a separate feature and do not implement prayer reminders. Implement UNUserNotificationCenter local scheduling before advertising timed reminders on iOS. |
| Firebase/Firestore count | GitLive/Firestore actuals and Room-generated actuals are included in native compilation. Native Firebase initialization precedes Compose service resolution; config bundle ID/project/app/API-key presence is checked. Server count propagates error/missing results and ignores canceled continuations. Network/offline/auth failures require device testing with a non-production project. |
| Store opening | Unsupported iOS update contract returns false, allowing the existing unavailable-store UI; no fabricated App Store ID or hidden external link. |

`ci-kmp.yml` compiles all three configured iOS Kotlin targets, runs native tests on the
runner's simulator architecture, validates plists and builds the SwiftUI simulator
host. Linux can run Android/shared metadata/tests but cannot execute Apple toolchains.
Check the PR's native CI result before treating compilation as verified.

## Usage disclosures and safe areas

- English default location disclosure and Arabic/English InfoPlist.strings explain
  that coordinates reach the prayer-time service and a saved city is an alternative.
- iOS does **not** expose a customizable notification usage-description plist key.
  `NabataNotificationPurpose` is app-owned localized text shown before the fixed
  system notification prompt; it is not an Apple permission entitlement. Both
  Continue and Not now are localized. Existing Arabic onboarding explains the need.
- SwiftUI gives Compose the full container and keyboard region. Compose scaffolds
  and existing inset-aware bars consume safe areas; Login and Settings explicitly
  handle IME padding, and Login scrolls when a keyboard/small screen reduces space.
- Review portrait/landscape iPhone notch/home indicator, iPad split view, Arabic RTL,
  large text, hardware keyboard and interactive keyboard dismissal on real devices.
  Simulator compilation alone does not establish visual correctness.

## Kids Category and privacy findings

No parental gate or verified parental-consent flow was found. The existing app uses
Firebase anonymous auth, cloud profiles/leaderboards/progress, FCM installation IDs,
preset social messages and a third-party coordinate prayer-time service. OS location
or notification authorization is **not** parental consent. These concrete data flows
must be assessed against Apple's Kids requirements and applicable child-privacy
requirements before selecting Kids Category or making compliance claims.

This PR permanently deactivates Firebase Analytics collection in the iOS host and
disables automatic Messaging initialization until notification authorization. It
does not disable essential Firestore/Auth features or pretend that all Firebase
services qualify for a Kids exception. Review SDK-generated privacy reports and
network traffic, data retention/deletion, security rules, contact/profile visibility,
and parent controls. Audit production Remote Config and backend behavior as well.

PrivacyInfo.xcprivacy is a conservative app-level disclosure draft covering names,
user/device identifiers, location, social content and product interaction/progress,
with functionality purposes and no tracking. Location is marked unlinked because
this client sends coordinates to Aladhan without the Firebase user ID; confirm the
service's handling before submission. All other declared identifiers/content are
conservatively marked linked. SDK manifests must be aggregated with the app manifest;
match the final App Store Connect privacy labels to actual release data flows.

Remaining submission blockers outside this patch:

1. Complete or clearly remove unsupported advertised iOS streaming/download/local
   reminder functionality; validate interruptions, lock screen and denied permissions.
2. Implement and review parental gates/consent and data controls appropriate to the
   chosen audience, especially for cloud/social features and any outgoing links.
3. Supply a real privacy-policy URL, support contact, age rating and accurate privacy
   labels in App Store Connect. No real policy URL was supplied in this repository.
4. Add the final iOS AppIcon asset catalog (including an opaque 1024px marketing icon)
   and review screenshots/metadata. The current 200px transparent splash art is not
   an App Store icon, and this audit does not upscale it or fabricate branding.
5. Configure the real iOS Firebase app, APNs key, Apple signing/team/provisioning and
   production rules. Test on device/TestFlight; simulator signing-free builds do not
   validate APNs delivery, production Firebase or submission signing.

References for release review: Apple App Review Guidelines 1.3 (Kids), 5.1 (privacy),
UserNotifications authorization documentation, and required-reason API/privacy
manifest documentation. No approval/compliance certification is asserted by this PR.
