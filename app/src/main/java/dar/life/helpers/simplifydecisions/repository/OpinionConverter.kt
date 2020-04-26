package dar.life.helpers.simplifydecisions.repository

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dar.life.helpers.simplifydecisions.data.Opinion
import java.lang.reflect.Type

class OpinionConverter {

    @TypeConverter
    fun fromString(value: String?): MutableList<Opinion> {
        val listType: Type = object : TypeToken<MutableList<Opinion>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: MutableList<Opinion>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }


}