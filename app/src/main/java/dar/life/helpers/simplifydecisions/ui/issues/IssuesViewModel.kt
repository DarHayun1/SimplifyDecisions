package dar.life.helpers.simplifydecisions.ui.issues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.repository.AppRepository

class IssuesViewModel(application: Application) : AndroidViewModel(application) {

    private val lastViewedIssue: Issue? = null
    private val mDecisionsRepository = AppRepository.getInstance(application)

    fun getAllIssues(): LiveData<List<Issue>> = mDecisionsRepository.allIssues

    fun getAllActiveIssues(): LiveData<List<Issue>> = mDecisionsRepository.allActiveIssues

    fun addNewIssue(): Issue{
        val issue = Issue("New Issue", "No Description")
        mDecisionsRepository.addNewIssue(issue)
        return issue
    }


}