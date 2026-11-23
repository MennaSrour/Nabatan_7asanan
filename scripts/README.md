# Firestore sample content

From the repository root, run the following with Node.js 20 or newer and Application Default Credentials for a principal with Firestore write access.

```sh
npm --prefix scripts install && npm --prefix scripts run seed -- YOUR_PROJECT_ID
```

Set `FIRESTORE_EMULATOR_HOST=127.0.0.1:8080` and use a demo project ID to seed a running emulator without credentials. The script commits all three collections together and uses fixed `sample_` IDs. Reruns update the sample fields and preserve other documents.

## Wheel game content

Edit the starter arrays in `upload_game_questions.js`. Multiple-choice entries
use the existing `q`, `a` and comma-separated `opts` format with a stable `id`.
Verse entries use a reference and the verse text in word order. Re-uploading
updates matching IDs and leaves other documents in place.

```sh
cd scripts
npm install
npm run upload:games -- --dry-run
npm run upload:games -- --project YOUR_PROJECT_ID
```

The dry run validates the starter batch without Firebase access. For uploads,
provide Firebase Admin Application Default Credentials, for example by pointing
`GOOGLE_APPLICATION_CREDENTIALS` to a service account key outside this repository.
Deploy the updated Firestore rules before clients read the curated
`game_questions/{trivia|riddle|verse}/items` pools. The app retains its last
validated pool locally and ships an offline fallback.
