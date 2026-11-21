# Official Friday challenge

Practice remains unlimited and does not reserve or submit an official attempt.
The official exam requires a saved profile, Friday in Africa/Cairo, known attempt
status, and a successful server reservation. Its entry hint explicitly explains
that **starting consumes the one official attempt even if the child exits**.
An interrupted/process-killed attempt remains recorded, rather than allowing a
new attempt. This change does not provide resumable unfinished exams.

The challenge identifier uses the existing Saturday-based `QuizWeek` ID. The
reserved user/week/token are captured at start and remain the submission target
even if the calendar week changes during the exam. Home/profile star scores and
practice results are separate from the official score.

Firestore transactions reserve `quiz_attempts/{week}/entries/{uid}` once per
Firebase user. Completion verifies the reservation token and updates the private
attempt once. A trusted backend can verify results and publish ranking entries
to `quiz_podium`. See [Firebase setup](FIREBASE_SETUP.md) for rollout and legacy
data migration.

The local versioned preference ledger is keyed by user/week. Completion is saved
locally before its network request. Failed/timed-out uploads remain queued and
retry when the quiz overview is visited, including after week rollover. Corrupt
local data blocks the official flow rather than silently unlocking another try.
An unknown server status/offline first start also fails closed; practice is still
available.
