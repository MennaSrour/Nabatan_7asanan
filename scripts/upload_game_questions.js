'use strict';

// Use stable ids when editing questions so re-uploading updates the same documents.
const trivia = [
  { id: 'five-prayers', q: 'كم صلاة مفروضة في اليوم؟', a: 'خمس صلوات', opts: 'ثلاث صلوات,خمس صلوات,سبع صلوات' },
  { id: 'fasting-month', q: 'في أي شهر يصوم المسلمون؟', a: 'رمضان', opts: 'شعبان,شوال,رمضان' },
  { id: 'qiblah', q: 'إلى أين نتجه عند الصلاة؟', a: 'نحو الكعبة', opts: 'نحو الكعبة,نحو المدرسة,نحو البحر' },
  { id: 'first-surah', q: 'ما أول سورة في المصحف؟', a: 'الفاتحة', opts: 'الفلق,الفاتحة,العصر' },
  { id: 'last-surah', q: 'ما آخر سورة في المصحف؟', a: 'الناس', opts: 'الكوثر,الإخلاص,الناس' },
  { id: 'fajr-rakah', q: 'كم ركعة في صلاة الفجر المفروضة؟', a: 'ركعتان', opts: 'ركعتان,ثلاث ركعات,أربع ركعات' },
  { id: 'maghrib-rakah', q: 'كم ركعة في صلاة المغرب المفروضة؟', a: 'ثلاث ركعات', opts: 'أربع ركعات,ركعتان,ثلاث ركعات' },
  { id: 'isha-rakah', q: 'كم ركعة في صلاة العشاء المفروضة؟', a: 'أربع ركعات', opts: 'ثلاث ركعات,أربع ركعات,ركعتان' },
  { id: 'start-meal', q: 'ماذا نقول قبل أن نبدأ الأكل؟', a: 'بسم الله', opts: 'بسم الله,الحمد لله,إلى اللقاء' },
  { id: 'after-meal', q: 'ماذا نقول شكرًا لله بعد الأكل؟', a: 'الحمد لله', opts: 'السلام عليكم,الحمد لله,صباح الخير' },
  { id: 'greeting', q: 'بماذا نحيّي من نلقاه؟', a: 'السلام عليكم', opts: 'أستغفر الله,تصبح على خير,السلام عليكم' },
  { id: 'greeting-reply', q: 'ماذا نرد على من قال السلام عليكم؟', a: 'وعليكم السلام', opts: 'وعليكم السلام,إلى اللقاء,بسم الله' },
  { id: 'kaaba-city', q: 'في أي مدينة تقع الكعبة؟', a: 'مكة', opts: 'المدينة,مكة,القدس' },
  { id: 'prophets-mosque', q: 'في أي مدينة يقع المسجد النبوي؟', a: 'المدينة', opts: 'مكة,القدس,المدينة' },
  { id: 'quran-prophet', q: 'على أي نبي أُنزل القرآن؟', a: 'محمد ﷺ', opts: 'محمد ﷺ,موسى عليه السلام,عيسى عليه السلام' },
  { id: 'last-prophet', q: 'من خاتم الأنبياء؟', a: 'محمد ﷺ', opts: 'آدم عليه السلام,محمد ﷺ,إبراهيم عليه السلام' },
  { id: 'ark', q: 'أي نبي بنى السفينة؟', a: 'نوح عليه السلام', opts: 'يوسف عليه السلام,داود عليه السلام,نوح عليه السلام' },
  { id: 'whale', q: 'أي نبي التقمه الحوت؟', a: 'يونس عليه السلام', opts: 'يونس عليه السلام,نوح عليه السلام,موسى عليه السلام' },
  { id: 'moses-staff', q: 'أي نبي ألقى عصاه فأصبحت حيّة بإذن الله؟', a: 'موسى عليه السلام', opts: 'إبراهيم عليه السلام,موسى عليه السلام,يوسف عليه السلام' },
  { id: 'first-human', q: 'من أول إنسان خلقه الله؟', a: 'آدم عليه السلام', opts: 'نوح عليه السلام,إسماعيل عليه السلام,آدم عليه السلام' },
  { id: 'pillars', q: 'كم عدد أركان الإسلام؟', a: 'خمسة', opts: 'خمسة,ستة,أربعة' },
  { id: 'faith-pillars', q: 'كم عدد أركان الإيمان؟', a: 'ستة', opts: 'خمسة,ستة,سبعة' },
  { id: 'eid-fitr', q: 'ما العيد الذي يأتي بعد شهر رمضان؟', a: 'عيد الفطر', opts: 'عيد الأضحى,يوم الجمعة,عيد الفطر' },
  { id: 'hijri-first', q: 'ما أول شهر في السنة الهجرية؟', a: 'المحرّم', opts: 'المحرّم,رمضان,شوال' },
  { id: 'hijri-months', q: 'كم شهرًا في السنة الهجرية؟', a: 'اثنا عشر', opts: 'عشرة,اثنا عشر,أربعة عشر' },
  { id: 'azan', q: 'ما اسم النداء الذي يُعلن دخول وقت الصلاة؟', a: 'الأذان', opts: 'الدعاء,التحية,الأذان' },
  { id: 'wudu', q: 'ماذا نفعل استعدادًا للصلاة إذا احتجنا إلى الطهارة؟', a: 'نتوضأ', opts: 'نتوضأ,نتناول الطعام,نلعب' },
  { id: 'trust', q: 'ما التصرف الذي يدل على الأمانة؟', a: 'إعادة الشيء لصاحبه', opts: 'إخفاء الشيء,إعادة الشيء لصاحبه,الاحتفاظ به دون إذن' },
  { id: 'mercy', q: 'أي عمل يدل على الرحمة بالحيوان؟', a: 'وضع ماء للطيور', opts: 'إخافة الطيور,إبعاد الماء عنها,وضع ماء للطيور' },
  { id: 'honesty', q: 'ماذا أفعل إن أخطأت في حق صديقي؟', a: 'أعتذر وأصلح خطئي', opts: 'أعتذر وأصلح خطئي,ألقي اللوم عليه,أخفي ما حدث' },
  { id: 'masad', q: 'أيّ سورة يبدأ اسمها بحرف الميم؟', a: 'المسد', opts: 'الفلق,المسد,الناس' },
  { id: 'al-asr', q: 'أي سورة تبدأ بقول الله تعالى: والعصر؟', a: 'العصر', opts: 'النصر,الكوثر,العصر' },
];

