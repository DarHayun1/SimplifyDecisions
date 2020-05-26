package dar.life.helpers.simplifydecisions.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dar.life.helpers.simplifydecisions.NotificationsHelper
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.repository.AppExecutors
import dar.life.helpers.simplifydecisions.repository.AppRepository
import dar.life.helpers.simplifydecisions.ui.decisions.DecisionDetailsFragment
import dar.life.helpers.simplifydecisions.ui.decisions.DecisionDetailsFragment.Companion.REMINDER_ID

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("remindernot", "onRecive with $context and ${intent?.extras?.get(REMINDER_ID)}")
        if (context != null && intent != null && intent.action != null) {
            Log.d("remindernot", "onRecive2 with ${intent.action}")

            if (intent.action!!.equals(
                    context.getString(R.string.action_goal_reminded),
                    true
                )
            ) {
                Log.d("remindernot", "onRecive3")
                if (intent.extras != null) {
                    Log.d("remindernot", "on4")
                    AppExecutors.getInstance().diskIO().execute{
                        val reminder =
                            AppRepository.getInstance(context).getReminderById(
                                Integer.valueOf(intent.type!!)
                            )
                        Log.d("remindernot", "AlarmReciver, $reminder")
                        AppExecutors.getInstance().mainThread().execute{
                            if (reminder != null) {
                                NotificationsHelper.createNotification(
                                    context,
                                    reminder.title,
                                    reminder.text
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
