package dar.life.helpers.simplifydecisions.repository

import android.util.Log
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dar.life.helpers.simplifydecisions.data.Goal
import dar.life.helpers.simplifydecisions.data.IntReminderObj
import java.lang.reflect.Type

class GoalsConverter {

    @TypeConverter
    fun fromJson(value: String?): MutableList<Goal> {
        val listType: Type = object : TypeToken<MutableList<IntGoal>>() {}.type
        val list: MutableList<IntGoal> = Gson().fromJson(value, listType)
        return IntGoal.fromIntGoalList(list)
    }

    @TypeConverter
    fun fromList(list: MutableList<Goal>?): String {
        val gson = Gson()
        val intList = IntGoal.fromGoalList(list)
        return gson.toJson(intList)
    }

    data class IntGoal(
        val title: String,
        val isDone: Boolean = false,
        val expanded: Boolean = false,
        var epochDd: Long? = null
    ) {
        lateinit var intReminder: IntReminderObj

        companion object {
            fun fromGoalList(goals: MutableList<Goal>?): MutableList<IntGoal> =
                goals?.map{ goal -> IntGoal(
                    goal.name,
                    goal.isDone,
                    goal.expanded,
                epochDd = goal.epochDueDate).apply {
                    intReminder = goal.reminder.toIntReminderObj()
                }
                }?.toMutableList()
                    ?: mutableListOf()

            fun fromIntGoalList(intGoals: MutableList<IntGoal>): MutableList<Goal> =
                intGoals.map{
                        intGoal -> Goal(intGoal.title).apply {
                    epochDueDate = intGoal.epochDd
                    isDone = intGoal.isDone
                    reminder = intGoal.intReminder.toReminderObj()
                    expanded = intGoal.expanded
                    }
                }.toMutableList()
        }
    }

}
