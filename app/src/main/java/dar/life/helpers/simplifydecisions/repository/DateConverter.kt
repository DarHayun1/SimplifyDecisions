package dar.life.helpers.simplifydecisions.repository

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dar.life.helpers.simplifydecisions.data.Opinion
import java.lang.reflect.Type
import java.time.*

/**
 * A converter for db intended data.
 *
 * (epochSec: Long?) <--> (date: LocalDateTime?)
 */
class DateConverter {

    @TypeConverter
    fun fromTimestamp(epochSec: Long?): LocalDateTime? {
        return epochSec?.let { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.UTC)
    }

}