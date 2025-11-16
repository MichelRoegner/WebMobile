package routes

import database.MoodTrackerDatabaseRepository
import extension.toDto
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.User
import model.UserId
import java.time.LocalDate

@kotlinx.serialization.Serializable
data class CreateUserRequest(
    val username: String,
    val email: String,
    val passwordHash: String,
    val registrationDate: String? = null,
    val isActive: Boolean = true
)

fun Route.userRoutes(repository: MoodTrackerDatabaseRepository) {

    route("/api/users") {

        post {
            val req = call.receive<CreateUserRequest>()  // funktioniert nur mit ContentNegotiation
            val domainUser = User(
                id = UserId(0),
                username = req.username,
                email = req.email,
                passwordHash = req.passwordHash,
                registrationDate = req.registrationDate?.let(LocalDate::parse) ?: LocalDate.now(),
                isActive = req.isActive
            )
            val created = repository.createUser(domainUser)
            call.respond(HttpStatusCode.Created, created.toDto())
        }

        get("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid id"))

            val user = repository.findUserById(model.UserId(id))
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "User $id not found"))

            call.respond(user.toDto())
        }
    }
}

