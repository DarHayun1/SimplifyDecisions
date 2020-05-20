package dar.life.helpers.simplifydecisions.data

import dar.life.helpers.simplifydecisions.Constants.Companion.DEFAULT_CATEGORY


data class Opinion(var title: String,
                   var description: String = "",
                   var isOfFirstOption: Boolean = true,
                   var importance: Int = MEDIUM_IMPORTANCE) {

    val tasks: MutableList<Task> = mutableListOf()
    var category: String = DEFAULT_CATEGORY

    companion object {

        const val LOW_IMPORTANCE = 0
        const val MEDIUM_IMPORTANCE = 20
        const val HIGH_IMPORTANCE = 40
        const val GAME_CHANGER = 60

    }

    val isAFact: Boolean get() = tasks.size == 0

    fun checkTask(position: Int, checked: Boolean): Boolean{
        return if(tasks[position].isChecked == checked)
            false
        else{
            tasks[position].flipChecked()
            true
        }
    }

    fun tasksLeftText(): CharSequence? {
        return "${tasks.filter {it.isChecked}.size}/${tasks.size}"
    }

    data class Task(var text: String) {
        var isChecked = false

        fun flipChecked(){
            isChecked = !isChecked
        }
    }
}
