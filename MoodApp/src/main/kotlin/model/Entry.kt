package model


import java.time.LocalDate
import java.time.LocalDateTime

@JvmInline
value class EntryId(val value: Long)


data class Entry(
    val id: EntryId,
    val userId: UserId,
    val title: String,
    val content: String,
    val moodRating: Int?, // 1..10 or null
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime? = null,
    val tags: Set<String> = emptySet(),
) {
    val wordCount: Int get() = content.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size
    val hasGoodMood: Boolean get() = (moodRating ?: 0) >= 7
    val hasPoorMood: Boolean get() = (moodRating ?: 11) <= 3
    val isEdited: Boolean get() = updatedAt != null
    val createdDate: LocalDate get() = createdAt.toLocalDate()


    fun updateContent(newContent: String): Entry = copy(
        content = newContent,
        updatedAt = LocalDateTime.now()
    )


    fun updateMood(newRating: Int): Entry {
        require(newRating in 1..10) { "Mood rating must be between 1 and 10" }
        return copy(moodRating = newRating, updatedAt = LocalDateTime.now())
    }





    // -------- Optional 1: Ähnlichkeit (Jaccard-Index) --------
    fun similarity(other: Entry): Double {
        if (tags.isEmpty() && other.tags.isEmpty()) return 1.0
        val a = tags
        val b = other.tags
        val inter = a.intersect(b).size.toDouble()
        val uni = a.union(b).size.toDouble()
        return if (uni == 0.0) 0.0 else inter / uni
    }


    // -------- Optional 3: Operator Overloading --------
    operator fun plus(other: Entry): Int {
        val a = this.moodRating
        val b = other.moodRating
        require(a != null && b != null) { "Both entries must have a moodRating to compute the average" }
        return ((a + b) / 2.0).toInt()
    }
}