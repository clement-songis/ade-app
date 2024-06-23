package com.chtibizoux.adeapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.core.app.NotificationCompat
import com.chtibizoux.adeapp.data.Time

class MyNotificationManager(private val context: Context) {
    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun sendAlarm(alarmNumber: Number, forTime: Time, fullscreenIntent: PendingIntent) {
        val showIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        }
        val showPendingIntent = PendingIntent.getActivity(
            context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val cancelIntent = Intent(context, SetAlarmReceiver::class.java)
        cancelIntent.action = CANCEL_ALARM_ACTION
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_success))
            .setContentText(context.getString(R.string.alarm_success_text, alarmNumber, forTime))
            .addAction(R.drawable.ic_cancel, context.getString(R.string.cancel), cancelPendingIntent)
            .setContentIntent(showPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setFullScreenIntent(fullscreenIntent, true)
        notificationManager.notify(1, builder.build())
    }

    fun sendAlarmSuccess(alarmNumber: Number, forTime: Time) {
        val showIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        }
        val showPendingIntent = PendingIntent.getActivity(
            context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val cancelIntent = Intent(context, SetAlarmReceiver::class.java)
        cancelIntent.action = CANCEL_ALARM_ACTION
        val cancelPendingIntent = PendingIntent.getBroadcast(
            context, 0, cancelIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_success))
            .setContentText(context.getString(R.string.alarm_success_text, alarmNumber, forTime))
            .addAction(R.drawable.ic_cancel, context.getString(R.string.cancel), cancelPendingIntent)
            .setContentIntent(showPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(1, builder.build())
    }

    fun sendNoAlarmError(time: Time) {
        val showIntent = Intent(context, MainActivity::class.java).apply {
            action = VIEW_ALARMS_ACTION
        }
        val showPendingIntent = PendingIntent.getActivity(
            context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val addIntent = Intent(context, MainActivity::class.java).apply {
            action = NEW_ALARM_ACTION
            putExtra(TIME_EXTRA, time.toString())
        }
        val addPendingIntent = PendingIntent.getActivity(
            context, 0, addIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_error))
            .setContentText(context.getString(R.string.no_alarm_error, time))
            .addAction(
                R.drawable.ic_add,
                context.getString(R.string.add),
                addPendingIntent
            )
            .setContentIntent(showPendingIntent).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        notificationManager.notify(1, builder.build())
    }

    fun sendBadHoursError() {
        val showIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        }
        val showPendingIntent = PendingIntent.getActivity(
            context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_error))
            .setContentText(context.getString(R.string.no_calendar_error))
            .setContentIntent(showPendingIntent).setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
        notificationManager.notify(1, builder.build())
    }

    fun sendGetAlarmError() {
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_error))
            .setContentText(context.getString(R.string.get_alarm_error))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        notificationManager.notify(1, builder.build())
    }

    fun sendAlarmError(e: Exception) {
        val showIntent = Intent(context, MainActivity::class.java).apply {
            action = VIEW_ALARMS_ACTION
        }
        val showPendingIntent =
            PendingIntent.getActivity(context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE)
        val retryIntent = Intent(context, SetAlarmReceiver::class.java)
        val retryPendingIntent = PendingIntent.getBroadcast(
            context, 0, retryIntent, PendingIntent.FLAG_IMMUTABLE
        )
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_error)).setContentText(e.toString())
            .setPriority(NotificationCompat.PRIORITY_HIGH).addAction(
                R.drawable.ic_sync, context.getString(R.string.retry), retryPendingIntent
            ).setContentIntent(showPendingIntent).setAutoCancel(true)
        notificationManager.notify(1, builder.build())
    }
}