package routes

import database.MoodTrackerDatabaseRepository
import dto.CreateEntryRequest
import dto.UpdateEntryRequest
import extension.toDto
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import model.EntryId
import model.UserId
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun Route.entryRoutes(repo: MoodTrackerDatabaseRepository) {

    route("/api/users/{userId}/entries") {
        // LIST
        get {
            val userId = call.parameters["userId"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid userId"))
            val list = repo.findAllEntries(UserId(userId)).map { it.toDto() }
            call.respond(list)
        }
        // CREATE
        post {
            val userId = call.parameters["userId"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid userId"))
            val req = call.receive<CreateEntryRequest>()
            if (req.title.isBlank() || req.content.isBlank())
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "title/content required"))
            if (req.moodRating != null && (req.moodRating !in 1..10))
                return@post call.respond(HttpStatusCode.BadRequest, mapOf("error" to "moodRating 1..10"))


            val created = repo.createEntry(
                model.Entry(
                    id = EntryId(0),
                    userId = UserId(userId),
                    title = req.title,
                    content = req.content,
                    moodRating = req.moodRating,
                    createdAt = Clock.System.now(),
                    updatedAt = null
                )
            )
            call.respond(HttpStatusCode.Created, created.toDto())
        }
    }

    route("/api/entries") {
        // READ by id
        get("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid id"))
            val entry = repo.findEntryById(EntryId(id))
                ?: return@get call.respond(HttpStatusCode.NotFound, mapOf("error" to "Entry $id not found"))
            call.respond(entry.toDto())
        }
        // UPDATE
        put("{id}") {
            val id = call.parameters["id"]?.toLongOrNull()
                ?: return@put call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Invalid id"))
            val req = call.receive<UpdateEntryRequest>()
            val existing = repo.findEntryById(EntryId(id))
                ?: return@put call.respond(HttpStatusCode.NotFound, mapOf("error" to "Entry $id not found"))

            val updated = repo.updateEntry(
                existing.copy(
                    title = req.title ?: existing.title,
                    content = req.content ?: existing.content,
                    moodRating = req.moodRating ?: existing.moodRating
                )
            )
            call.respond(updated.toDto())
        }
        // DELETE
        delete("{entryId}") {
            val entryId = call.parameters["entryId"]?.toLongOrNull()
                ?: return@delete call.respond(HttpStatusCode.BadRequest)

            val deleted = repo.deleteEntry(model.EntryId(entryId))
            if (deleted) call.respond(HttpStatusCode.NoContent)
            else call.respond(HttpStatusCode.NotFound)
        }


    }
}