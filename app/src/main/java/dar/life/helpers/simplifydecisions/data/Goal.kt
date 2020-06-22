package dar.life.helpers.simplifydecisions.data

/**
 *A data class representing a "goal" for a decision, included in the decisions db
 *
 */
data class Goal(var name: String) {

    //Saved as an epochday for simplicity but represents a LocalDate
    var epochDueDate: Long? = null

    //Holding the goal reminder (if exists)
    var reminder: ReminderObj = ReminderObj.emptyObj()

    var expanded: Boolean = false

    var isDone: Boolean = false

    override fun toString(): String {
        return "$name, dueDate: $epochDueDate, isDone: $isDone"
    }

}