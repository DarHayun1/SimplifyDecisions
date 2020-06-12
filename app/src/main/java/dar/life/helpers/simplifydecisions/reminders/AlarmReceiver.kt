package dar.life.helpers.simplifydecisions.reminders

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dar.life.helpers.simplifydecisions.Constants
import dar.life.helpers.simplifydecisions.Constants.REMINDER_ID_KEY
import dar.life.helpers.simplifydecisions.NotificationsHelper
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.repository.AppExecutors
import dar.life.helpers.simplifydecisions.repository.AppRepository
import dar.life.helpers.simplifydecisions.ui.decisions.DecisionDetailsFragment
import dar.life.helpers.simplifydecisions.ui.decisions.DecisionDetailsFragment.Companion.REMINDER_ID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("notification", "onReceive")

        if (context != null && intent != null && intent.action != null) {

            if (intent.action!!.equals(
                    context.getString(R.string.action_goal_reminded),
                    true
                )
            ) {
                if (intent.extras != null) {
                    val keyId = intent.getLongExtra(REMINDER_ID_KEY, 0)
                    Log.d("notification", "keyid: $keyId")
                    GlobalScope.launch(Dispatchers.IO){
                        val decisions =
                            AppRepository.getInstance(context).allDecisionsNow
                        val reminder = decisions.flatMap { decision -> decision.goals }
                            .map { goal -> goal.reminder }
                            .find { it.id == Integer.valueOf(intent.type!!).toLong()}
                        decisions.forEach{
                            decision ->
                            decision.goals.forEach{goal ->
                                Log.d("notification", "${goal.reminder}")
                            }
                        }
                            if (reminder != null) {
                                //Removed mainthread call
                                NotificationsHelper.createNotification(
                                    context,
                                    reminder.title,
                                    reminder.text,
                                    reminder.id
                                )
                            }

                    }
                }
            }
        }
    }
}