const riddle = [
  { id: 'prayer-call', q: 'تسمعني عند دخول وقت الصلاة، فماذا أكون؟', a: 'الأذان', opts: 'الأذان,صوت المطر,صوت الريح' },
  { id: 'night-prayer', q: 'أنا صلاة مفروضة تأتي بعد المغرب. من أنا؟', a: 'العشاء', opts: 'العصر,العشاء,الفجر' },
  { id: 'morning-prayer', q: 'أنا صلاة مفروضة وقتي قبل شروق الشمس. من أنا؟', a: 'الفجر', opts: 'العشاء,الظهر,الفجر' },
  { id: 'friday', q: 'أنا يوم يجتمع فيه المسلمون لصلاة الجمعة. من أنا؟', a: 'الجمعة', opts: 'الجمعة,الخميس,السبت' },
  { id: 'muharram', q: 'أنا الشهر الأول من السنة الهجرية. ما اسمي؟', a: 'المحرّم', opts: 'ربيع الأول,المحرّم,شوّال' },
  { id: 'good-deed', q: 'أنفع المحتاج عندما أعطيه من مالي ابتغاء الأجر. ما هذا العمل؟', a: 'الصدقة', opts: 'البيع,اللعب,الصدقة' },
  { id: 'house-of-prayer', q: 'أنا مكان يجتمع فيه الناس للصلاة. ما اسمي؟', a: 'المسجد', opts: 'المسجد,المكتبة,الملعب' },
  { id: 'opening', q: 'أنا سورة يبدأ بها المصحف وتقرأ في الصلاة. من أنا؟', a: 'الفاتحة', opts: 'الفلق,الفاتحة,النصر' },
  { id: 'three-verses', q: 'أبدأ بقول الله تعالى: إنا أعطيناك الكوثر. ما اسمي؟', a: 'الكوثر', opts: 'العصر,الفيل,الكوثر' },
  { id: 'eid', q: 'أجيء بعد رمضان، وفي صباحي يصلّي المسلمون صلاة العيد. من أنا؟', a: 'عيد الفطر', opts: 'عيد الفطر,عيد الأضحى,ليلة الجمعة' },
  { id: 'whale-prophet', q: 'دعا ربّه وهو في بطن الحوت. من هو؟', a: 'يونس عليه السلام', opts: 'نوح عليه السلام,يونس عليه السلام,يوسف عليه السلام' },
  { id: 'before-prayer', q: 'أغسل وجهي ويديّ في هذا العمل استعدادًا للصلاة. ما اسمه؟', a: 'الوضوء', opts: 'الصيام,الأذان,الوضوء' },
];

