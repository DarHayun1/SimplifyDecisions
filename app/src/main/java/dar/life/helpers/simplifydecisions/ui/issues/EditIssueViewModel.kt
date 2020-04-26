package dar.life.helpers.simplifydecisions.ui.issues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.Issue
import dar.life.helpers.simplifydecisions.repository.AppRepository

class EditIssueViewModel(application: Application) : AndroidViewModel(application) {

    private val mDecisionsRepository = AppRepository.getInstance(application)

    fun getIssueById(id: String): LiveData<Issue>?{
        return mDecisionsRepository.getIssue(id)
    }


}