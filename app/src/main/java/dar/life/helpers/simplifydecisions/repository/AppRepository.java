package dar.life.helpers.simplifydecisions.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import dar.life.helpers.simplifydecisions.data.Decision;
import dar.life.helpers.simplifydecisions.data.Issue;

public class AppRepository {

    private static AppRepository sInstance;
    private final IssuesDao mIssuesDao;
    private final DecisionsDao mDecisionsDao;

    private final LiveData<List<Issue>> mIssues;
    private final LiveData<List<Issue>> mActiveIssues;

    private final LiveData<List<Decision>> mDecisions;



    private AppRepository(Application application) {

        IssuesDatabase db = Room.databaseBuilder(
                application.getApplicationContext(),
                IssuesDatabase.class, IssuesDatabase.DB_NAME
        ).build();

        mIssuesDao = db.issuesDao();
        mDecisionsDao = db.decisionsDao();


        mActiveIssues = mIssuesDao.getAllActiveIssues();
        mIssues = mIssuesDao.getAllIssues();

        mDecisions = mDecisionsDao.getAllDecisions();


    }

    /**
     * Creating an instance only once in an app lifetime. performed in a synced way.
     *
     * @param application = the App reference.
     * @return The repository singleton instance.
     */
    public static AppRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (AppRepository.class) {
                if (sInstance == null)
                    sInstance = new AppRepository(application);
            }
        }
        return sInstance;
    }

    @NotNull
    public LiveData<List<Issue>> getAllIssues() {
        return mIssues;
    }

    @NotNull
    public LiveData<List<Decision>> getAllDecisions() {
        return mDecisions;
    }

    @NotNull
    public LiveData<List<Issue>> getAllActiveIssues() {
        return mActiveIssues;
    }

    @Nullable
    public LiveData<Issue> getIssue(String requestedId) {
        return mIssuesDao.getIssueById(requestedId);
    }

    public void addNewIssue(@NotNull final Issue issue) {
        AppExecutors.getInstance().diskIO().execute(() -> mIssuesDao.addNewIssue(issue));
    }

    public void updateIssue(@NotNull Issue issue) {
        AppExecutors.getInstance().diskIO().execute(() -> mIssuesDao.updateIssue(issue));
    }

    public void addNewDecision(@NotNull Decision decision) {
        AppExecutors.getInstance().diskIO().execute(() -> mDecisionsDao.addNewDecision(decision));

    }

    public LiveData<Decision> getDecision(int id) {
        return mDecisionsDao.getDecisionById(id);
    }

    public void updateDecision(@NotNull Decision decision) {
        AppExecutors.getInstance().diskIO().execute(() -> mDecisionsDao.updateDecision(decision));
    }
}