const verse = [
  { id: 'ikhlas-1', reference: 'الإخلاص · ١', text: 'قُلْ هُوَ اللَّهُ أَحَدٌ' },
  { id: 'ikhlas-3', reference: 'الإخلاص · ٣', text: 'لَمْ يَلِدْ وَلَمْ يُولَدْ' },
  { id: 'ikhlas-4', reference: 'الإخلاص · ٤', text: 'وَلَمْ يَكُنْ لَهُ كُفُوًا أَحَدٌ' },
  { id: 'falaq-1', reference: 'الفلق · ١', text: 'قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ' },
  { id: 'falaq-2', reference: 'الفلق · ٢', text: 'مِنْ شَرِّ مَا خَلَقَ' },
  { id: 'nas-1', reference: 'الناس · ١', text: 'قُلْ أَعُوذُ بِرَبِّ النَّاسِ' },
  { id: 'nas-4', reference: 'الناس · ٤', text: 'مِنْ شَرِّ الْوَسْوَاسِ الْخَنَّاسِ' },
  { id: 'kawthar-1', reference: 'الكوثر · ١', text: 'إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ' },
  { id: 'kawthar-2', reference: 'الكوثر · ٢', text: 'فَصَلِّ لِرَبِّكَ وَانْحَرْ' },
  { id: 'kawthar-3', reference: 'الكوثر · ٣', text: 'إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ' },
  { id: 'nasr-1', reference: 'النصر · ١', text: 'إِذَا جَاءَ نَصْرُ اللَّهِ وَالْفَتْحُ' },
  { id: 'asr-2', reference: 'العصر · ٢', text: 'إِنَّ الْإِنْسَانَ لَفِي خُسْرٍ' },
  { id: 'fatiha-2', reference: 'الفاتحة · ٢', text: 'الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ' },
  { id: 'fatiha-4', reference: 'الفاتحة · ٤', text: 'مَالِكِ يَوْمِ الدِّينِ' },
  { id: 'fatiha-5', reference: 'الفاتحة · ٥', text: 'إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ' },
  { id: 'fatiha-6', reference: 'الفاتحة · ٦', text: 'اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ' },
];

function documents() {
  const paths = new Set();
  return Object.entries({ trivia, riddle, verse }).flatMap(([game, entries]) => entries.map(entry => {
    if (!/^[a-z0-9-]+$/.test(entry.id)) throw new Error(`Invalid id: ${entry.id}`);
    const path = `game_questions/${game}/items/${entry.id}`;
    if (paths.has(path)) throw new Error(`Duplicate id: ${path}`);
    paths.add(path);
    if (game === 'verse') {
      const reference = entry.reference.trim();
      const words = entry.text.trim().split(/\s+/);
      if (!reference || words.length < 3 || words.length > 12 || new Set(words).size < 2) {
        throw new Error(`Invalid verse at ${path}`);
      }
      return { path, data: { reference, words } };
    }
    const question = entry.q.trim();
    const choices = entry.opts.split(',').map(option => option.trim());
    const correctIndex = choices.indexOf(entry.a.trim());
    if (!question || choices.length < 2 || choices.length > 4 ||
        choices.some(option => !option) || new Set(choices).size !== choices.length || correctIndex < 0) {
      throw new Error(`Invalid question at ${path}`);
    }
    return { path, data: { question, choices, correctIndex } };
  }));
}

async function main() {
  const args = process.argv.slice(2);
  const rows = documents();
  if (args.length === 1 && args[0] === '--dry-run') {
    console.log(`Validated ${rows.length} documents (${trivia.length} trivia, ${riddle.length} riddles, ${verse.length} verses).`);
    return;
  }
  if (args.length !== 2 || args[0] !== '--project' || !/^[a-z][a-z0-9-]+$/.test(args[1])) {
    throw new Error('Usage: npm run upload:games -- --project PROJECT_ID (or --dry-run)');
  }
  const { initializeApp, applicationDefault } = require('firebase-admin/app');
  const { getFirestore } = require('firebase-admin/firestore');
  const db = getFirestore(initializeApp({ credential: applicationDefault(), projectId: args[1] }));
  for (let start = 0; start < rows.length; start += 400) {
    const batch = db.batch();
    for (const { path, data } of rows.slice(start, start + 400)) batch.set(db.doc(path), data);
    await batch.commit();
  }
  console.log(`Uploaded ${rows.length} documents to ${args[1]}.`);
}

main().catch(error => {
  console.error(error.message);
  process.exitCode = 1;
});
