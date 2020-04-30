package dar.life.helpers.simplifydecisions.data

import androidx.room.Ignore

data class Opinion(var title: String,
                   var certaintypercent: Int,
                   var description: String = "",
                   var isPositive: Boolean = true,
                   var importance: Int = MEDIUM_IMPORTANCE) {

    val tasks: MutableList<String> = mutableListOf()

    companion object {

        const val LOW_IMPORTANCE = 2
        const val MEDIUM_IMPORTANCE = 3
        const val HIGH_IMPORTANCE = 9
        const val GAME_CHANGER = 18

    }

    val isAFact: Boolean get() = certaintypercent == 100
}
