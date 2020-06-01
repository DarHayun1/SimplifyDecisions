package dar.life.helpers.simplifydecisions.ui.decisions

import dar.life.helpers.simplifydecisions.data.Goal

interface OnGoalClickListener {
    fun onNewGoalRequest()
    fun onGoalChecked(goal: Goal)
    fun onEditGoalRequest(position: Int)
    fun deleteGoal(goal: Goal)
}