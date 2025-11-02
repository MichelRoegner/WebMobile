package dto

import kotlinx.serialization.Serializable
import model.User

@Serializable
data class UserDto(
    val id: Long,
    val username: String,
    val email: String,
    val registrationDate: String,
    val isActive: Boolean
) {
    companion object {
        fun fromModel(u: User) = UserDto(
            id = u.id.value,
            username = u.username,
            email = u.email,
            registrationDate = u.registrationDate.toString(),
            isActive = u.isActive
        )
    }
}