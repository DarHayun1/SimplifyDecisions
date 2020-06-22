package dar.life.helpers.simplifydecisions.data

import dar.life.helpers.simplifydecisions.Constants.DEFAULT_CATEGORY

/**
 * Represent an opinion (or "argument") on a specific issue.
 *
 * @property title - the opinion's title
 * @property description - description if available
 * @property isOfFirstOption - a flag declaring which option the opinion supports
 * @property importance - the importance score
 */
data class Opinion(var title: String,
                   var description: String = "",
                   var isOfFirstOption: Boolean = true,
                   var importance: Int = MEDIUM_IMPORTANCE) {

    //A list of the to-do task object
    val tasks: MutableList<Task> = mutableListOf()
    //A String representation of the category
    var category: String = DEFAULT_CATEGORY

    companion object {

        //Importance tags to define the color
        const val LOW_IMPORTANCE = 0
        const val MEDIUM_IMPORTANCE = 25
        const val HIGH_IMPORTANCE = 50
        const val GAME_CHANGER = 75

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

    /**
     *
     * @return string representing the tasks completion status in the format :
     * unfinised_tasks/total_tasks
     */
    fun tasksLeftText(): CharSequence? {
        return "${tasks.filter {it.isChecked}.size}/${tasks.size}"
    }

    /**
     * An opinion's To-Do task
     *
     * @property text
     */
    data class Task(var text: String) {
        var isChecked = false

        fun flipChecked(){
            isChecked = !isChecked
        }
    }
}
