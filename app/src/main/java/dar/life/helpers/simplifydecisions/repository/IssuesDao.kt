package dar.life.helpers.simplifydecisions.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import dar.life.helpers.simplifydecisions.data.Issue

@Dao
@TypeConverters(DateConverter::class, OpinionConverter::class)
interface IssuesDao {

    @Query("SELECT * FROM issues ORDER BY date DESC")
    fun getAllIssues(): LiveData<List<Issue>>

    @Query("SELECT * FROM issues WHERE isActive = 1 ORDER BY date DESC")
    fun getAllActiveIssues(): LiveData<List<Issue>>

    @Query("SELECT * FROM issues WHERE id = :requestedId")
    fun getIssueById(requestedId: Int): LiveData<Issue?>

    @Insert(onConflict = REPLACE)
    fun addNewIssue(issue: Issue)

    @Update
    fun updateIssue(issue: Issue)

    @Delete
    fun deleteIssue(issue: Issue)

}
