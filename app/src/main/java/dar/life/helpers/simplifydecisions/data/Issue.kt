package dar.life.helpers.simplifydecisions.data

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_A_COLOR
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_A_ICON
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_B_COLOR
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_B_ICON
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_CATEGORY
import dar.life.helpers.simplifydecisions.R

import dar.life.helpers.simplifydecisions.repository.DateConverter
import dar.life.helpers.simplifydecisions.repository.OpinionConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(tableName = "issues")
data class Issue(
    var title: String,
    var optionAName: String = "Option A",
    var optionBName: String = "Option B",
    var description: String? = null,
    var type: String = TYPE_YES_NO,
    var isActive: Boolean = true
) {


    @TypeConverters(DateConverter::class)
    var date = LocalDateTime.now()
    @PrimaryKey(autoGenerate = true)
    var id: Int = date.toEpochSecond(ZoneOffset.UTC).toInt()
    @TypeConverters(OpinionConverter::class)
    var opinions: MutableMap<String, MutableList<Opinion>> = createNewMap()
    var optionAIconName: String = DEFAULT_A_ICON
    var optionBIconName: String = DEFAULT_B_ICON
    var optionAColor: Int = Color.parseColor(DEFAULT_A_COLOR)
    var optionBColor: Int = Color.parseColor(DEFAULT_B_COLOR)

    private fun createNewMap(): MutableMap<String, MutableList<Opinion>> {
        val generalList: MutableList<Opinion> = mutableListOf()
        return mutableMapOf(Pair(DEFAULT_CATEGORY, generalList))
    }

    @Ignore
    var expanded: Boolean = false

    companion object{
        val DEFAULT_ISSUE: Issue = Issue(title = "", description = null)
        const val TYPE_YES_NO = "Yes/No Question"
        const val TYPE_MULTIPLE_OPTIONS = "Multiple Options Dilemma"

        fun fromTemplate(template: String): Issue{
            var optionA: String = "Option A"
            var optionB: String = "Option B"
            when (template){
                "new" -> {
                    optionA = "Yes"
                    optionB = "No"
                }
                "car" -> {
                    optionA = "Car #1"
                    optionB = "Car #2"
                }
                "work" -> {
                    optionA = "Find a new job"
                    optionB = "Stay in current job"
                }
                "love" -> {
                    optionA = "Ask him out"
                    optionB = "Let go"
                }
                "purchase" -> {
                    optionA = "Buy it"
                    optionB = "Save the money"
                }
                "vacation" -> {
                    optionA = "Travel to A"
                    optionB = "Travel to B"
                }
                "diet" -> {
                    optionA = "Diet #1"
                    optionB = "Diet #2"
                }

            }
            return Issue("", optionAName = optionA, optionBName = optionB)
        }
    }

    fun displayedTitle(context: Context): String{
        return if (title.isNotEmpty())
            title
        else
            context.getString(R.string.noIssueTitle)
    }
    fun toDecision(): Decision {
        isActive = false
        return Decision(title, description, opinions, id)
            .also { it.goals.addAll(listOf(Goal("do that"), Goal("do this"))) }
    }

    fun changeOpinionCategory(opinion: Opinion, category: String) {
        opinions[opinion.category]?.remove(opinion)
        opinion.category = category
        if (opinions.containsKey(category))
            opinions[category]!!.add(opinion)
        else
            opinions[category] = mutableListOf(opinion)
    }

    fun hasTasks(): Boolean = opinions.flatMap { it.value }.flatMap { it.tasks }.isNotEmpty()

}