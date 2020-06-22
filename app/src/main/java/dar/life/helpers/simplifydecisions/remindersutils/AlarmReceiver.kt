package dar.life.helpers.simplifydecisions.remindersutils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dar.life.helpers.simplifydecisions.NotificationsHelper
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * A [BroadcastReceiver] handling a Reminder Alarm and creating a notification if possible
 *
 */
class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null && intent?.action != null) {

            if (intent.action!!.equals(context.getString(R.string.action_goal_reminded), true)) {
                if (intent.extras != null) {

                    //Getting the reminder from the DB off the main thread
                    GlobalScope.launch(Dispatchers.IO) {
                        val decisions =
                            AppRepository.getInstance(context).allDecisionsNow
                        val reminder = decisions.flatMap { decision -> decision.goals }
                            .map { goal -> goal.reminder }
                            .find { it.id == Integer.valueOf(intent.type!!).toLong() }

                        //Creates a notification if a valid reminder has been found
                        reminder?.let {
                            NotificationsHelper.createNotification(
                                context,
                                it.title,
                                it.text,
                                it.id
                            )
                        }
                    }
                }
            }
        }
    }
}
