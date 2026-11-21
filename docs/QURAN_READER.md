# Quran reader and playlists

The Quran reader supports Cards and Mushaf modes. The shared `QuranLibraryStore`
persists the selected mode and ordered custom Surah playlists as a single versioned
JSON preference value using the existing Android SharedPreferences/iOS NSUserDefaults
platform abstraction. One app-scoped store is shared by Quran ViewModel instances.
Playlist edits publish only after the storage write succeeds. Invalid stored data
is reported without overwriting the original payload.

The bundled Hafs data contains 6,236 Ayahs, each with a valid page number, spanning
all 604 pages. The Mushaf groups by those page numbers; no inferred page table is
added. Surah/Juz/bookmark selection navigates using the actual Ayah/page metadata.
Reader position has one persistence owner and survives switching view modes.

## Quran font and text integrity

The font is **Scheherazade New Regular**, from SIL International, distributed under
SIL Open Font License 1.1. Its unmodified binary is bundled at
`composeApp/src/commonMain/composeResources/font/scheherazade_new_regular.ttf`.

- Source: https://github.com/google/fonts/tree/main/ofl/scheherazadenew
- License: `composeApp/src/commonMain/composeResources/files/licenses/scheherazade_new_OFL.txt`
- SHA-256: `794bac8dc9e83d1d620bc471ea694f5f31d0965ce8006490a79dfc51a2d283b3`

Each source Ayah ends in a font-specific number glyph (`FC00 + Ayah number - 1`)
and a literal `\xa0` separator. The decoder replaces only that known terminal
encoding. Quranic letters, disconnected letters, diacritics and pause marks are
preserved. The UI adds standard ornate Arabic-Indic end markers such as `﴿٢٨٦﴾`.

All source text and the new markers passed font character-coverage checks. HarfBuzz
shaping of all 6,236 decoded Ayahs produced zero missing glyphs. At a 2,048-unit em,
observed ink extended to +3,758/-2,148 units, Cards retain generous 80sp line height for 24sp text. Mushaf pages independently
measure their text, banners and Basmalah against the available body height, choosing
the largest fitting size between 26sp and 14sp with 1.8× line spacing. A 13sp safety size with
1.7× spacing is tried before enabling a scrolling accessibility
fallback. First/last-line gutters protect Quranic marks; no Ayahs are ellipsized.
Device checks remain necessary for diacritic appearance and accessibility scales.

Mushaf Surah starts, including mid-page starts, receive lavender/gold banners and
centered Basmalah lines (except Tawbah; Fatiha uses its existing first Ayah).
Download, repeat and bookmark actions share the top row. The page footer reserves
clearance for the floating navigation bar and system navigation inset, subtracting
any clearance already supplied by the playlist player.

## Playback ownership

One `QuranPlaybackController` owns single-Ayah, memorization and playlist sessions.
A playlist traverses all Ayahs in each chosen Surah, then advances to the next
Surah. Play/pause, previous, next and whole-playlist repeat operate on that session.
The existing URL resolver still prefers downloaded audio. Stale player callbacks
cannot advance a replaced/stopped session. Starting memorization or selecting a
different Ayah takes over playback. An active playlist uses an immutable queue
snapshot; edits apply to subsequent starts, while deleting the active playlist stops
it. Playlist metadata, view preference and Quran text work offline.

**Existing platform limitation:** the repository's iOS Quran audio and download
adapters still report unsupported operations. Shared playlist sequencing and local
storage are implemented for both targets, but audible playback/downloads on iOS
require those native adapters. This change preserves that explicit failure behavior
rather than reporting false playback success. Android retains its working player
and download implementations.
