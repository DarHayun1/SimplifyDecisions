package dar.life.helpers.simplifydecisions

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dar.life.helpers.simplifydecisions.ui.MainActivity

object NotificationsHelper {

    fun createNotification(
        context: Context,
        title: String,
        text: CharSequence,
        id: Long
    ) {
        var builder =
            NotificationCompat.Builder(context, getChannelId(context)).apply {
                setSmallIcon(R.drawable.blue_light_avatar)
                setContentTitle(title)
                setContentText(text)
                priority = NotificationCompat.PRIORITY_DEFAULT

                val intent = Intent(context, MainActivity::class.java)
                intent.action = Constants.REMINDER_ACTION
                intent.type = id.toString()
                intent.putExtra(Constants.REMINDER_ID_KEY, id)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                val pendingIntent =
                    PendingIntent.getActivity(context, 0, intent, 0)
                setContentIntent(pendingIntent)
            }

        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(1001, builder.build())

    }

    fun createNotificationChannel(context: Context) {
        // Create the NotificationChannel, only on API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = context.getString(R.string.channel_name)
            val descriptionText = context.getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel =
                NotificationChannel(getChannelId(context), name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getChannelId(context: Context): String =
        "${context.packageName}-${context.getString(R.string.app_name)}"
}