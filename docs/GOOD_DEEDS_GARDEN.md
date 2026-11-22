# Good deeds garden world

Home shows a compact, Canvas-illustrated garden preview with planted-tree and water
counts. It opens a dedicated Arabic RTL garden route using the Home ViewModel and
app day/night theme. The horizontally pannable landscape starts with four plots.
Each 50-star expansion adds three plots, up to sixteen. Every plot supports one
tree and three decoration placements. Plot zero appears at the right; accessible
plot chips supplement Canvas hit targets. Drawing culls trees and decorations
outside the viewport, and idle animations stop outside the STARTED lifecycle state.

## Trees and watering

Palm, apple, olive and blossom trees are independent owned instances. Each begins
as a seed and advances at 0, 2, 8, 18, 35 and 60 XP through six visual stages.
Stage is derived from XP. Selecting a planted tree shows its individual progress;
watering spends one shared drop for one XP on that visible tree only. Moving,
hiding or returning a tree to inventory retains its identity and growth. Mature
trees retain subsequent XP. Legacy aggregate garden XP equals the sum across all
owned trees, including trees in inventory.

One persisted completed daily task earns one drop. Optimistic checkmarks are
excluded. The existing daily task-ID ledger prevents duplicate rewards from
rechecking or replayed snapshots; the same task may earn again on a later day.
Missed days never remove XP or water. Reminders use the selected tree's watering
date. Garden XP is separate from achievement stars.

## Repeatable shop and placement

The shop sells four tree species, eight decorations (butterflies, birds, flowers,
lantern, rainbow, stars, bench and fountain), and land expansions. Purchases add
copies to inventory; owned quantity badges do not impose a one-purchase limit.
Tree, decoration-stock and placed-decoration tabs support planting, placement,
moving, hiding and returning items to inventory. Hidden items still reserve their
plot/slot and owned copy; removing a placement frees it for reuse. Placement and
visibility changes cost nothing and do not refund stars.

Spendable balance is `max(0, achievement stars - spentStars)`. Shopping does not
reduce leaderboard scores. If an achievement is undone, balance can fall to zero;
owned items remain. The repository reads the matching profile's current star total
inside its serialized purchase operation. Costs, ownership, land, placements and
revision are saved atomically before state is published. Insufficient funds,
placement conflicts, expansion limits or failed writes cannot partially charge or
unlock items. Purchase celebration plays only after a saved purchase.

## Persistence and backup

GardenRepository serializes mutations with a mutex and writes the per-profile JSON
snapshot before publishing. Android SharedPreferences and iOS NSUserDefaults use
the existing platform preferences abstraction. Snapshot version 2 is explicitly
encoded under the existing preference key. Version-1 gardens migrate to one apple
tree with their existing XP and watering date; water, reward ledgers, spending and
owned decorations survive, and equipped decorations become placed instances.
Migration introduces no purchase charge. Unsupported or corrupt snapshots are
preserved and reported. Older clients reject the new snapshot version rather than
silently dropping world fields.

Registered profiles back up to `gardens/{profileId}/devices/{deviceId}` through
GitLive Firestore. Tree identities/species/XP/plots, decoration quantities and
placements, land expansions and the existing wallet/reward fields join that backup.
Exact-revision acknowledgements preserve newer pending changes. Failed uploads or
seven-second timeouts leave local play available; retries run every minute while
the Home ViewModel is alive. Guests never upload. Deployment must allow these
paths in the existing Firestore policy; that policy is not in this repository.

This is a device-specific backup, not a cross-device merge or automatic cloud
restore mechanism. It cannot replace leaderboard/profile data or another device's
garden. No production Firestore writes were used in verification.

## Verification

Unit coverage includes legacy migration, independent growth, inventory round trips,
quantity/slot validation, concurrent purchases and placements, expansion limits,
profile isolation, failed-write rollback and backup revisions. Android debug builds,
common Kotlin metadata and unit tests validate shared code. Native iOS rendering,
on-device visuals and live Firebase behavior still require device review.
