package database

import database.dao.UserDAO
import database.tables.EntriesTable
import model.User
import model.UserId
import model.Entry
import kotlinx.coroutines.Dispatchers
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import model.EntryId
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import kotlin.time.ExperimentalTime
import kotlin.time.Clock


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
    fun createEntry(entry: Entry): Entry = transaction {
        val userDao = UserDAO.findById(entry.userId.value)
            ?: throw IllegalArgumentException("User ${entry.userId.value} not found")

        val dao = EntryDAO.new {
            user = userDao
            title = entry.title
            content = entry.content
            moodRating = entry.moodRating
            createdAt = entry.createdAt.toDbInstant()      // Kt -> Kx
            updatedAt = entry.updatedAt?.toDbInstant()
        }
        dao.toModel()
    }

    // UPDATE
    fun updateEntry(entry: Entry): Entry = transaction {
        val dao = EntryDAO.findById(entry.id.value)
            ?: throw IllegalArgumentException("Entry ${entry.id.value} not found")

        dao.title = entry.title
        dao.content = entry.content
        dao.moodRating = entry.moodRating
        dao.updatedAt = Clock.System.now().toDbInstant()  // Kt -> Kx

        dao.toModel()
    }



    fun findAllEntries(userId: UserId): List<Entry> = transaction {
        EntryDAO.find { EntriesTable.userId eq userId.value }
            .orderBy(EntriesTable.id to SortOrder.DESC)
            .map { it.toModel() }  // toModel liest createdAt NICHT aus der DB
    }

    // READ by id
    fun findEntryById(entryId: EntryId): Entry? = transaction {
        EntryDAO.findById(entryId.value)?.toModel()
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
