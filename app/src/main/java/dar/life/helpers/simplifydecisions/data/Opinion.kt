package dar.life.helpers.simplifydecisions.data

data class Opinion(val title: String, val certaintyPrecent: Int) {

    companion object {

        const val EXTERNAL_DELIMETER: String = "#1e2#"
        const val INTERNAL_DELIMETER: String = "#1i2#"

        fun createListFromDbFormat(string: String?): List<Opinion>? {
            val strings = string?.split(EXTERNAL_DELIMETER)
            if (strings == null || strings.isEmpty())
                return null
            return strings.map {
                val parts = it.split(INTERNAL_DELIMETER)
                if (parts.size < 2) return null
                else
                    Opinion(strings[0], strings[1].toInt())
            }
        }

        fun listToDbFormat(opinions: List<Opinion>?): String? {
           return opinions?.joinToString(EXTERNAL_DELIMETER) { opinion ->
               "${opinion.title}$INTERNAL_DELIMETER${opinion.certaintyPrecent}"
           }
        }
    }
}
