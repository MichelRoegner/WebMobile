package dto


import kotlinx.serialization.Serializable

@Serializable
data class UpdateEntryRequest(
    val title: String? = null,
    val content: String? = null,
    val moodRating: Int? = null
)