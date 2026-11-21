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

Firestore transactions reserve `quiz_attempts/{week}/entries/{user}` once, checking
legacy `quiz_scores/{week}/entries/{user}` results as well. Completion verifies the
reservation token and creates the official result only if no result exists. It
updates the reservation in the same transaction. Repeated submissions cannot
replace a completed score. Unfinished reservations do not appear in the winners
query because they are separate from `quiz_scores`.

The local versioned preference ledger is keyed by user/week. Completion is saved
locally before its network request. Failed/timed-out uploads remain queued and
retry when the quiz overview is visited, including after week rollover. Corrupt
local data blocks the official flow rather than silently unlocking another try.
An unknown server status/offline first start also fails closed; practice is still
available. Existing completed scores without the new fields remain locked.

The deployed Firestore policy must permit the app's reservation/result reads and
transactions. No authentication migration or permissive rule deployment is
included. Transactions enforce the rule for cooperating app clients; server-side
security policy must separately reject unauthorized/malicious writes.
