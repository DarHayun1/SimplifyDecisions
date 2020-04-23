package dar.life.helpers.simplifydecisions.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.Issue

@Database(entities = [Issue::class, Decision::class], version = 1, exportSchema = false)
abstract class IssuesDatabase : RoomDatabase() {
    abstract fun issuesDao(): IssuesDao
}