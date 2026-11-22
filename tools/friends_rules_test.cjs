const { readFileSync } = require('node:fs')
const { join } = require('node:path')
const { test } = require('node:test')
const { assertFails, assertSucceeds, initializeTestEnvironment } = require('@firebase/rules-unit-testing')
const {
  arrayUnion, collection, doc, getDoc, getDocs, query, runTransaction,
  serverTimestamp, setDoc, updateDoc, where, writeBatch
} = require('firebase/firestore')

test('friend codes, consent, preset-only access and atomic daily gifts', async () => {
  const environment = await initializeTestEnvironment({
    projectId: 'demo-nabata-friends',
    firestore: { rules: readFileSync(join(__dirname, '..', 'firestore.rules'), 'utf8') }
  })
  try {
    await environment.clearFirestore()
    const alice = environment.authenticatedContext('alice').firestore()
    const bob = environment.authenticatedContext('bob').firestore()
    const other = environment.authenticatedContext('other').firestore()
    const guest = environment.unauthenticatedContext().firestore()
    const aliceCode = 'NB-23456789'
    const bobCode = 'NB-ABCDEFGH'

    function reserve(db, uid, code) {
      return runTransaction(db, async (transaction) => {
        const profile = doc(db, 'friendProfiles', uid)
        const index = doc(db, 'friendCodes', code)
        await transaction.get(profile)
        await transaction.get(index)
        transaction.set(profile, { profileId: uid, friendCode: code, avatarIndex: 0, gender: 'BOY' })
        transaction.set(index, { uid })
      })
    }
    await assertSucceeds(reserve(alice, 'alice', aliceCode))
    await assertSucceeds(reserve(bob, 'bob', bobCode))
    await assertFails(reserve(other, 'other', bobCode))
    await assertFails(getDocs(collection(alice, 'friendCodes')))
    await assertFails(getDoc(doc(guest, 'friendCodes', bobCode)))
    await assertSucceeds(getDoc(doc(alice, 'friendCodes', bobCode)))
    await assertFails(getDoc(doc(alice, 'friendProfiles', 'bob')))

    const relationship = {
      senderId: 'alice', recipientId: 'bob', senderCode: aliceCode, recipientCode: bobCode,
      participants: ['alice', 'bob'], status: 'pending'
    }
    const friendship = (db) => doc(db, 'friendships', 'alice:bob')
    const messages = (db) => collection(db, 'chats', 'alice:bob', 'messages')
    const valid = () => ({ presetId: 'peace', senderId: 'alice', timestamp: serverTimestamp() })
    await assertSucceeds(runTransaction(alice, async (transaction) => {
      await transaction.get(friendship(alice))
      await transaction.get(doc(alice, 'friendships', 'bob:alice'))
      transaction.set(friendship(alice), relationship)
    }))
    await assertSucceeds(getDocs(query(collection(bob, 'friendships'), where('participants', 'array-contains', 'bob'))))
    await assertFails(getDoc(friendship(other)))
    await assertFails(getDocs(collection(bob, 'friendships')))
    await assertFails(setDoc(doc(bob, 'friendships', 'bob:alice'), {
      ...relationship, senderId: 'bob', recipientId: 'alice', senderCode: bobCode,
      recipientCode: aliceCode, participants: ['bob', 'alice']
    }))
    await assertFails(updateDoc(friendship(alice), { status: 'accepted' }))
    await assertFails(updateDoc(friendship(bob), { participants: arrayUnion('other'), status: 'accepted' }))
    await assertFails(setDoc(doc(messages(alice)), valid()))
    await assertSucceeds(updateDoc(friendship(bob), { status: 'accepted' }))
    await assertSucceeds(getDoc(doc(alice, 'friendProfiles', 'bob')))
    await assertSucceeds(setDoc(doc(messages(alice)), valid()))
    await assertSucceeds(getDocs(messages(bob)))
    await assertFails(getDocs(messages(other)))
    await assertFails(setDoc(doc(messages(bob)), valid()))
    await assertFails(setDoc(doc(messages(alice)), { ...valid(), text: 'نص إضافي' }))
    await assertFails(setDoc(doc(messages(alice)), { ...valid(), presetId: 'unknown' }))
    await assertFails(setDoc(doc(messages(alice)), { ...valid(), presetId: 'water' }))

    function sendGift() {
      const message = doc(messages(alice))
      const batch = writeBatch(alice)
      batch.set(message, { ...valid(), presetId: 'water' })
      batch.set(doc(alice, 'chats', 'alice:bob', 'giftCooldowns', 'alice'), {
        messageId: message.id, timestamp: serverTimestamp()
      })
      batch.set(doc(alice, 'friendGardenGifts', 'bob', 'credits', message.id), {
        chatId: 'alice:bob', senderId: 'alice', timestamp: serverTimestamp()
      })
      return batch.commit()
    }

    await assertSucceeds(sendGift())
    await assertFails(sendGift())
    await assertFails(setDoc(doc(collection(alice, 'friendGardenGifts', 'bob', 'credits')), {
      chatId: 'alice:bob', senderId: 'alice', timestamp: serverTimestamp()
    }))
    const credits = await assertSucceeds(getDocs(collection(bob, 'friendGardenGifts', 'bob', 'credits')))
    if (credits.size !== 1) throw new Error('Expected one Garden credit')
    await assertFails(getDocs(collection(alice, 'friendGardenGifts', 'bob', 'credits')))
  } finally {
    await environment.cleanup()
  }
})
