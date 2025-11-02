import database.DatabaseFactory
import database.MoodTrackerDatabaseRepository
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import routes.entryRoutes
import routes.userRoutes


fun configureDatabases() {
    DatabaseFactory.init()
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        configureDatabases()
        configureRouting()
        configureSerialization()
    }.start(wait = true)
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }
    install(CallLogging)
}
fun Application.configureRouting() {
    val repository = MoodTrackerDatabaseRepository()

    routing {
        userRoutes(repository)
        entryRoutes(repository)
    }
}
