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

    data class IntGoal(var title: String) {
        lateinit var intReminder: IntReminderObj
        var epochDd: Long? = null

        companion object {
            fun fromGoalList(goals: MutableList<Goal>?): MutableList<IntGoal> =
                goals?.map{ goal -> IntGoal(goal.name).apply {
                    epochDd = goal.epochDueDate
                    intReminder = goal.reminder.toIntReminderObj()
                }
                }?.toMutableList()
                    ?: mutableListOf()

            fun fromIntGoalList(intGoals: MutableList<IntGoal>): MutableList<Goal> =
                intGoals.map{
                        intGoal -> Goal(intGoal.title).apply {
                    epochDueDate = intGoal.epochDd
                    reminder = intGoal.intReminder.toReminderObj()
                    }
                }.toMutableList()
        }
    }

}
