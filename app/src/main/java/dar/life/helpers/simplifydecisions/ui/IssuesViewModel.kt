package dar.life.helpers.simplifydecisions.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.repository.DecisionsRepository

class IssuesViewModel(application: Application) : AndroidViewModel(application) {

    private val decisionsRepository = DecisionsRepository.getInstance(application)

    fun getAllIssues(): LiveData<List<Issue>> {
        return decisionsRepository.allIssues
    }



}