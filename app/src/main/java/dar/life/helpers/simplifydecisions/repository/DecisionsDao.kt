package dar.life.helpers.simplifydecisions.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import dar.life.helpers.simplifydecisions.data.DecisionModel

@Dao
@TypeConverters(DateConverter::class, OpinionConverter::class)
interface DecisionsDao {

    @Query("SELECT * FROM decisions ORDER BY date DESC")
    fun getAllDecisions(): LiveData<List<DecisionModel>>

    @Query("SELECT * FROM decisions ORDER BY date DESC")
    fun getAllDecisionsNow(): List<DecisionModel>

    @Query("SELECT * FROM decisions WHERE id = :requestedId")
    fun getDecisionById(requestedId: Int): LiveData<DecisionModel?>

    @Insert(onConflict = REPLACE)
    fun addNewDecision(decision: DecisionModel)

    @Update
    fun updateDecision(decision: DecisionModel)

    @Delete
    fun deleteDecision(decision: DecisionModel)

}
