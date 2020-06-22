package dar.life.helpers.simplifydecisions.data

import android.provider.SyncStateContract
import androidx.room.*
import dar.life.helpers.simplifydecisions.Constants
import dar.life.helpers.simplifydecisions.repository.DateConverter
import dar.life.helpers.simplifydecisions.repository.GoalsConverter
import dar.life.helpers.simplifydecisions.repository.OpinionConverter
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Decision Data Model.
 * Represents a decision that's made.
 *
 * @property title - decision's title
 * @property description - description (Nullable)
 * @property opinions - The opinions that support the decision
 * @property issueIdBasedOf - The original [IssueModel] id.
 * @property colorName - the decision's theme color.
 */
@Entity(tableName = "decisions")
@TypeConverters(OpinionConverter::class)
data class DecisionModel(
    var title: String,
    var description: String?,
    var opinions: MutableMap<String, MutableList<Opinion>> = mutableMapOf(),
    val issueIdBasedOf: Int? = null,
    @ColumnInfo(name = "color_name")
    val colorName: String = Constants.DEFAULT_A_ICON
) {

    @TypeConverters(DateConverter::class)
    var date: LocalDateTime = LocalDateTime.now()
    @PrimaryKey(autoGenerate = false)
    var id: Int = date.toEpochSecond(ZoneOffset.UTC).toInt()
    @TypeConverters(GoalsConverter::class)
    var goals: MutableList<Goal> = mutableListOf()

    @Ignore
    var expanded: Boolean = false

    override fun toString(): String {
        return "Decision - title: $title, goals:$goals"
    }

}