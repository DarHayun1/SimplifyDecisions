package dar.life.helpers.simplifydecisions.repository

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dar.life.helpers.simplifydecisions.data.Opinion
import java.lang.reflect.Type

class OpinionConverter {

    @TypeConverter
    fun fromString(value: String?): MutableMap<String, MutableList<Opinion>> {
        val mapType: Type = object : TypeToken<MutableMap<String, MutableList<Opinion>>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: MutableMap<String, MutableList<Opinion>>?): String {
        val gson = Gson()
        return gson.toJson(map)
    }


}