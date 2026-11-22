# Firestore setup

Enable Anonymous sign-in in Firebase Authentication for Android and iOS. Garden backups and Friday attempts use the Firebase UID. The local Room profile ID stays on device. Existing garden backups and weekly attempts stored under local UUIDs require an administrator migration with a verified UID mapping. Local progress stays available after upgrading.

Deploy from the repository root after testing in a staging project.

```sh
firebase deploy --only firestore:rules --project YOUR_PROJECT_ID
```

Room still owns profile and daily progress. The optional `users/{uid}` cloud profile and garden backups are private. Friday attempts are private, one per Firebase UID per week, with one bounded score submission. The app currently computes scores and picks questions on the device, so scores and client-declared week IDs are untrusted. A trusted backend must verify gameplay before publishing leaderboard and Friday podium documents. Until that backend is connected, the app displays the published rankings and records new Friday attempts privately.

Publish only approved public display names and ranking fields in `leaderboard` and `quiz_podium`. Before rollout, copy approved legacy ranking fields from `quiz_scores` to `quiz_podium`. The legacy collection is denied to clients because it can contain attempt tokens. For sample game and shorts content, see [the seed script instructions](../scripts/README.md).

Quran Journey uploads only summary counts to the private `users/{uid}/quran_journeys/{profileId}` subcollection. Room retains the plan, Ayah progress, and bonus stars. A trusted publisher can include the approved `completedKhatmahs` count in public leaderboard entries.

Daily Gift spin claims use `users/{uid}/daily_reward_profiles/{profileId}/spins/{YYYY-MM-DD}`. The app creates each claim after checking local task completion. Deploy the rules above and enable Anonymous sign-in to sync claims and garden backups. The wheel, bundled games and riddle fallback work offline, and no seed data is required. Existing local spin records survive the Room upgrade. Wheel and game stars remain in the local Garden Shop balance. Public ranking changes still require the trusted publisher described above.
