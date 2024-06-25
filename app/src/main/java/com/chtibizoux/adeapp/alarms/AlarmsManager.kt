package com.chtibizoux.adeapp.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.chtibizoux.adeapp.MyNotificationManager
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.BadHoursError
import com.chtibizoux.adeapp.data.Result
import com.chtibizoux.adeapp.data.SettingsRepository
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.data.User
import java.util.Calendar

class AlarmsManager(private val context: Context) {
    private val wakelockManager = WakeLockManager(context)
    private val alarmKlaxon = AlarmKlaxon(context)
    private val notificationManager = MyNotificationManager(context)

    companion object {
        const val ALARM_CREATOR_REQUEST_CODE = 10
        const val ALARM_REQUEST_CODE = 20
        const val UPDATE_HOUR = 14
    }

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    private val createAlarmPendingIntent = PendingIntent.getBroadcast(
        context,
        ALARM_CREATOR_REQUEST_CODE,
        Intent(context, AlarmsReceiver::class.java).apply {
            action = AlarmsReceiver.CREATE_ALARM_ACTION
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    private fun getAlarmIntent(time: Int): PendingIntent {
        val alarmIntent = Intent(context, AlarmsReceiver::class.java).apply {
            action = AlarmsReceiver.START_ALARM_ACTION
        }
        return PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE + time,
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun scheduleNextAlarmCreation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                context,
                context.getString(R.string.unable_to_set_exact_alarm),
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val calendar: Calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, UPDATE_HOUR)
            set(Calendar.MINUTE, 0)

            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis < Calendar.getInstance().timeInMillis) {
                add(Calendar.DATE, 1)
            }
        }
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            createAlarmPendingIntent
        )
    }

    fun removeAlarmCreatorSchedule() {
        alarmManager.cancel(createAlarmPendingIntent)
    }

    suspend fun createAlarmAndNotifyUser(
        repository: SettingsRepository,
        user: User,
        alarms: List<Alarm>,
        usePreviousAlarm: Boolean,
        notify: Boolean = true
    ) {
        try {
            val tomorrow = Calendar.getInstance()
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
                        createAlarm(time)
                    }
                    if (notify) {
                        notificationManager.showCreateAlarmSuccess(alarm.hours.size, result.data)
                    }
                } else {
                    if (notify) {
                        notificationManager.showNoAlarmError(result.data)
                    }
                }
            } else {
                if (result is Result.Error && result.exception.cause is BadHoursError) {
                    if (notify) {
                        notificationManager.showNoHoursError()
                    }
                } else {
                    notificationManager.showGetStartTimeError()
                    // Retry
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        30000,
                        createAlarmPendingIntent
                    )
                }
            }
        } catch (e: Exception) {
            notificationManager.showAlarmError(e)
        }
    }

    private fun createAlarm(time: Time) {
        val alarmIntent = Intent(context, AlarmsReceiver::class.java).apply {
            action = AlarmsReceiver.START_ALARM_ACTION
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE + time.getMinutesNumber(),
            alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val calendar: Calendar = Calendar.getInstance().apply {
//            Add a day if UPDATE_HOUR is before midnight
            add(Calendar.DATE, 1)
            set(Calendar.HOUR_OF_DAY, time.hour)
            set(Calendar.MINUTE, time.minute)

            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun deleteAlarms(alarm: Alarm) {
        for (time in alarm.hours) {
            alarmManager.cancel(getAlarmIntent(time.getMinutesNumber()))
        }
    }

    fun startAlarm() {
        stopAlarm()

        wakelockManager.acquire()

        notificationManager.showAlarmNotification()

        alarmKlaxon.start()

//        context.sendBroadcast(Intent(ALARM_ALERT_ACTION))
    }


    fun stopAlarm() {
        alarmKlaxon.stop()

//        context.sendBroadcast(Intent(ALARM_DONE_ACTION))

        notificationManager.cancelAlarmNotification()
//        service.stopForeground(STOP_FOREGROUND_REMOVE)

        val finishAlarmActivityIntent = Intent(AlarmActivity.FINISH_ALARM_ACTIVITY_ACTION)
        context.sendBroadcast(finishAlarmActivityIntent)

        wakelockManager.release()
    }

    fun snoozeAlarm() {
        stopAlarm()

        val calendar: Calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 10)
        }

        val numberOfMinutes =
            calendar.get(Calendar.MINUTE) + 60 * calendar.get(Calendar.HOUR_OF_DAY)

        notificationManager.showSnooze(Time(numberOfMinutes))

        val pendingIntent = getAlarmIntent(numberOfMinutes)

        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun cancelSnooze(time: Int) {
        alarmManager.cancel(getAlarmIntent(time))
        notificationManager.cancelSnoozeNotification()
    }
}