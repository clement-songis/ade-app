package com.chtibizoux.adeapp

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.chtibizoux.adeapp.data.DataSource
import com.chtibizoux.adeapp.data.Result
import com.chtibizoux.adeapp.data.Settings
import com.chtibizoux.adeapp.data.SettingsRepository
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.Calendar
import java.util.Date

const val UPDATE_HOUR = 14
const val ALARM_MESSAGE = "ADE Alarm"

class AlarmReceiver : BroadcastReceiver() {
    companion object {
        fun setBackgroundWork(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val alarmIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager?.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 30000, alarmIntent)
//            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
//            // TODO: Check if not already exist
//            if (!alarmManager.) {
//                val intent = Intent(context, AlarmReceiver::class.java)
//                val alarmIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//
//                val calendar: Calendar = Calendar.getInstance().apply {
//                    timeInMillis = System.currentTimeMillis()
//                    set(Calendar.HOUR_OF_DAY, UPDATE_HOUR)
//                    // set(Calendar.MINUTES, 0)
//                }
//                alarmManager?.setInexactRepeating(
//                    AlarmManager.RTC_WAKEUP, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, alarmIntent
//                )
//            }
        }

        fun removeBackgroundWork(context: Context) {
            val intent = Intent(context, AlarmReceiver::class.java)
            val alarmIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            alarmManager?.cancel(alarmIntent)
        }

        suspend fun setAlarmAndNotifyUser(
            context: Context,
            repository: SettingsRepository,
            settings: Settings
        ) {
            Toast.makeText(context, "test", Toast.LENGTH_LONG).show()
//            setAlarm(context, Time(14, 0))
//            setAlarm(context, Time(15, 2))

//            TODO: Attention no today trigger
//            if (!alarmAlreadyInUse) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            try {
                val tomorrow = Calendar.getInstance()
                tomorrow.time = Date()
                tomorrow.add(Calendar.DATE, 1)
                if (settings.user == null) throw Error("No user")
                val result = repository.getStartingHour(settings.user, tomorrow.time)
                if (result is Result.Success) {
                    val alarm = settings.alarms.find { it.forHour == result.data }
                    if (alarm != null) {
                        for (time in alarm.hours) {
                            setAlarm(context, time)
                        }
                        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("Alarm Error")
                            .setContentText("${alarm.hours.size} alarm(s) set for ${result.data}")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                        notificationManager.notify(1, builder.build())
                    } else {
                        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle("Alarm Error")
                            .setContentText("No alarm set for this hour ${result.data}")
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                        notificationManager.notify(1, builder.build())
                    }
                } else {
                    val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle("Alarm Error")
                        .setContentText("Error getting starting hour from ade api")/* TODO: replace by string resource */
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                    notificationManager.notify(1, builder.build())
                }
            } catch (e: Exception) {
                val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle("Alarm Error")
                    .setContentText(e.toString())
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                notificationManager.notify(1, builder.build())
            }
//            }
        }

        private fun setAlarm(context: Context, time: Time) {
            val setIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                putExtra(AlarmClock.EXTRA_MESSAGE, ALARM_MESSAGE)
                putExtra(AlarmClock.EXTRA_HOUR, time.hour)
                putExtra(AlarmClock.EXTRA_MINUTES, time.minute)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            }
            if (setIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(setIntent)
            } else {
                Toast.makeText(context, "There is no app to support this action", Toast.LENGTH_LONG)
                    .show()
            }
        }

        fun dismissAlarm(context: Context) {
            val dismissIntent = Intent(AlarmClock.ACTION_DISMISS_ALARM).apply {
                putExtra(AlarmClock.EXTRA_ALARM_SEARCH_MODE, AlarmClock.ALARM_SEARCH_MODE_LABEL)
                putExtra(AlarmClock.EXTRA_MESSAGE, ALARM_MESSAGE)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            }
            if (dismissIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(dismissIntent)
            } else {
                Toast.makeText(context, "There is no app to support this action", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        runBlocking {
            val repository = SettingsRepository(context.dataStore, DataSource())
            val settings = repository.settings.first()
            if (settings.alarms.isNotEmpty()) {
                setAlarmAndNotifyUser(context, repository, settings)
            } else {
                removeBackgroundWork(context)
            }
        }
    }
}
