package dar.life.helpers.simplifydecisions.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dar.life.helpers.simplifydecisions.repository.DateConverter
import dar.life.helpers.simplifydecisions.repository.OpinionConverter
import java.time.LocalDateTime

@Entity(tableName = "issues")
data class Issue(var title: String) {

    @TypeConverters(DateConverter::class)
    var date = LocalDateTime.now()
    @PrimaryKey
    var id: String = "$title$date"
    @TypeConverters(OpinionConverter::class)
    var opinions: MutableList<Opinion>? = mutableListOf<Opinion>()

}