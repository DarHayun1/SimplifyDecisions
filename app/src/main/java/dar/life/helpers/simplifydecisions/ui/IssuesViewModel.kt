package dar.life.helpers.simplifydecisions.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.repository.AppRepository

class IssuesViewModel(application: Application) : AndroidViewModel(application) {

    private val mDecisionsRepository = AppRepository.getInstance(application)

    fun getAllIssues(): LiveData<List<Issue>> {
        return mDecisionsRepository.allIssues
    }

    fun addIssue(issue: Issue){
        mDecisionsRepository.addNewIssue(issue)
    }



}