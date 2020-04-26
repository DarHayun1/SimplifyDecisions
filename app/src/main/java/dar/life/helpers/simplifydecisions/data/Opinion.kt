package dar.life.helpers.simplifydecisions.data

data class Opinion(var title: String,
                   var certaintypercent: Int,
                   var description: String = "",
                   var isPositive: Boolean = true,
                   var importance: Int = IMPORTANT) {

    companion object {

        const val NOT_IMPORTANT = 2
        const val IMPORTANT = 3
        const val VERY_IMPORTANT = 9
        const val GAME_CHANGER = 18

    }

    fun isAFact(): Boolean{
        return certaintypercent == 100
    }
}
