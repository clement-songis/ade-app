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
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.DataSource
import com.chtibizoux.adeapp.data.Result
import com.chtibizoux.adeapp.data.SettingsRepository
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.data.User
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
            // TODO: test multiple alarms and add delays if necessary
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
//            intent.putExtra("state", "Main")
            val alarmIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

//            alarmManager?.setInexactRepeating(
//                AlarmManager.RTC_WAKEUP,
//                Date().time,
//                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
//                alarmIntent
//            )
            val calendar: Calendar = Calendar.getInstance().apply {
                timeInMillis = System.currentTimeMillis()
                set(Calendar.HOUR_OF_DAY, UPDATE_HOUR)
                // set(Calendar.MINUTES, 0)
            }
            alarmManager?.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                alarmIntent
            )
        }

        fun removeBackgroundWork(context: Context) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)
            val alarmIntent = PendingIntent.getBroadcast(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager?.cancel(alarmIntent)
        }

        suspend fun setAlarmAndNotifyUser(
            context: Context,
            repository: SettingsRepository,
            user: User,
            alarms: List<Alarm>,
            usePreviousAlarm: Boolean
        ) {
            Toast.makeText(context, "test", Toast.LENGTH_LONG).show()
//            TODO: Attention no today trigger
//            if (!alarmAlreadyInUse) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            try {
                val tomorrow = Calendar.getInstance()
                tomorrow.time = Date()
                tomorrow.add(Calendar.DATE, 1)
                val result = repository.getStartingTime(user, tomorrow.time)
                if (result is Result.Success) {
                    val alarm = if (usePreviousAlarm) {
                        alarms.sortedBy { it.forHour.getMinutesNumber() }.reversed()
                            .find { it.forHour.getMinutesNumber() <= result.data.getMinutesNumber() }
                    } else {
                        alarms.find { it.forHour.toString() == result.data.toString() }
                    }
                    if (alarm != null) {
                        for (time in alarm.hours) {
                            setAlarm(context, time)
                        }

                        val intent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                        }
                        val viewIntent = PendingIntent.getActivity(
                            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
                        )
//                        val cancelIntent = Intent(context, CancelAlarmReceiver::class.java)
//                        val cancelPendingIntent =
//                            PendingIntent.getBroadcast(
//                                context,
//                                0,
//                                cancelIntent,
//                                PendingIntent.FLAG_IMMUTABLE
//                            )
                        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(context.getString(R.string.alarm_success))
                            .setContentText(
                                context.getString(
                                    R.string.alarm_success_text,
                                    alarm.hours.size.toString(),
                                    result.data
                                )
                            )
//                            .addAction(
//                                R.drawable.ic_cancel,
//                                context.getString(R.string.cancel),
//                                cancelPendingIntent
//                            )
                            .setContentIntent(viewIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                        notificationManager.notify(1, builder.build())
                    } else {
//                        val viewIntent = Intent(context, MainActivity::class.java).apply {
//                            AlarmsPath
//                        }
//                        val viewPendingIntent = PendingIntent.getActivity(
//                            context,
//                            0,
//                            viewIntent,
//                            PendingIntent.FLAG_IMMUTABLE
//                        )
//                        val addIntent = Intent(context, MainActivity::class.java).apply {
//                            NewAlarmPath
//                        }
//                        val addPendingIntent = PendingIntent.getActivity(
//                            context,
//                            0,
//                            addIntent,
//                            PendingIntent.FLAG_IMMUTABLE
//                        )
                        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_foreground)
                            .setContentTitle(context.getString(R.string.alarm_error))
                            .setContentText(context.getString(R.string.no_alarm_error, result.data))
//                            .addAction(
//                                R.drawable.ic_add,
//                                context.getString(R.string.add),
//                                addPendingIntent
//                            )
//                            .setContentIntent(viewPendingIntent)
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                        notificationManager.notify(1, builder.build())
                    }
                } else {
                    val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setContentTitle(context.getString(R.string.alarm_error))
                        .setContentText(context.getString(R.string.get_alarm_error))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                    notificationManager.notify(1, builder.build())

//                    Retry
                    val alarmManager =
                        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                    val intent = Intent(context, AlarmReceiver::class.java)
                    val alarmIntent = PendingIntent.getBroadcast(
                        context,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )

                    alarmManager?.set(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP, 30000, alarmIntent
                    )
                }
            } catch (e: Exception) {
//                val viewIntent = Intent(context, MainActivity::class.java).apply {
//                    AlarmsPath
//                }
//                val viewPendingIntent =
//                    PendingIntent.getActivity(context, 0, viewIntent, PendingIntent.FLAG_IMMUTABLE)
                val retryIntent = Intent(context, AlarmReceiver::class.java)
                val retryPendingIntent = PendingIntent.getBroadcast(
                    context, 0, retryIntent, PendingIntent.FLAG_IMMUTABLE
                )
                val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(context.getString(R.string.alarm_error))
                    .setContentText(e.toString()).setPriority(NotificationCompat.PRIORITY_HIGH)
                    .addAction(
                        R.drawable.ic_sync, context.getString(R.string.retry), retryPendingIntent
                    )
//                    .setContentIntent(viewPendingIntent)
                notificationManager.notify(1, builder.build())
            }
//            }
        }

        private fun setAlarm(context: Context, time: Time) {
            val setIntent = Intent(AlarmClock.ACTION_SET_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
                putExtra(AlarmClock.EXTRA_MESSAGE, ALARM_MESSAGE)
                putExtra(AlarmClock.EXTRA_HOUR, time.hour)
                putExtra(AlarmClock.EXTRA_MINUTES, time.minute)
                putExtra(AlarmClock.EXTRA_SKIP_UI, true)
            }
            // If don't work use setFullScreenIntent on a notification
            if (setIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(setIntent)
            } else {
                Toast.makeText(context, "There is no app to support this action", Toast.LENGTH_LONG)
                    .show()
            }
        }

        fun dismissAlarm(context: Context) {
            val dismissIntent = Intent(AlarmClock.ACTION_DISMISS_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
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
            if (settings.alarms.isNotEmpty() && settings.user != null) {
                setAlarmAndNotifyUser(context, repository, settings.user, settings.alarms, settings.usePreviousAlarm)
            } else {
                removeBackgroundWork(context)
            }
        }
    }
}
