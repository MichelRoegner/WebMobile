package extension

import dto.EntryDto
import dto.UserDto
import model.Entry
import model.User
import kotlin.time.ExperimentalTime

fun User.toDto(): UserDto =
    UserDto(
        id = this.id.value,
        username = this.username,
        email = this.email,
        registrationDate = this.registrationDate.toString(), // ISO-8601
        isActive = this.isActive
    )
@OptIn(ExperimentalTime::class)
fun Entry.toDto() = EntryDto(
    id = id.value,
    userId = userId.value,
    title = title,
    content = content,
    moodRating = moodRating,
    createdAt = createdAt.toString(),
    updatedAt = updatedAt?.toString()
)