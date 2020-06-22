package dar.life.helpers.simplifydecisions.repository

import android.content.Context
import androidx.lifecycle.LiveData
import dar.life.helpers.simplifydecisions.data.DecisionModel
import dar.life.helpers.simplifydecisions.data.IssueModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * The App's singleton repository, in charge of updating and fetching data as a single source to
 * the data sources
 *
 * @param context
 */
class AppRepository
private constructor(context: Context) {
    companion object {
        private var sInstance: AppRepository? = null
        private val mutex = Mutex()

        /**
         * Creating an instance only once in an app lifetime, performed synchronized.
         *
         *
         * @param context
         * @return The repository singleton instance.
         */
        fun getInstance(context: Context): AppRepository =
            sInstance ?: runBlocking {
                mutex.withLock { }
                sInstance ?: AppRepository(context).also {sInstance = it}
            }
    }


    private val mIssuesDao: IssuesDao
    private val mDecisionsDao: DecisionsDao
    val allIssues: LiveData<List<IssueModel>>
    val allActiveIssues: LiveData<List<IssueModel>>
    val allDecisions: LiveData<List<DecisionModel>>

    val allDecisionsNow: List<DecisionModel>
        get() = mDecisionsDao.getAllDecisionsNow()

    init {
        val db = IssuesDatabase.getDatabase(context)
        mIssuesDao = db.issuesDao()
        mDecisionsDao = db.decisionsDao()
        allActiveIssues = mIssuesDao.getAllActiveIssues()
        allIssues = mIssuesDao.getAllIssues()
        allDecisions = mDecisionsDao.getAllDecisions()
    }

    fun getIssue(requestedId: Int): LiveData<IssueModel?> {
        return mIssuesDao.getIssueById(requestedId)
    }

    fun addNewIssue(issue: IssueModel) {
        GlobalScope.launch(Dispatchers.IO) {
        mIssuesDao.addNewIssue(issue)
        }
    }

    fun updateIssue(issue: IssueModel) {
        GlobalScope.launch(Dispatchers.IO) { mIssuesDao.updateIssue(issue) }
    }

    fun addNewDecision(decision: DecisionModel) {
        GlobalScope.launch(Dispatchers.IO) { mDecisionsDao.addNewDecision(decision) }
    }

    fun getDecision(id: Int): LiveData<DecisionModel?> {
        return mDecisionsDao.getDecisionById(id)
    }

    fun updateDecision(decision: DecisionModel) {
        GlobalScope.launch(Dispatchers.IO) { mDecisionsDao.updateDecision(decision) }
    }
}