# Good deeds garden

The Home garden is drawn entirely with shared Compose Canvas paths, gradients and
layers. Home contains a compact stage/drop preview that opens the dedicated full-screen garden route.
The garden shares the Home ViewModel owner, hides the floating navigation bar,
and uses the Home day/night state for sky, moon/stars and tree lighting. Seed, sprout,
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

## Garden shop

The Garden screen opens an Arabic RTL shop sheet with six Canvas decorations:
butterflies (20 stars), birds (25), extra flowers (10), lantern (30), rainbow (35)
and magical stars (15). All stages can equip them. Butterflies are now an equipped
shop decoration rather than an automatic maturity effect; ordinary stage blossoms,
meadow flowers and night-sky stars remain part of the base scene.

The spendable shop balance is `max(0, current achievement stars - spentStars)`.
Achievement/leaderboard stars remain unchanged by shopping. This preserves ranking
and existing task undo/award behavior. If an achievement is undone, the shop balance
can fall to zero; owned items are retained, and subsequent earnings count against
the same spent total. There is no negative balance or refund from toggling an item.

The repository reads the current matching Room profile's star total inside its
serialized purchase operation. Price, currency spent, ownership, automatic equip
and revision are committed together in the existing local JSON snapshot before
publishing. Repeated buys of an owned item are no-ops; insufficient funds, a changed
profile, or a failed save cannot charge/unlock it. Equip operations require ownership
and never cost stars. Pre-shop snapshots decode with empty ownership/equipment and
zero spending, preserving XP, water and reward ledgers.

Shop fields join the existing per-profile, per-device Firestore backup and pending
revision/retry mechanism. This remains a local game wallet with cloud backup, not a
shared multi-device transactional economy or cloud-restore service. Purchases do not
change leaderboard documents. The UI announces successful saved purchases with a
Canvas confetti banner; reopening the shop does not replay the celebration. Shop
idle animation stops outside the STARTED lifecycle state.
