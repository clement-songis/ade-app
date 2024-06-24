package com.chtibizoux.adeapp

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.AlarmClock
import androidx.core.app.NotificationCompat
import com.chtibizoux.adeapp.alarms.AlarmActivity
import com.chtibizoux.adeapp.alarms.AlarmsReceiver
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.ui.home.alarms.NEW_ALARM_ACTION
import com.chtibizoux.adeapp.ui.home.alarms.TIME_EXTRA
import com.chtibizoux.adeapp.ui.home.alarms.VIEW_ALARMS_ACTION

class MyNotificationManager(private val context: Context) {
    companion object {
        const val ALARM_NOTIFICATION_ID = 1
        const val CREATE_ALARM_NOTIFICATION_ID = 2
        const val ALARM_ERROR_NOTIFICATION_ID = 3
        const val ALARM_SNOOZE_NOTIFICATION_ID = 4
    }

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun showAlarmNotification() {
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm))
            .setContentText("A super alarm")// TODO
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setWhen(0)
            .setOngoing(true)
            .setAutoCancel(false)
            .setLocalOnly(true)

        // Add Snooze action
        val snoozeIntent = Intent(context, AlarmsReceiver::class.java).apply {
            action = AlarmsReceiver.SNOOZE_ALARM_ACTION
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context, 0, snoozeIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(
            R.drawable.ic_snooze,
            context.getString(R.string.snooze),
            snoozePendingIntent
        )

        // Add Dismiss Action
        val dismissIntent = Intent(context, AlarmsReceiver::class.java).apply {
            action = AlarmsReceiver.STOP_ALARM_ACTION
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(
            R.drawable.ic_alarm_off,
            context.getString(R.string.cancel),
            dismissPendingIntent
        )

        // Setup Content Action
        val showIntent = Intent(context, AlarmActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val showPendingIntent = PendingIntent.getActivity(
            context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(showPendingIntent)

        // Setup fullscreen intent
        val fullScreenIntent = Intent(context, AlarmActivity::class.java).apply {
            // set action, so we can be different then content pending intent
            action = "fullscreen_activity"
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context, 0, fullScreenIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.setFullScreenIntent(fullScreenPendingIntent, true)

//        service.startForeground(1, builder.build())
        notificationManager.notify(ALARM_NOTIFICATION_ID, builder.build())
    }

    fun showCreateAlarmSuccess(alarmNumber: Number, forTime: Time) {
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_success))
            .setContentText(context.getString(R.string.alarm_success_text, alarmNumber, forTime))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        // Show alarms on notification click
        val showIntent = Intent(AlarmClock.ACTION_SHOW_ALARMS).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        }
        val showPendingIntent = PendingIntent.getActivity(
            context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(showPendingIntent)

        // Add delete action
        val deleteIntent = Intent(context, AlarmsReceiver::class.java).apply {
            action = AlarmsReceiver.DELETE_ALARM_ACTION
            putExtra(AlarmsReceiver.ALARM_EXTRA, forTime.getMinutesNumber())
        }
        val deletePendingIntent = PendingIntent.getBroadcast(
            context, 0, deleteIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(
            R.drawable.ic_cancel,
            context.getString(R.string.delete),
            deletePendingIntent
        )

        notificationManager.notify(CREATE_ALARM_NOTIFICATION_ID, builder.build())
    }

    fun showNoAlarmError(time: Time) {
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_error))
            .setContentText(context.getString(R.string.no_alarm_error, time))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show alarms on notification click
        val showIntent = Intent(context, MainActivity::class.java).apply {
            action = VIEW_ALARMS_ACTION
        }
        val showPendingIntent = PendingIntent.getActivity(
            context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(showPendingIntent)

        // Add add alarm action
        val addIntent = Intent(context, MainActivity::class.java).apply {
            action = NEW_ALARM_ACTION
            putExtra(TIME_EXTRA, time.toString())
        }
        val addPendingIntent = PendingIntent.getActivity(
            context, 0, addIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(R.drawable.ic_add, context.getString(R.string.add), addPendingIntent)

        notificationManager.notify(ALARM_ERROR_NOTIFICATION_ID, builder.build())
    }

    fun showNoHoursError() {
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_error))
            .setContentText(context.getString(R.string.no_calendar_error))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        // Show timetable on notification click
        val showIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        }
        val showPendingIntent = PendingIntent.getActivity(
            context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(showPendingIntent)

        notificationManager.notify(ALARM_ERROR_NOTIFICATION_ID, builder.build())
    }

    fun showGetStartTimeError() {
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_error))
            .setContentText(context.getString(R.string.get_alarm_error))
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        notificationManager.notify(ALARM_ERROR_NOTIFICATION_ID, builder.build())
    }

    fun showAlarmError(e: Exception) {
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.alarm_error))
            .setContentText(e.toString())
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val showIntent = Intent(context, MainActivity::class.java).apply {
            action = VIEW_ALARMS_ACTION
        }
        val showPendingIntent = PendingIntent.getActivity(
            context, 0, showIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.setContentIntent(showPendingIntent)

        val retryIntent = Intent(context, AlarmsReceiver::class.java)
        val retryPendingIntent = PendingIntent.getBroadcast(
            context, 0, retryIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(R.drawable.ic_sync, context.getString(R.string.retry), retryPendingIntent)

        notificationManager.notify(ALARM_ERROR_NOTIFICATION_ID, builder.build())
    }

    fun showSnooze(time: Time) {
        val builder = NotificationCompat.Builder(context, ALARMS_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(context.getString(R.string.snoozed, time))
            .setContentText(context.getString(R.string.turn_off_on_tap))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setOngoing(true)
            .setAutoCancel(false)
            .setLocalOnly(true)

        // Add Dismiss Action
        val dismissIntent = Intent(context, AlarmsReceiver::class.java).apply {
            action = AlarmsReceiver.CANCEL_SNOOZE_ALARM_ACTION
            putExtra(AlarmsReceiver.ALARM_EXTRA, time.getMinutesNumber())
        }
        val dismissPendingIntent = PendingIntent.getBroadcast(
            context, 0, dismissIntent, PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(
            R.drawable.ic_alarm_off,
            context.getString(R.string.cancel),
            dismissPendingIntent
        )

        notificationManager.notify(ALARM_SNOOZE_NOTIFICATION_ID, builder.build())
    }

    fun cancelAlarmNotification() {
        notificationManager.cancel(ALARM_NOTIFICATION_ID)
    }

    fun cancelSnoozeNotification() {
        notificationManager.cancel(ALARM_SNOOZE_NOTIFICATION_ID)
    }
}