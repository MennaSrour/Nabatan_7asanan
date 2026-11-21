# Good deeds garden

The Home garden is drawn entirely with shared Compose Canvas paths, gradients and
layers. The compact card expands into a scrollable garden dialog. Seed, sprout,
sapling, young tree, blooming tree and fruiting garden begin at 0, 2, 8, 18, 35 and
60 garden XP respectively. Stage is derived from persisted XP, so there is no
separate level value that can drift out of sync. The mature garden keeps growing
its XP without resetting or losing fruit.

One **persisted completed task** earns one water drop. Optimistic UI checkmarks
are excluded. The reward ledger records task IDs for the latest credited local
date: rechecking, reopening Home, or repeating the same snapshot cannot grant
another drop. The same task can earn again on a later day. Older-date snapshots
cannot replay rewards. Watering spends one drop for one XP; missed days neither
remove XP nor consume water. A gentle reminder appears when last watered before
today. This is garden XP, separate from the app's existing star score.

`GardenRepository` owns updates. A mutex serializes reward/watering changes; the
versioned per-profile JSON snapshot is written before publishing new state.
Android SharedPreferences and iOS NSUserDefaults use the existing platform
preferences abstraction. Unreadable/unsupported snapshots are preserved and
reported rather than replaced with an empty garden. A failed write cannot publish
growth or consume a drop. The watering animation is triggered only after a
successful applied watering operation, and does not replay merely on expansion.
Idle animation composition stops outside the STARTED lifecycle state.

Registered profiles also upload a **device-specific backup** to
`gardens/{profileId}/devices/{deviceId}` through GitLive Firestore. Device identity,
revision, XP, drops, ledger and last-watered date survive process restarts locally.
The backup uses a separate collection and cannot replace leaderboard/profile
fields or another device's garden. Acknowledgements mark only the revision that
was uploaded; newer local changes remain pending. Failures and seven-second
timeouts leave progress playable locally, with retries at one-minute intervals
while the Home ViewModel is alive. Guests never upload. Backup permissions must
be allowed by the project's deployed Firestore policy; this repository does not
contain that policy. No new Firebase authentication scheme is introduced.

This backup is not a cross-device merge or automatic cloud restore mechanism.
Device gardens remain independent, avoiding duplicate rewards or overwriting
another device's progress. No production Firestore writes were used for testing.

Validation covers stage thresholds, once-per-day rewards, non-punitive misses,
local restore, profile isolation, corrupt/failed persistence, concurrent watering,
backup failure/cancellation, device identity, and mutations during an upload.
Visual appearance, motion/accessibility settings and native iOS rendering still
need device review; common metadata and Android builds check shared API usage.
