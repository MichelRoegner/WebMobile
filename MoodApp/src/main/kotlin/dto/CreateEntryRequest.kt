package dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateEntryRequest(
    val userId: Long,
    val title: String,
    val content: String,
    val moodRating: Int? = null
)