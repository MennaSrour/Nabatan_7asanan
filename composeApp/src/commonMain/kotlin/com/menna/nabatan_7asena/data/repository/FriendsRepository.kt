@file:OptIn(kotlin.time.ExperimentalTime::class, kotlin.uuid.ExperimentalUuidApi::class)

package com.menna.nabatan_7asena.data.repository

import com.menna.nabatan_7asena.data.remote.FirebaseAvailability
import com.menna.nabatan_7asena.data.remote.dto.UserRankDto
import com.menna.nabatan_7asena.domain.entity.LeaderboardEntry
import com.menna.nabatan_7asena.domain.entity.PresetMessage
import com.menna.nabatan_7asena.domain.entity.User
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.Direction
import dev.gitlive.firebase.firestore.BaseTimestamp
import dev.gitlive.firebase.firestore.Source
import dev.gitlive.firebase.firestore.Timestamp
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
internal data class FriendProfile(
    val profileId: String,
    val friendCode: String,
    val avatarIndex: Int,
    val gender: String
) {
    val avatarGender get() = if (gender == "GIRL") User.Gender.GIRL else User.Gender.BOY
}

internal data class FriendsAccount(val uid: String, val profile: FriendProfile)

@Serializable
internal data class Friendship(
    val senderId: String,
    val recipientId: String,
    val senderCode: String,
    val recipientCode: String,
    val participants: List<String>,
    val status: String
) {
    val id get() = "${senderId}:${recipientId}"
    fun peer(uid: String) = if (senderId == uid) recipientId else senderId
    fun peerCode(uid: String) = if (senderId == uid) recipientCode else senderCode
}

@Serializable
private data class MessageWrite(val presetId: String, val senderId: String, val timestamp: BaseTimestamp)
@Serializable
private data class CooldownWrite(val messageId: String, val timestamp: BaseTimestamp)
@Serializable
private data class CreditWrite(val chatId: String, val senderId: String, val timestamp: BaseTimestamp)

internal data class ChatMessage(val id: String, val preset: PresetMessage, val senderId: String, val time: Timestamp)
internal class FriendsActionError(message: String) : Exception(message)

internal class FriendsRepository {
    private val sessionMutex = Mutex()
    private val firestore get() = Firebase.firestore
    private val profiles get() = firestore.collection("friendProfiles")
    private val codes get() = firestore.collection("friendCodes")
    private val friendships get() = firestore.collection("friendships")

    suspend fun account(user: User): FriendsAccount = sessionMutex.withLock {
        check(FirebaseAvailability.isConfigured)
        val auth = Firebase.auth
        var uid = auth.currentUser?.uid ?: requireNotNull(auth.signInAnonymously().user).uid
        var profile = profiles.document(uid).get(Source.SERVER).takeIf { it.exists }?.data<FriendProfile>()
        if (profile != null && profile.profileId != user.id) {
            auth.signOut()
            uid = requireNotNull(auth.signInAnonymously().user).uid
            profile = null
        }
        val saved = profile ?: reserveCode(uid, user)
        val updated = saved.copy(avatarIndex = user.avatarIndex, gender = user.gender.name)
        if (updated != saved) profiles.document(uid).set(updated)
        FriendsAccount(uid, updated)
    }

    private suspend fun reserveCode(uid: String, user: User): FriendProfile {
        val profile = profiles.document(uid)
        val alphabet = "23456789ABCDEFGHJKLMNPQRSTUVWXYZ"
        repeat(8) {
            val candidate = "NB-" + Uuid.random().toByteArray().takeLast(8).joinToString("") {
                alphabet[it.toInt() and 31].toString()
            }
            val code = codes.document(candidate)
            val reserved = firestore.runTransaction {
                val existing = get(profile)
                if (existing.exists) existing.data<FriendProfile>()
                else if (get(code).exists) null
                else {
                    val fresh = FriendProfile(user.id, candidate, user.avatarIndex, user.gender.name)
                    set(code, mapOf("uid" to uid))
                    set(profile, fresh)
                    fresh
                }
            }
            if (reserved != null) return reserved
        }
        error("Unable to reserve a friend code")
    }

    fun relationships(uid: String): Flow<List<Friendship>> =
        friendships.where { "participants" contains uid }.snapshots.map { snapshot ->
            snapshot.documents.map { it.data<Friendship>() }
        }

