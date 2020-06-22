package dar.life.helpers.simplifydecisions.remindersutils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dar.life.helpers.simplifydecisions.Constants.REMINDER_ID_KEY
import dar.life.helpers.simplifydecisions.NotificationsHelper
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        if (context != null && intent != null && intent.action != null) {

            if (intent.action!!.equals(
                    context.getString(R.string.action_goal_reminded),
                    true
                )
            ) {
                if (intent.extras != null) {
                    GlobalScope.launch(Dispatchers.IO){
                        val decisions =
                            AppRepository.getInstance(context).allDecisionsNow
                        val reminder = decisions.flatMap { decision -> decision.goals }
                            .map { goal -> goal.reminder }
                            .find { it.id == Integer.valueOf(intent.type!!).toLong()}
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
