package dar.life.helpers.simplifydecisions.repository

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import dar.life.helpers.simplifydecisions.data.Issue

@Dao
@TypeConverters(DateConverter::class, OpinionConverter::class)
interface IssuesDao {

    @Query("SELECT * FROM issues ORDER BY date DESC")
    fun getAllIssues(): List<Issue>

    @Insert(onConflict = REPLACE)
    fun addNewIssue(issue: Issue)

    @Update
    fun updateIssue(issue: Issue)

    @Delete
    fun deleteIssue(issue: Issue)

}
