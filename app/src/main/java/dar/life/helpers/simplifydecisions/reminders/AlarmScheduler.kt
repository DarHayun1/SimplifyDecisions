/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package dar.life.helpers.simplifydecisions.reminders

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import dar.life.helpers.simplifydecisions.R
import dar.life.helpers.simplifydecisions.data.ReminderObj
import dar.life.helpers.simplifydecisions.ui.decisions.DecisionDetailsFragment
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*
import java.util.Calendar.*

/**
 * Helpers to assist in scheduling alarms for ReminderData.
 */
object AlarmScheduler {

    /**
     * Schedules all the alarms for [ReminderData].
     *
     * @param context      current application context
     * @param reminder ReminderData to use for the alarm
     */
    fun scheduleAlarmsForReminder(context: Context, reminder: ReminderObj) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // get the PendingIntent for the alarm
        val alarmIntent = createPendingIntent(context, reminder)

        scheduleAlarm(reminder, alarmIntent, alarmMgr)


    }


    /**
     * Schedules a single alarm
     */
    private fun scheduleAlarm(
        reminderData: ReminderObj,
        alarmIntent: PendingIntent?,
        alarmMgr: AlarmManager
    ) {
        Log.d("notification", "scheduleAlarm, $reminderData")

        val cal = GregorianCalendar.from(
            ZonedDateTime.of(reminderData.time, ZoneId.systemDefault())
        )
        alarmMgr.set(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis, alarmIntent
        )
        return
    }

    /**
     * Creates a [PendingIntent] for the Alarm using the [ReminderData]
     *
     * @param context      current application context
     * @param reminder ReminderData for the notification
     */
    private fun createPendingIntent(
        context: Context,
        reminder: ReminderObj
    ): PendingIntent? {
        // create the intent using a unique type
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java).apply {
          action = context.getString(R.string.action_goal_reminded)
          type = reminder.id.toString()
          putExtra(DecisionDetailsFragment.REMINDER_ID, reminder.id)
        }

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Determines if the Alarm should be scheduled for today.
     *
     * @param dayOfWeek day of the week as an Int
     * @param today today's datetime
     * @param datetimeToAlarm Alarm's datetime
     */
    private fun shouldNotifyToday(
        dayOfWeek: Int,
        today: Calendar,
        datetimeToAlarm: Calendar
    ): Boolean {
        return dayOfWeek == today.get(DAY_OF_WEEK) &&
                today.get(HOUR_OF_DAY) <= datetimeToAlarm.get(HOUR_OF_DAY) &&
                today.get(MINUTE) <= datetimeToAlarm.get(MINUTE)
    }

    /**
     * Updates a notification.
     * Note: this just calls [AlarmScheduler.scheduleAlarmsForReminder] since
     * alarms with exact matching pending intents will update if they are already set, otherwise
     * call [AlarmScheduler.removeAlarmsForReminder] if the medicine has been administered.
     *
     * @param context      current application context
     * @param reminderData ReminderData for the notification
     */
    fun updateAlarmsForReminder(context: Context, reminderData: ReminderObj) {
        scheduleAlarmsForReminder(context, reminderData)
    }

    /**
     * Removes the notification if it was previously scheduled.
     *
     * @param context      current application context
     * @param reminderData ReminderData for the notification
     */
    fun removeAlarmsForReminder(context: Context, reminderData: ReminderObj) {
        val intent = Intent(context.applicationContext, AlarmReceiver::class.java)
        intent.putExtra(DecisionDetailsFragment.REMINDER_ID, reminderData.id)

        val type = String.format(
            Locale.getDefault(),
            "%s-%s-%s",
            reminderData.title,
            reminderData.time,
            reminderData.id
        )

        intent.type = type
        val alarmIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmMgr.cancel(alarmIntent)
    }


    /**
     * Returns the int representation for the day of the week.
     *
     * @param days      array from resources
     * @param dayOfWeek String representation of the day e.g "Sunday"
     * @return [Calendar.DAY_OF_WEEK] for given dayOfWeek
     */
    private fun getDayOfWeek(days: Array<String>, dayOfWeek: String): Int {
        return when {
            dayOfWeek.equals(days[0], ignoreCase = true) -> SUNDAY
            dayOfWeek.equals(days[1], ignoreCase = true) -> MONDAY
            dayOfWeek.equals(days[2], ignoreCase = true) -> TUESDAY
            dayOfWeek.equals(days[3], ignoreCase = true) -> WEDNESDAY
            dayOfWeek.equals(days[4], ignoreCase = true) -> THURSDAY
            dayOfWeek.equals(days[5], ignoreCase = true) -> FRIDAY
            else -> SATURDAY
        }
    }

}