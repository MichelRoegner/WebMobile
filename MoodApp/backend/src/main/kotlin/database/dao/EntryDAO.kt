import database.dao.UserDAO
import database.tables.EntriesTable
import database.toDomainInstant
import org.jetbrains.exposed.dao.LongEntity
import org.jetbrains.exposed.dao.LongEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import kotlin.time.ExperimentalTime

class EntryDAO(id: EntityID<Long>) : LongEntity(id) {
    companion object : LongEntityClass<EntryDAO>(EntriesTable)

    var user by UserDAO referencedOn EntriesTable.userId
    var title by EntriesTable.title
    var content by EntriesTable.content
    var moodRating by EntriesTable.moodRating
    @OptIn(ExperimentalTime::class)
    var createdAt by EntriesTable.createdAt
    @OptIn(ExperimentalTime::class)
    var updatedAt by EntriesTable.updatedAt

    @OptIn(ExperimentalTime::class)
    fun toModel(): model.Entry =
        model.Entry(
            id = model.EntryId(id.value),
            userId = model.UserId(user.id.value),
            title = title,
            content = content,
            moodRating = moodRating,
            createdAt = createdAt.toDomainInstant(),
            updatedAt = updatedAt?.toDomainInstant()
        )
}
