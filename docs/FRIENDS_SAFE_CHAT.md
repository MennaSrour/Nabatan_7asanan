# أصحابي Firebase setup

Enable the Anonymous sign-in provider in Firebase Authentication for the project used by the Android and iOS hosts. The authenticated Firebase UID owns the social profile. The app keeps its existing local profile ID for the leaderboard and Garden.

The repository's `firestore.rules` includes the social rules alongside the other app collections. Review any deployed rules that differ from the repository before publishing. Recursive wildcard permissions can grant access even when another matching rule denies it.

The client reserves a friend code and its profile in the same transaction. Code lookups require an exact document ID and can never perform a list query. Declined requests stay declined to prevent the same pair from repeatedly sending requests. Chat access starts only after the recipient accepts.

Garden gifts use a UTC calendar day per sender and friend. The Firestore transaction creates the preset message and one recipient credit and advances the sender's cooldown together. The recipient's Garden applies credit IDs once in the same local storage write as the balance update, following the existing device-local Garden model.

The rules regression test uses the Firestore Emulator and the Node packages `firebase` and `@firebase/rules-unit-testing`. With the emulator running and `FIRESTORE_EMULATOR_HOST` set, run `node --test tools/friends_rules_test.cjs`.
