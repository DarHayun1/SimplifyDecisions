package dar.life.helpers.simplifydecisions.data

import androidx.room.*
import dar.life.helpers.simplifydecisions.repository.DateConverter
import dar.life.helpers.simplifydecisions.repository.OpinionConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(tableName = "issues")
data class Issue(
    var title: String,
    var description: String?,
    var type: String = TYPE_YES_NO,
    var isActive: Boolean = true
) {

    @TypeConverters(DateConverter::class)
    var date = LocalDateTime.now()
    @PrimaryKey(autoGenerate = true)
    var id: Int = date.toEpochSecond(ZoneOffset.UTC).toInt()
    @TypeConverters(OpinionConverter::class)
    var opinions: MutableList<Opinion> = createDemoOpinions()


    @Ignore
    var expanded: Boolean = false

    companion object{
        const val TYPE_YES_NO = "Yes/No Question"
        const val TYPE_MULTIPLE_OPTIONS = "Multiple Options Dilemma"
    }

    fun toDecision(): Decision {
        isActive = false
        return Decision(title, description, opinions, id)
    }

    private fun createDemoOpinions(): MutableList<Opinion> {
        return mutableListOf(
            Opinion("First Opinion!", 100),
            Opinion("Second! OMG it's bigger", 80),
            Opinion("First Opinion!", 100),
            Opinion("Second! OMG it's bigger", 80),
            Opinion("First Opinion!", 100),
            Opinion("Second! OMG it's bigger", 80),
            Opinion("First Opinion!", 100),
            Opinion("Second! OMG it's bigger", 80),
            Opinion(
                "3Second! OMG it's bigger! the Biggeeesssstt", 100,
                isPositive = false, importance = Opinion.GAME_CHANGER
            ),
            Opinion(
                "4Second! OMG it's Biggeeesssstt", 100, importance =
                Opinion.HIGH_IMPORTANCE
            ),
            Opinion("Second! OMG it's bigger", 100),
            Opinion(
                "Second! OMG it's bigger", 100, isPositive = false,
                importance = Opinion.LOW_IMPORTANCE
            ),
            Opinion(
                "Second! OMG it's bigger", 100, isPositive = false,
                importance = Opinion.HIGH_IMPORTANCE
            ),
            Opinion(
                "Second! OMG it's bigger", 100, isPositive = true,
                importance = Opinion.MEDIUM_IMPORTANCE
            ),
            Opinion(
                "Second! OMG it's bigger", 100, isPositive = false,
                importance = Opinion.LOW_IMPORTANCE
            ),
            Opinion(
                "Second! OMG it's bigger", 100, isPositive = false,
                importance = Opinion.LOW_IMPORTANCE
            ),
            Opinion(
                "Second! OMG it's bigger", 100, isPositive = false,
                importance = Opinion.LOW_IMPORTANCE
            ),
            Opinion(
                "Second! OMG it's bigger", 100, isPositive = false,
                importance = Opinion.LOW_IMPORTANCE
            ),
            Opinion("Second! OMG it's bigger", 80)
        )
    }

}