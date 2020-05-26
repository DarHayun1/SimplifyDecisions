package dar.life.helpers.simplifydecisions.repository

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.ReminderObj

@Dao
@TypeConverters(DateConverter::class)
interface RemindersDao {

    @Query("SELECT * FROM reminders ORDER BY title DESC")
    fun getAllReminders(): LiveData<List<ReminderObj>>

    @Query("SELECT * FROM reminders WHERE id = :requestedId")
    fun getReminderById(requestedId: Int): ReminderObj?

    @Insert(onConflict = REPLACE)
    fun addNewReminder(reminder: ReminderObj)

    @Update
    fun updateReminder(reminder: ReminderObj)

    @Delete
    fun deleteReminder(reminder: ReminderObj)

}
