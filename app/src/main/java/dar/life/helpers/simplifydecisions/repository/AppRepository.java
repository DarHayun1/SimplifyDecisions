package dar.life.helpers.simplifydecisions.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dar.life.helpers.simplifydecisions.data.Decision;
import dar.life.helpers.simplifydecisions.data.IssueModel;

public class AppRepository {

    private static AppRepository sInstance;
    private final IssuesDao mIssuesDao;
    private final DecisionsDao mDecisionsDao;

    private final LiveData<List<IssueModel>> mIssues;
    private final LiveData<List<IssueModel>> mActiveIssues;

    private final LiveData<List<Decision>> mDecisions;



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
    public LiveData<List<Decision>> getAllDecisions() {
        return mDecisions;
    }

    public List<Decision> getAllDecisionsNow() {
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

    public void addNewDecision(@NotNull Decision decision) {
        Log.i("helpfix", "adddecision: $decision");
        AppExecutors.getInstance().diskIO().execute(() -> mDecisionsDao.addNewDecision(decision));
    }

    public LiveData<Decision> getDecision(int id) {
        return mDecisionsDao.getDecisionById(id);
    }

    public void updateDecision(@NotNull Decision decision) {
        Log.i("helpfix", "update1: $decision");
        AppExecutors.getInstance().diskIO().execute(() -> {
            mDecisionsDao.updateDecision(decision);
            Log.i("helpfix", "update2: $decision");
        });
    }
}
