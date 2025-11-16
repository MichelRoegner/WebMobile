package model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@JvmInline
value class EntryId(val value: Long)

data class Entry @OptIn(ExperimentalTime::class) constructor(
    val id: EntryId,
    val userId: UserId,
    val title: String,
    val content: String,
    val moodRating: Int?,   // 1..10 or null
    val createdAt: Instant,
    val updatedAt: Instant? = null,
    val tags: Set<String> = emptySet(),
) {
    val wordCount: Int
        get() = content.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size

    val hasGoodMood: Boolean get() = (moodRating ?: 0) >= 7
    val hasPoorMood: Boolean get() = (moodRating ?: 11) <= 3
    @OptIn(ExperimentalTime::class)
    val isEdited: Boolean get() = updatedAt != null

    @OptIn(ExperimentalTime::class)
    val createdDate: LocalDate
        get() = createdAt.toLocalDateTime(TimeZone.UTC).date

    @OptIn(ExperimentalTime::class)
    fun updateContent(newContent: String): Entry =
        copy(
            content = newContent,
            updatedAt = Clock.System.now()
        )

    @OptIn(ExperimentalTime::class)
    fun updateMood(newRating: Int): Entry {
        require(newRating in 1..10) { "Mood rating must be between 1 and 10" }
        return copy(moodRating = newRating, updatedAt = Clock.System.now())
    }

    fun similarity(other: Entry): Double {
        if (tags.isEmpty() && other.tags.isEmpty()) return 1.0
        val inter = tags.intersect(other.tags).size.toDouble()
        val uni = tags.union(other.tags).size.toDouble()
        return if (uni == 0.0) 0.0 else inter / uni
    }

    operator fun plus(other: Entry): Int {
        val a = this.moodRating
        val b = other.moodRating
        require(a != null && b != null) { "Both entries must have a moodRating to compute the average" }
        return ((a + b) / 2.0).toInt()
    }
}
