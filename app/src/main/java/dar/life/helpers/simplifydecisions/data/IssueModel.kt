package dar.life.helpers.simplifydecisions.data

import android.content.Context
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_A_ICON
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_B_ICON
import dar.life.helpers.simplifydecisions.Constants.DEFAULT_CATEGORY
import dar.life.helpers.simplifydecisions.R

import dar.life.helpers.simplifydecisions.repository.DateConverter
import dar.life.helpers.simplifydecisions.repository.OpinionConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

@Entity(tableName = "issues")
data class IssueModel(
    var title: String,
    var aColorName: String = DEFAULT_A_ICON,
    var bColorName: String = DEFAULT_B_ICON,
    var aTitle: String = "Option A",
    var bTitle: String = "Option B",
    var description: String? = null,
    var isActive: Boolean = true
) {


    @TypeConverters(DateConverter::class)
    var date = LocalDateTime.now()
    @PrimaryKey(autoGenerate = true)
    var id: Int = date.toEpochSecond(ZoneOffset.UTC).toInt()
    @TypeConverters(OpinionConverter::class)
    var opinions: MutableMap<String, MutableList<Opinion>> = createNewMap()

    private fun createNewMap(): MutableMap<String, MutableList<Opinion>> {
        val generalList: MutableList<Opinion> = mutableListOf()
        return mutableMapOf(Pair(DEFAULT_CATEGORY, generalList))
    }

    @Ignore
    var expanded: Boolean = false

    companion object{
        val DEFAULT_ISSUE: IssueModel = IssueModel(title = "", description = null)

        fun fromTemplate(template: String): IssueModel{
            var optionA = "Option A"
            var optionB = "Option B"
            when (template){
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
                "yes_no" -> {
                    optionA = "Yes"
                    optionB = "No"
                }

            }
            return IssueModel("", aTitle = optionA, bTitle = optionB)
        }
    }

    fun displayedTitle(context: Context): String{
        return if (title.isNotEmpty())
            title
        else
            context.getString(R.string.noIssueTitle)
    }
    fun toDecision(isOpinionA: Boolean): Decision {
        isActive = false
        val decisionName = if (isOpinionA) aTitle else bTitle
        return Decision(decisionName, description, opinions, id)

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

    fun getOptionsScores(): Pair<Int, Int> {
        val (first, second) =
            opinions.flatMap { it.value }.partition { it.isOfFirstOption }
        return Pair(
            first.sumBy { it.importance },
            second.sumBy { it.importance }
        )
    }
}