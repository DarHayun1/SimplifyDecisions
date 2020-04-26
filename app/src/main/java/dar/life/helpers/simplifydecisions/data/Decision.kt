package dar.life.helpers.simplifydecisions.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dar.life.helpers.simplifydecisions.repository.DateConverter
import dar.life.helpers.simplifydecisions.repository.OpinionConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZonedDateTime


@Entity(tableName = "decisions")
@TypeConverters(OpinionConverter::class)
data class Decision(
    var title: String,
    var description: String?,
    var opinions: MutableList<Opinion> = mutableListOf(),
    val issueIdBasedOf: String? = null
) {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    @TypeConverters(DateConverter::class)
    var date: LocalDateTime = LocalDateTime.now()
    @TypeConverters(DateConverter::class)
    var reminders: MutableList<Instant> = mutableListOf()


}