package dar.life.helpers.simplifydecisions.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dar.life.helpers.simplifydecisions.data.DecisionModel;
import dar.life.helpers.simplifydecisions.data.IssueModel;

public class AppRepository {

    private static AppRepository sInstance;
    private final IssuesDao mIssuesDao;
    private final DecisionsDao mDecisionsDao;

    private final LiveData<List<IssueModel>> mIssues;
    private final LiveData<List<IssueModel>> mActiveIssues;

    private final LiveData<List<DecisionModel>> mDecisions;



    private AppRepository(Context context) {

        IssuesDatabase db = IssuesDatabase.Companion.invoke(context);

        mIssuesDao = db.issuesDao();
        mDecisionsDao = db.decisionsDao();

        mActiveIssues = mIssuesDao.getAllActiveIssues();
        mIssues = mIssuesDao.getAllIssues();

        mDecisions = mDecisionsDao.getAllDecisions();

    }

    /**
     * Creating an instance only once in an app lifetime. performed in a synced way.
     *
     *
     * @param context@return The repository singleton instance.
     */
    public static AppRepository getInstance(Context context) {
        if (sInstance == null) {
            synchronized (AppRepository.class) {
                if (sInstance == null)
                    sInstance = new AppRepository(context);
            }
        }
        return sInstance;
    }

    @NotNull
    public LiveData<List<IssueModel>> getAllIssues() {
        return mIssues;
    }

    @NotNull
    public LiveData<List<DecisionModel>> getAllDecisions() {
        return mDecisions;
    }

    public List<DecisionModel> getAllDecisionsNow() {
        return mDecisionsDao.getAllDecisionsNow();
    }

    @NotNull
    public LiveData<List<IssueModel>> getAllActiveIssues() {
        return mActiveIssues;
    }

    public LiveData<IssueModel> getIssue(int requestedId) {
        return mIssuesDao.getIssueById(requestedId);
    }

    public void addNewIssue(@NotNull final IssueModel issue) {
        AppExecutors.getInstance().diskIO().execute(() -> mIssuesDao.addNewIssue(issue));
    }

    public void updateIssue(@NotNull IssueModel issue) {
        AppExecutors.getInstance().diskIO().execute(() -> mIssuesDao.updateIssue(issue));
    }

    public void addNewDecision(@NotNull DecisionModel decision) {
        AppExecutors.getInstance().diskIO().execute(() -> mDecisionsDao.addNewDecision(decision));
    }

    public LiveData<DecisionModel> getDecision(int id) {
        return mDecisionsDao.getDecisionById(id);
    }

    public void updateDecision(@NotNull DecisionModel decision) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            mDecisionsDao.updateDecision(decision);
        });
    }
}
