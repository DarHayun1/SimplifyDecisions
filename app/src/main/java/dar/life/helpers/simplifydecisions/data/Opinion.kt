package dar.life.helpers.simplifydecisions.data

data class Opinion(var title: String,
                   var certaintypercent: Int,
                   var description: String = "",
                   var isPositive: Boolean = true,
                   var importance: Int = MEDIUM_IMPORTANCE) {

    companion object {

        const val LOW_IMPORTANCE = 2
        const val MEDIUM_IMPORTANCE = 3
        const val HIGH_IMPORTANCE = 9
        const val GAME_CHANGER = 18

    }

    fun isAFact(): Boolean{
        return certaintypercent == 100
    }
}
