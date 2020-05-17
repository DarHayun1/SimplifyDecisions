package dar.life.helpers.simplifydecisions.data

import java.time.LocalDateTime

data class ReminderObj(var name: String) {

    var time: LocalDateTime = LocalDateTime.now().plusWeeks(1)
}
