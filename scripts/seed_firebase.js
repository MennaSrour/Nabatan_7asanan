'use strict';

const { applicationDefault, initializeApp } = require('firebase-admin/app');
const { getFirestore } = require('firebase-admin/firestore');

const samples = {
  verse_builder_items: {
    ikhlas_1: {
      order: 1, surah: 'الإخلاص', ayahNumber: 1,
      verse: 'قُلْ هُوَ اللَّهُ أَحَدٌ',
      words: ['قُلْ', 'هُوَ', 'اللَّهُ', 'أَحَدٌ'],
    },
    kawthar_1: {
      order: 2, surah: 'الكوثر', ayahNumber: 1,
      verse: 'إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ',
      words: ['إِنَّا', 'أَعْطَيْنَاكَ', 'الْكَوْثَرَ'],
    },
  },
  speed_challenge_questions: {
    prayers: {
      order: 1, text: 'كم صلاة مفروضة نصلي كل يوم؟',
      choices: ['ثلاث صلوات', 'خمس صلوات', 'سبع صلوات'], correctIndex: 1,
    },
    ramadan: {
      order: 2, text: 'في أي شهر يصوم المسلمون؟',
      choices: ['رمضان', 'شوال', 'محرم'], correctIndex: 0,
    },
  },
  kids_shorts: {
    smile: {
      order: 1, title: 'ابتسامة صغيرة',
      description: 'ابتسامتك صدقة! جرّب أن تبتسم وتلقي السلام على أهلك اليوم.',
    },
    water: {
      order: 2, title: 'كل قطرة مهمة',
      description: 'أغلق الصنبور بعد الوضوء. هكذا نحافظ على نعمة الماء!',
    },
  },
};

async function seed() {
  const projectId = process.argv[2];
  if (!projectId || process.argv.length !== 3) {
    throw new Error('Usage: npm run seed -- <firebase-project-id>');
  }

  const app = initializeApp({
    projectId,
    ...(process.env.FIRESTORE_EMULATOR_HOST ? {} : { credential: applicationDefault() }),
  });
  const firestore = getFirestore(app);
  const batch = firestore.batch();
  let count = 0;
  for (const [collection, documents] of Object.entries(samples)) {
    for (const [id, document] of Object.entries(documents)) {
      batch.set(firestore.collection(collection).doc(`sample_${id}`), document, { merge: true });
      count++;
    }
  }
  await batch.commit();
  console.log(`Seeded ${count} sample documents in ${projectId}.`);
}

seed().catch((error) => {
  console.error(error.message);
  process.exitCode = 1;
});
