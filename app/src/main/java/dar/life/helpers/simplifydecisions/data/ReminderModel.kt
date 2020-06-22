package dar.life.helpers.simplifydecisions.data

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * A Reminder data object, representing a reminder for a specific [Goal]
 *
 * @property title
 * @property isActive
 */
data class ReminderModel(var title: String, var isActive: Boolean = false) {

    companion object {
        fun emptyObj(): ReminderModel{
            return ReminderModel("", isActive = false)
        }
    }

    var id = Instant.now().epochSecond
    //The reminder's activating time
    var time: LocalDateTime = LocalDateTime.now().plusWeeks(1L)
    var text: String = ""

    //Migrating to a db version of the object
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
/**
 * The reminder object DB version (compatible types)
 */
data class IntReminderObj(
    val title1: String,
    var id1: Long,
    var time1: Long,
    var isActive1: Boolean,
    var text1: String){

    //Migrating to the Reminder object
    fun toReminderObj(): ReminderModel = ReminderModel(title1).apply {
        id = id1
        time = LocalDateTime.ofEpochSecond(time1,0, ZoneOffset.UTC)
        isActive = isActive1
        text = text1
    }
}
