package dar.life.helpers.simplifydecisions.repository

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dar.life.helpers.simplifydecisions.data.DecisionModel
import dar.life.helpers.simplifydecisions.data.IssueModel
import dar.life.helpers.simplifydecisions.data.ReminderObj


@Database(entities = [IssueModel::class, DecisionModel::class], version = 3, exportSchema = false)
abstract class IssuesDatabase : RoomDatabase() {

    abstract fun issuesDao(): IssuesDao
    abstract fun decisionsDao(): DecisionsDao

    companion object {

        @Volatile private var instance: IssuesDatabase? = null

        private val LOCK = Any()
        private const val DB_NAME: String = "issuesdb.db"

        operator fun invoke(context: Context) =
            instance ?:
            synchronized(LOCK){
                instance ?:
                buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            IssuesDatabase::class.java, DB_NAME)
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()

        private val MIGRATION_1_2: Migration =
            object : Migration(1, 2) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        "ALTER TABLE decisions "
                                + " ADD COLUMN color_name TEXT DEFAULT blue_light NOT NULL"
                    )
                }
            }

        private val MIGRATION_2_3: Migration =
            object : Migration(2, 3) {
                override fun migrate(database: SupportSQLiteDatabase) {
                    database.execSQL(
                        "DROP TABLE reminders"
                    )
                }
            }
    }
}
