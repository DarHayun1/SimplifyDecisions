package dar.life.helpers.simplifydecisions.ui.issues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.repository.AppRepository

class OpinionViewModel(application: Application): AndroidViewModel(application) {

    private val repository: AppRepository = AppRepository.getInstance(application)

    var opinion: Opinion? = null
    var issue: Issue? = null

    fun getIssueById(id: Int): LiveData<Issue?>{
        return repository.getIssue(id)
    }

    fun updateIssue(issue: Issue) {
        repository.updateIssue(issue)
    }
}