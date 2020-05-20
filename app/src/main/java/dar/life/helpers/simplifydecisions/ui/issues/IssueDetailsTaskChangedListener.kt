package dar.life.helpers.simplifydecisions.ui.issues

import dar.life.helpers.simplifydecisions.data.Opinion

interface IssueDetailsTaskChangedListener {
    fun taskTextChanged()
    fun taskCheckedChanged()
}
