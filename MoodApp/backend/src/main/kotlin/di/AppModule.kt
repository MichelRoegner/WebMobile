package di

import database.DatabaseFactory
import database.MoodTrackerDatabaseRepository
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.instance
import org.kodein.di.singleton

val appModule = DI.Module("app") {

    // DatabaseFactory als Singleton – init() wird beim ersten Zugriff ausgeführt
    bind<DatabaseFactory>() with singleton {
        DatabaseFactory.also { it.init() }
    }

    // Repository als Singleton, stellt sicher, dass die DB vorher initialisiert ist
    bind<MoodTrackerDatabaseRepository>() with singleton {
        // erzwingt, dass DatabaseFactory-Singleton einmal aufgebaut wird
        instance<DatabaseFactory>()
        MoodTrackerDatabaseRepository()
    }

}
