package dar.life.helpers.simplifydecisions.repository;

import android.app.Application;
import android.content.Context;

import androidx.lifecycle.MutableLiveData;

public class DecisionsRepository {

    private static DecisionsRepository sInstance;
    private final Context mAppContext;

    private DecisionsRepository(Application application) {

        mAppContext = application.getApplicationContext();

    }

    /**
     * Creating an instance only once in an app lifetime. performed in a synced way.
     *
     * @param application = the App reference.
     * @return The repository singleton instance.
     */
    public static DecisionsRepository getInstance(Application application) {
        if (sInstance == null) {
            synchronized (DecisionsRepository.class) {
                if (sInstance == null)
                    sInstance = new DecisionsRepository(application);
            }
        }
        return sInstance;
    }

}
