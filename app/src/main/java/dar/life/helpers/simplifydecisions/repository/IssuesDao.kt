package dar.life.helpers.simplifydecisions.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import dar.life.helpers.simplifydecisions.data.IssueModel

@Dao
@TypeConverters(DateConverter::class, OpinionConverter::class)
interface IssuesDao {

    @Query("SELECT * FROM issues ORDER BY date DESC")
    fun getAllIssues(): LiveData<List<IssueModel>>

    @Query("SELECT * FROM issues WHERE isActive = 1 ORDER BY date DESC")
    fun getAllActiveIssues(): LiveData<List<IssueModel>>

    @Query("SELECT * FROM issues WHERE id = :requestedId")
    fun getIssueById(requestedId: Int): LiveData<IssueModel?>

    @Insert(onConflict = REPLACE)
    fun addNewIssue(issue: IssueModel)

    @Update
    fun updateIssue(issue: IssueModel)

    @Delete
    fun deleteIssue(issue: IssueModel)

}
