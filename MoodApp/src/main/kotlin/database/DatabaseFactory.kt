package database

import database.tables.UsersTable
import database.tables.EntriesTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import java.nio.file.Paths

object DatabaseFactory {
    fun init() {
        val dbPath = Paths.get("moodtracker.db").toAbsolutePath().normalize()
        println("[DB] Using SQLite at: $dbPath")

        Database.connect("jdbc:sqlite:$dbPath", driver = "org.sqlite.JDBC")
        transaction { SchemaUtils.create(UsersTable, EntriesTable) }
    }
}

