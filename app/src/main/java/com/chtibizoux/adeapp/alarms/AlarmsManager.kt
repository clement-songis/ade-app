package com.chtibizoux.adeapp.alarms

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.app.Service.STOP_FOREGROUND_REMOVE
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.chtibizoux.adeapp.MyNotificationManager
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Time
import java.util.Calendar

class AlarmsManager(private val service: Service) {
    companion object {
        fun getAlarmIntent(time: Int, context: Context): PendingIntent {
            val alarmIntent = Intent(context, AlarmService::class.java).apply {
                action = AlarmService.START_ALARM_ACTION
            }
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                PendingIntent.getForegroundService(
                    context,
                    CreateAlarmsManager.ALARM_REQUEST_CODE + time,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            } else {
                PendingIntent.getService(
                    context,
                    CreateAlarmsManager.ALARM_REQUEST_CODE + time,
                    alarmIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            }
        }
    }

    private val wakelockManager = WakeLockManager(service)
    private val alarmKlaxon = AlarmKlaxon(service)
    private val notificationManager = MyNotificationManager(service)

    private val alarmManager = service.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun startAlarm() {
        stopAlarm()
        notificationManager.cancelSnoozeNotification()

        wakelockManager.acquire()

        notificationManager.showAlarmNotification()

        alarmKlaxon.start()

//        context.sendBroadcast(Intent(ALARM_ALERT_ACTION))
    }


    fun stopAlarm() {
        alarmKlaxon.stop()

//        context.sendBroadcast(Intent(ALARM_DONE_ACTION))

        service.stopForeground(STOP_FOREGROUND_REMOVE)

        val finishAlarmActivityIntent = Intent(AlarmActivity.FINISH_ALARM_ACTIVITY_ACTION)
        service.sendBroadcast(finishAlarmActivityIntent)

        wakelockManager.release()
    }

    fun snoozeAlarm() {
        stopAlarm()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                service,
                service.getString(R.string.unable_to_set_exact_alarm),
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val calendar: Calendar = Calendar.getInstance().apply {
            add(Calendar.MINUTE, 10)
        }

        val numberOfMinutes = calendar.get(Calendar.MINUTE) + 60 * calendar.get(Calendar.HOUR_OF_DAY)

        notificationManager.showSnooze(Time(numberOfMinutes))

        val pendingIntent = getAlarmIntent(numberOfMinutes, service)

        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, pendingIntent)
        alarmManager.setAlarmClock(alarmClockInfo, pendingIntent)
    }

    fun cancelSnooze(time: Int) {
        alarmManager.cancel(getAlarmIntent(time, service))
        notificationManager.cancelSnoozeNotification()
    }
}