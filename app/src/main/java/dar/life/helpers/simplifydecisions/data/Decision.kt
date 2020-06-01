package dar.life.helpers.simplifydecisions.data

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dar.life.helpers.simplifydecisions.repository.DateConverter
import dar.life.helpers.simplifydecisions.repository.GoalsConverter
import dar.life.helpers.simplifydecisions.repository.OpinionConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset


@Entity(tableName = "decisions")
@TypeConverters(OpinionConverter::class)
data class Decision(
    var title: String,
    var description: String?,
    var opinions: MutableMap<String, MutableList<Opinion>> = mutableMapOf(),
    val issueIdBasedOf: Int? = null
) {

    @TypeConverters(DateConverter::class)
    var date: LocalDateTime = LocalDateTime.now()
    @PrimaryKey(autoGenerate = false)
    var id: Int = date.toEpochSecond(ZoneOffset.UTC).toInt()
    @TypeConverters(GoalsConverter::class)
    var goals: MutableList<Goal> = mutableListOf()

    @Ignore
    var expanded: Boolean = false

    override fun toString(): String {
        return "Decision - title: $title, goals:$goals"
    }

}