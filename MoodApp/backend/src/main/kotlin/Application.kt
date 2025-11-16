import database.MoodTrackerDatabaseRepository
import di.appModule
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.calllogging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.kodein.di.ktor.di
import routes.entryRoutes
import routes.userRoutes

// DI-Konfiguration
fun Application.configureDI() {
    di {
        import(appModule)
    }
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
    val di by closestDI()
    val repository by di.instance<MoodTrackerDatabaseRepository>()

    routing {
        userRoutes(repository)
        entryRoutes(repository)
    }
}

fun main() {
    embeddedServer(Netty, port = 8080) {
        configureDI()            // DI zuerst
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
