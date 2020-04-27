package dar.life.helpers.simplifydecisions.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.Issue

@Database(entities = [Issue::class, Decision::class], version = 1, exportSchema = false)
abstract class IssuesDatabase : RoomDatabase() {
    abstract fun issuesDao(): IssuesDao
    abstract fun decisionsDao(): DecisionsDao


    companion object {
        @Volatile private var instance: IssuesDatabase? = null
        private val LOCK = Any()
        const val DB_NAME: String = "issuesdb.db"

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            IssuesDatabase::class.java, DB_NAME)
            .build()
    }
}
