package dar.life.helpers.simplifydecisions

import android.app.Application
import androidx.core.app.NotificationManagerCompat

class SimplifyDecisions: Application() {

    companion object {
        lateinit var instance: SimplifyDecisions
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this

        NotificationsHelper.createNotificationChannel(this)
    }
}