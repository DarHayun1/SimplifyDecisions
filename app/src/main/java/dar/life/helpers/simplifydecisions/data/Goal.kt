package dar.life.helpers.simplifydecisions.data

import java.time.LocalDate
import java.time.LocalDateTime

data class Goal(var name: String) {

    var dueDate: LocalDate? = null
    val reminders: MutableList<ReminderObj> = mutableListOf()

    var expanded: Boolean = false

}