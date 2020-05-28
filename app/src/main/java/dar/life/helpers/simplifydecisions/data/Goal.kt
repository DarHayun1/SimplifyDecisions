package dar.life.helpers.simplifydecisions.data

import android.telephony.CellLocation.getEmpty
import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime

data class Goal(var name: String) {

    //Saved as an epochday but represents a LocalDate
    var epochDueDate: Long? = null


    var reminder: ReminderObj = ReminderObj.emptyObj()

    var expanded: Boolean = false

    override fun toString(): String {
        return "$name, dueDate: $epochDueDate"
    }

}