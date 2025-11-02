package database.dao

import database.tables.EntriesTable
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import java.time.LocalDateTime

@OptIn(kotlin.time.ExperimentalTime::class)
class EntryDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<EntryDAO>(EntriesTable)

    var user by UserDAO referencedOn EntriesTable.userId
    var title by EntriesTable.title
    var content by EntriesTable.content
    var moodRating by EntriesTable.moodRating

    // vorhanden, aber Zugriff von außen verhindern: Typ Probleme
    var createdAt by EntriesTable.createdAt

    var updatedAt by EntriesTable.updatedAt

    fun toModel(): model.Entry =
        model.Entry(
            id = model.EntryId(id.value),
            userId = model.UserId(user.id.value),
            title = title,
            content = content,
            moodRating = moodRating,
            createdAt = LocalDateTime.now(),
            updatedAt = null
        )
}
