package dar.life.helpers.simplifydecisions.repository

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dar.life.helpers.simplifydecisions.data.Opinion
import java.lang.reflect.Type
import java.time.*

class DateConverter {

    @TypeConverter
    fun fromTimestamp(epochSec: Long?): LocalDateTime? {
        return epochSec?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

    @TypeConverter
    fun fromString(value: String?): MutableList<Instant> {
        val listType: Type = object : TypeToken<MutableList<Instant>>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromList(list: MutableList<Instant>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }


}