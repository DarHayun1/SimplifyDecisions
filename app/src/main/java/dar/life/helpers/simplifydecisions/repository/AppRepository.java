package dar.life.helpers.simplifydecisions.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import dar.life.helpers.simplifydecisions.data.Issue;

public class AppRepository {

    private static AppRepository sInstance;
    private final Context mAppContext;
    private final IssuesDao mDao;
    private final LiveData<List<Issue>> mIssues;

    private AppRepository(Application application) {

        mAppContext = application.getApplicationContext();

        IssuesDatabase db = Room.databaseBuilder(
                mAppContext,
                IssuesDatabase.class, IssuesDatabase.DB_NAME
        ).build();

        mDao = db.issuesDao();

        mIssues = mDao.getAllIssues();

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

    public void addNewIssue(@NotNull final Issue issue) {
        AppExecutors.getInstance().diskIO().execute( () -> mDao.addNewIssue(issue));
    }
}
