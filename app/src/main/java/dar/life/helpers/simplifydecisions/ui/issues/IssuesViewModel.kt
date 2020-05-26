package dar.life.helpers.simplifydecisions.ui.issues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.repository.AppRepository

class IssuesViewModel(application: Application) : AndroidViewModel(application) {

    var lastUsedIssue: Issue? = null
    var lastUsedOpinion: Opinion? = null
    private val mDecisionsRepository =
        AppRepository.getInstance(application.applicationContext)

    fun getAllIssues(): LiveData<List<Issue>> = mDecisionsRepository.allIssues

    fun getAllActiveIssues(): LiveData<List<Issue>> = mDecisionsRepository.allActiveIssues

    fun addNewIssue(issue: Issue = Issue("New Issue")) {
        mDecisionsRepository.addNewIssue(issue)
        lastUsedIssue = issue
    }

    fun getIssueById(id: Int): LiveData<Issue?> {
        return mDecisionsRepository.getIssue(id)
    }

    fun updateIssue(issue: Issue) {
        mDecisionsRepository.updateIssue(issue)
    }


}