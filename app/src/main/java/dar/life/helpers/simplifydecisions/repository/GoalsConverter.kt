package dar.life.helpers.simplifydecisions.repository

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dar.life.helpers.simplifydecisions.data.Goal
import java.lang.reflect.Type

class GoalsConverter {

    @TypeConverter
    fun fromJson(value: String?): MutableList<Goal> {
        val listType: Type = object : TypeToken<MutableList<Goal>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: MutableList<Goal>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
