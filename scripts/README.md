# Firestore sample content

From the repository root, run the following with Node.js 20 or newer and Application Default Credentials for a principal with Firestore write access.

```sh
npm --prefix scripts install && npm --prefix scripts run seed -- YOUR_PROJECT_ID
```

Set `FIRESTORE_EMULATOR_HOST=127.0.0.1:8080` and use a demo project ID to seed a running emulator without credentials. The script commits all three collections together and uses fixed `sample_` IDs. Reruns update the sample fields and preserve other documents.
