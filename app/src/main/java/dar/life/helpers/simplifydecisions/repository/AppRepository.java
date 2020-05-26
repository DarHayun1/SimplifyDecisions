package dar.life.helpers.simplifydecisions.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import dar.life.helpers.simplifydecisions.data.Decision;
import dar.life.helpers.simplifydecisions.data.Issue;
import dar.life.helpers.simplifydecisions.data.ReminderObj;

public class AppRepository {

    private static AppRepository sInstance;
    private final IssuesDao mIssuesDao;
    private final DecisionsDao mDecisionsDao;
    private final RemindersDao mRemindersDao;

    private final LiveData<List<Issue>> mIssues;
    private final LiveData<List<Issue>> mActiveIssues;

    private final LiveData<List<Decision>> mDecisions;



    private AppRepository(Context context) {

        IssuesDatabase db = Room.databaseBuilder(
                context.getApplicationContext(),
                IssuesDatabase.class, IssuesDatabase.DB_NAME
        ).build();

        mIssuesDao = db.issuesDao();
        mDecisionsDao = db.decisionsDao();
        mRemindersDao = db.remindersDao();

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

    public LiveData<Issue> getIssue(int requestedId) {
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

    public void addNewReminder(@NotNull ReminderObj reminder) {
        AppExecutors.getInstance().diskIO().execute(() -> mRemindersDao.addNewReminder(reminder));
    }

    public @Nullable ReminderObj getReminderById(int id) {
        return mRemindersDao.getReminderById(id);
    }
}
