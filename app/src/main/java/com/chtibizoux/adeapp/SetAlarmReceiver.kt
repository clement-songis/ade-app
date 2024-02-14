package com.chtibizoux.adeapp

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import android.widget.Toast
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.BadHoursError
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
import java.util.TimeZone

const val UPDATE_HOUR = 14
const val ALARM_MESSAGE = "ADE Alarm"

const val SET_ALARM_ACTION = "com.chtibizoux.adeapp.SET_ALARM"
const val CANCEL_ALARM_ACTION = "com.chtibizoux.adeapp.CANCEL_ALARM"
const val VIEW_ALARMS_ACTION = "com.chtibizoux.adeapp.VIEW_ALARMS"
const val NEW_ALARM_ACTION = "com.chtibizoux.adeapp.VIEW_ALARMS"
const val TIME_EXTRA = "time"

class SetAlarmReceiver : BroadcastReceiver() {
    companion object {
        fun setBackgroundWork(context: Context) {
            // TODO: Solve bugs
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
            val intent = Intent(context, SetAlarmReceiver::class.java)
            intent.action = SET_ALARM_ACTION
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
//                set(Calendar.HOUR_OF_DAY, UPDATE_HOUR)
//                set(Calendar.MINUTE, 0)
                set(Calendar.HOUR_OF_DAY, 14)
                set(Calendar.MINUTE, 0)

                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
                timeZone = TimeZone.getTimeZone("UTC")
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
            val intent = Intent(context, SetAlarmReceiver::class.java)
//            intent.action = SET_ALARM_ACTION
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
            return
//            TODO: Attention no today trigger
            // TODO: test multiple alarms and add delays if necessary
//            if (!alarmAlreadyInUse) {

            // ########### test ###########
            val notificationManager = MyNotificationManager(context)
            val viewIntent = Intent(context, MainActivity::class.java)
            val viewPendingIntent = PendingIntent.getActivity(
                context, 0, viewIntent, PendingIntent.FLAG_IMMUTABLE
            )
            notificationManager.sendAlarm(1, Time(0, 0), viewPendingIntent)
//            notificationManager.sendAlarmSuccess(1, Time(0, 0))
            setAlarm(context, Time(0, 0))
            // ########### test end ###########

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
                        alarms.find { it.forHour.getMinutesNumber() == result.data.getMinutesNumber() }
                    }
                    if (alarm != null) {
                        for (time in alarm.hours) {
                            setAlarm(context, time)
                        }
                        notificationManager.sendAlarmSuccess(alarm.hours.size, result.data)
                    } else {
                        notificationManager.sendNoAlarmError(result.data)
                    }
                } else {
                    if (result is Result.Error && result.exception.cause is BadHoursError) {
                        notificationManager.sendBadHoursError()
                    } else {
                        notificationManager.sendGetAlarmError()
//                    Retry
                        val alarmManager =
                            context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
                        val intent = Intent(context, SetAlarmReceiver::class.java)
                        intent.action = SET_ALARM_ACTION
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
                }
            } catch (e: Exception) {
                notificationManager.sendAlarmError(e)
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
        when (intent.action) {
            SET_ALARM_ACTION -> runBlocking {
                val repository = SettingsRepository(context.dataStore, DataSource())
                val settings = repository.settings.first()
                if (settings.alarms.isNotEmpty() && settings.user != null) {
                    setAlarmAndNotifyUser(
                        context,
                        repository,
                        settings.user,
                        settings.alarms,
                        settings.usePreviousAlarm
                    )
                } else {
                    removeBackgroundWork(context)
                }
            }
            CANCEL_ALARM_ACTION -> {
                dismissAlarm(context)
            }
            else -> {
                throw Error("Bad action")
            }
        }
    }
}
