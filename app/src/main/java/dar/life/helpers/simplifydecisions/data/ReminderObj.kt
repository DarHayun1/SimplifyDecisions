package dar.life.helpers.simplifydecisions.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dar.life.helpers.simplifydecisions.repository.DateConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

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

    fun toIntReminderObj(): IntReminderObj = IntReminderObj(
        title,
        id,
        time.toEpochSecond(ZoneOffset.UTC),
        isActive,
        text)

    override fun toString(): String {
        return "id: $id\ntime: $time\ntitle: $title\ntext: $text"
    }
}
data class IntReminderObj(
    val title1: String,
    var id1: Long,
    var time1: Long,
    var isActive1: Boolean,
    var text1: String){

    fun toReminderObj(): ReminderObj = ReminderObj(title1).apply {
        id = id1
        time = LocalDateTime.ofEpochSecond(time1,0, ZoneOffset.UTC)
        isActive = isActive1
        text = text1
    }
}
