package database

import database.dao.EntryDAO
import database.dao.UserDAO
import database.tables.EntriesTable
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import model.User
import model.UserId
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
private fun java.time.Instant.toKxInstant(): Instant =
    Instant.fromEpochSeconds(epochSecond, nano.toLong())

private fun java.time.LocalDate.toKxLocalDate(): LocalDate =
    LocalDate(year, monthValue, dayOfMonth)

@OptIn(kotlin.time.ExperimentalTime::class)
class MoodTrackerDatabaseRepository {

    /* ====================== USER ====================== */

    suspend fun findUserById(id: UserId): User? = newSuspendedTransaction(Dispatchers.IO) {
        UserDAO.findById(id.value)?.toModel()
    }



    @OptIn(kotlin.time.ExperimentalTime::class)
    fun createUser(user: User): User = transaction {
        val src: java.time.LocalDate = user.registrationDate ?: java.time.LocalDate.now()
        val kxReg = src.toKxLocalDate()

        val dao = UserDAO.new {
            username = user.username
            email = user.email
            passwordHash = user.passwordHash
            registrationDate = kxReg
            isActive = user.isActive
        }
        dao.toModel()
    }



    suspend fun deleteUser(id: UserId): Boolean = newSuspendedTransaction(Dispatchers.IO) {
        UserDAO.findById(id.value)?.let { dao ->
            dao.delete()
            true
        } ?: false
    }

    /* ====================== ENTRIES ====================== */

    // CREATE
    fun createEntry(entry: model.Entry): model.Entry = transaction {
        val userDao = UserDAO.findById(entry.userId.value)
            ?: throw IllegalArgumentException("User ${entry.userId.value} not found")
        val dao = EntryDAO.new {
            user = userDao
            title = entry.title
            content = entry.content
            moodRating = entry.moodRating
            updatedAt = null
        }
        dao.toModel()
    }



    fun findAllEntries(userId: model.UserId): List<model.Entry> = transaction {
        EntryDAO.find { EntriesTable.userId eq userId.value }
            .orderBy(EntriesTable.id to SortOrder.DESC)
            .map { it.toModel() }  // toModel liest createdAt NICHT aus der DB
    }

    // READ by id
    fun findEntryById(entryId: model.EntryId): model.Entry? = transaction {
        EntryDAO.findById(entryId.value)?.toModel()
    }

    // UPDATE (setzt updatedAt)
    fun updateEntry(entry: model.Entry): model.Entry = transaction {
        val dao = EntryDAO.findById(entry.id.value)
            ?: throw IllegalArgumentException("Entry ${entry.id.value} not found")

        entry.title.let { dao.title = it }
        entry.content.let { dao.content = it }
        dao.moodRating = entry.moodRating
        //dao.updatedAt = Clock.System.now()          // Muss noch angepasst werden
        dao.toModel()
    }

    // DELETE
    fun deleteEntry(entryId: model.EntryId): Boolean = transaction {
        val entry = EntryDAO.findById(entryId.value)
        if (entry != null) {
            entry.delete()
            true
        } else {
            false
        }
    }
}
