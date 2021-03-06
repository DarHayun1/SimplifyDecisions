package dar.life.helpers.simplifydecisions.ui.decisions

import dar.life.helpers.simplifydecisions.data.Goal

/**
 * [Goal] events listener
 */
interface OnGoalClickListener {
    fun onNewGoalRequest()
    fun onGoalChecked(goal: Goal)
    fun onEditGoalRequest(position: Int)
    fun goalDeleted(goal: Goal)
    fun goalExpanded(goal: Goal)
}