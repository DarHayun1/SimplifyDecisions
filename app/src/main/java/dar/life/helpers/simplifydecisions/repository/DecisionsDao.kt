package dar.life.helpers.simplifydecisions.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.Issue

@Dao
@TypeConverters(DateConverter::class, OpinionConverter::class)
interface DecisionsDao {

    @Query("SELECT * FROM decisions ORDER BY date DESC")
    fun getAllDecisions(): LiveData<List<Decision>>

    @Query("SELECT * FROM decisions ORDER BY date DESC")
    fun getAllDecisionsNow(): List<Decision>

    @Query("SELECT * FROM decisions WHERE id = :requestedId")
    fun getDecisionById(requestedId: Int): LiveData<Decision>

    @Insert(onConflict = REPLACE)
    fun addNewDecision(decision: Decision)

    @Update
    fun updateDecision(decision: Decision)

    @Delete
    fun deleteDecision(decision: Decision)

}
