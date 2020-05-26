package dar.life.helpers.simplifydecisions.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dar.life.helpers.simplifydecisions.repository.DateConverter
import java.time.Instant
import java.time.LocalDateTime

@Entity(tableName = "reminders")
data class ReminderObj(var title: String) {

    companion object {
        fun emptyObj(): ReminderObj{
            return ReminderObj("")
        }
    }


    @PrimaryKey
    var id = Instant.now().epochSecond
    @TypeConverters(DateConverter::class)
    var time: LocalDateTime = LocalDateTime.now()
    var isActive = false
    var text: String = ""

    override fun toString(): String {
        return "id: $id\ntime: $time\ntitle: $title\ntext: $text"
    }
}
