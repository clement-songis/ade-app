package com.chtibizoux.adeapp.alarms

import android.annotation.SuppressLint
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import com.chtibizoux.adeapp.BuildConfig
import com.chtibizoux.adeapp.data.Time

class AlarmService : Service() {
    companion object {
        const val START_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".START_ALARM"
        const val STOP_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".STOP_ALARM"
        const val SNOOZE_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".SNOOZE_ALARM"
        const val CANCEL_SNOOZE_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".CANCEL_SNOOZE_ALARM"
        const val SNOOZE_TIME_EXTRA = "snooze_time"
    }

    private lateinit var alarmsManager: AlarmsManager

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                SNOOZE_ALARM_ACTION -> {
                    alarmsManager.snoozeAlarm()
                }

                CANCEL_SNOOZE_ALARM_ACTION -> {
                    val time = Time.fromString(intent.extras?.getString(SNOOZE_TIME_EXTRA) ?: "")
                    if (time != null) {
                        alarmsManager.cancelSnooze(time)
                    }
                }

                STOP_ALARM_ACTION -> {
                    alarmsManager.stopAlarm()
                }

                else -> {
                    throw Error("Bad action ${intent.action}")
                }
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate() {
        super.onCreate()

        alarmsManager = AlarmsManager(this)

        val intentFilter = IntentFilter().apply {
            addAction(STOP_ALARM_ACTION)
            addAction(SNOOZE_ALARM_ACTION)
            addAction(CANCEL_SNOOZE_ALARM_ACTION)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                broadcastReceiver,
                intentFilter,
                RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(broadcastReceiver, intentFilter)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val state = super.onStartCommand(intent, flags, startId)

        if (intent?.action == START_ALARM_ACTION) {
            alarmsManager.startAlarm()
        } else {
            throw Error("Bad action ${intent?.action}")
        }

        return state
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}