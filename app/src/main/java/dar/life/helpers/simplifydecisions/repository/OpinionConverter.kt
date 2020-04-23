package dar.life.helpers.simplifydecisions.repository

import androidx.room.TypeConverter
import dar.life.helpers.simplifydecisions.data.Opinion

class OpinionConverter {

    @TypeConverter
    fun fromDbFormat(s: String?): List<Opinion>?{
        return Opinion.createListFromDbFormat(s)
    }

    @TypeConverter
    fun OpinionToDbFormat(opinions: List<Opinion>?): String? {
        return Opinion.listToDbFormat(opinions)

    }


}