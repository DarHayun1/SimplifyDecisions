package dar.life.helpers.simplifydecisions.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Decision(@PrimaryKey(autoGenerate = true) var id: Int, var title: String) {
}