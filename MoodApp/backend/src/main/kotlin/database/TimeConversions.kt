package database
import kotlin.time.ExperimentalTime
import kotlin.time.Instant as KtInstant
import kotlinx.datetime.Instant as KxInstant

@OptIn(ExperimentalTime::class)
fun KtInstant.toDbInstant(): KxInstant =
    KxInstant.fromEpochMilliseconds(this.toEpochMilliseconds())

@OptIn(ExperimentalTime::class)
fun KxInstant.toDomainInstant(): KtInstant =
    KtInstant.fromEpochMilliseconds(this.toEpochMilliseconds())
