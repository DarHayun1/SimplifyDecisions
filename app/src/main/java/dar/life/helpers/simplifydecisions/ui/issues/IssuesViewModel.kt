package dar.life.helpers.simplifydecisions.ui.issues

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.IssueModel
import dar.life.helpers.simplifydecisions.data.Opinion
import dar.life.helpers.simplifydecisions.repository.AppRepository

/**
 * An [AndroidViewModel] serving fragments displaying [IssueModel]s
 */
class IssuesViewModel(application: Application) : AndroidViewModel(application) {

    var lastUsedIssue: IssueModel? = null
    var lastUsedOpinion: Opinion? = null
    private val mDecisionsRepository =
        AppRepository.getInstance(application.applicationContext)

    fun getAllIssues(): LiveData<List<IssueModel>> = mDecisionsRepository.allIssues

    fun getAllActiveIssues(): LiveData<List<IssueModel>> = mDecisionsRepository.allActiveIssues

    fun addNewIssue(issue: IssueModel = IssueModel("New Issue")) {
        mDecisionsRepository.addNewIssue(issue)
        lastUsedIssue = issue
    }

    fun getIssueById(id: Int): LiveData<IssueModel?> {
        return mDecisionsRepository.getIssue(id)
    }

    fun updateIssue(issue: IssueModel) {
        mDecisionsRepository.updateIssue(issue)
    }


}