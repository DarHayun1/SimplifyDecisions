package dar.life.helpers.simplifydecisions.ui.issues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Decision
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.repository.AppRepository
import dar.life.helpers.simplifydecisions.ui.Instruction

class EditIssueViewModel(application: Application) : AndroidViewModel(application) {

    var issueDetailsInstruc: List<Instruction>? = null

    private val mDecisionsRepository =
        AppRepository.getInstance(application.applicationContext)

    fun getIssueById(id: Int): LiveData<Issue>?{
        return mDecisionsRepository.getIssue(id)
    }

    fun updateIssue(issue: Issue) {
        mDecisionsRepository.updateIssue(issue)
    }

    fun addDecision(decision: Decision) = mDecisionsRepository.addNewDecision(decision)


}