    suspend fun request(account: FriendsAccount, raw: String) {
        val code = raw.trim().uppercase()
        if (!Regex("NB-[2-9A-HJ-NP-Z]{8}").matches(code)) throw FriendsActionError("تحقق من كود الصداقة")
        if (code == account.profile.friendCode) throw FriendsActionError("هذا كود الصداقة الخاص بك")
        val recipient = codes.document(code).get(Source.SERVER)
        if (!recipient.exists) throw FriendsActionError("لم نجد صديقاً بهذا الكود")
        val peer = recipient.get<String>("uid")
        val relationship = Friendship(
            account.uid, peer, account.profile.friendCode, code, listOf(account.uid, peer), "pending"
        )
        firestore.runTransaction {
            val forward = friendships.document(relationship.id)
            val reverse = friendships.document("${peer}:${account.uid}")
            if (get(forward).exists || get(reverse).exists) {
                throw FriendsActionError("هذا الصديق موجود أو لديه طلب صداقة بالفعل")
            }
            set(forward, relationship)
        }
    }

    suspend fun respond(relationship: Friendship, accept: Boolean) {
        val reference = friendships.document(relationship.id)
        reference.update("status" to if (accept) "accepted" else "declined")
    }

    suspend fun peerProfile(uid: String): FriendProfile? =
        profiles.document(uid).get().takeIf { it.exists }?.data()

    suspend fun friendsRanking(account: FriendsAccount, connections: List<Friendship>, user: User): List<LeaderboardEntry> {
        val entries = connections.filter { it.status == "accepted" }.mapNotNull { relationship ->
            val profile = peerProfile(relationship.peer(account.uid)) ?: return@mapNotNull null
            val doc = firestore.collection("leaderboard").document(profile.profileId).get()
            val score = if (doc.exists) doc.data<UserRankDto>() else UserRankDto()
            LeaderboardEntry(profile.profileId, "صديق ${profile.friendCode}", profile.avatarGender,
                score.totalStars, score.quranPartsFinished, 0, profile.avatarIndex)
        } + LeaderboardEntry(user.id, user.name, user.gender, user.totalStars, user.quranPartsFinished, 0, user.avatarIndex)
        return entries.distinctBy { it.userId }.sortedWith(compareByDescending<LeaderboardEntry> { it.totalStars }.thenBy { it.userId })
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
    }

    private fun messages(chatId: String) = firestore.collection("chats").document(chatId).collection("messages")

    fun messagesFor(chatId: String): Flow<List<ChatMessage>> =
        messages(chatId).orderBy("timestamp", Direction.DESCENDING).limit(100).snapshots.map { snapshot ->
            snapshot.documents.mapNotNull { doc ->
                val preset = PresetMessage.entries.firstOrNull { it.id == doc.get<String>("presetId") }
                    ?: return@mapNotNull null
                val time = doc.get<Timestamp?>("timestamp") ?: return@mapNotNull null
                ChatMessage(doc.id, preset, doc.get("senderId"), time)
            }.reversed()
        }

    suspend fun send(account: FriendsAccount, relationship: Friendship, preset: PresetMessage) {
        val message = messages(relationship.id).document
        val payload = MessageWrite(preset.id, account.uid, Timestamp.ServerTimestamp)
        if (preset != PresetMessage.WATER) {
            message.set(payload)
            return
        }
        val recipient = relationship.peer(account.uid)
        val cooldown = firestore.collection("chats").document(relationship.id).collection("giftCooldowns").document(account.uid)
        val credit = giftCollection(recipient).document(message.id)
        firestore.runTransaction {
            val lastGift = get(cooldown)
            // The server repeats this check against request.time for every gift.
            if (lastGift.exists && utcDate(lastGift.get("timestamp")) == utcDate(Timestamp.now())) {
                throw FriendsActionError("أرسلت قطرة الماء لهذا الصديق اليوم")
            }
            set(message, payload)
            set(cooldown, CooldownWrite(message.id, Timestamp.ServerTimestamp))
            set(credit, CreditWrite(relationship.id, account.uid, Timestamp.ServerTimestamp))
        }
    }

    fun gifts(uid: String): Flow<Set<String>> =
        giftCollection(uid).snapshots.map { snapshot -> snapshot.documents.mapTo(mutableSetOf()) { it.id } }

    private fun giftCollection(uid: String) = firestore.collection("friendGardenGifts").document(uid).collection("credits")

    private fun utcDate(timestamp: Timestamp) =
        Instant.fromEpochSeconds(timestamp.seconds, timestamp.nanoseconds.toLong()).toLocalDateTime(TimeZone.UTC).date
}
