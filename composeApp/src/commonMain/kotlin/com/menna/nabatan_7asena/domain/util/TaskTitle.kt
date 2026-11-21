package com.menna.nabatan_7asena.domain.util

class DuplicateDailyTaskException : IllegalArgumentException("Task already exists today")

fun normalizeTaskTitle(title: String): String {
    val canonical = buildString {
        title.lowercase().forEach { char ->
            when {
                char == 'ـ' || char in 'ؐ'..'ؚ' || char in 'ً'..'ٟ' || char == 'ٰ' || char in 'ۖ'..'ۭ' -> Unit
                char in "أإآٱ" -> append('ا')
                char == 'ى' -> append('ي')
                char == 'ة' -> append('ه')
                char.isWhitespace() || char == ' ' -> append(' ')
                char.isLetterOrDigit() -> append(char)
                else -> append(' ')
            }
        }
    }.trim().replace(Regex(" +"), " ")
    return when (canonical) {
        "سنه الفجر ركعتين", "سنه الفجر ركعتان" -> "سنه الفجر"
        else -> canonical
    }
}

fun requireNewDailyTask(title: String, existing: Iterable<String>) {
    val normalized = normalizeTaskTitle(title)
    require(normalized.isNotBlank()) { "Task title is empty" }
    if (existing.any { normalizeTaskTitle(it) == normalized }) throw DuplicateDailyTaskException()
}
