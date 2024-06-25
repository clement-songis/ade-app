package com.chtibizoux.adeapp.alarms

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.chtibizoux.adeapp.BuildConfig
import com.chtibizoux.adeapp.data.DataSource
import com.chtibizoux.adeapp.data.SettingsRepository
import com.chtibizoux.adeapp.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class AlarmsReceiver : BroadcastReceiver() {
    companion object {
        const val CREATE_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".CREATE_ALARM"
        const val DELETE_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".DELETE_ALARM"
        const val ALARM_EXTRA = "alarm"

        fun enable(context: Context) {
            val receiver = ComponentName(context, AlarmsReceiver::class.java)
            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        fun disable(context: Context) {
            val receiver = ComponentName(context, AlarmsReceiver::class.java)
            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val createAlarmsManager = CreateAlarmsManager(context)
        when (intent.action) {
            CREATE_ALARM_ACTION -> {
                val repository = SettingsRepository(context.dataStore, DataSource())
                runBlocking {
                    val settings = repository.settings.first()
                    if (settings.alarms.isNotEmpty() && settings.user != null) {
                        createAlarmsManager.createAlarmAndNotifyUser(
                            repository,
                            settings.user,
                            settings.alarms,
                            settings.usePreviousAlarm
                        )
                        createAlarmsManager.scheduleNextAlarmCreation()
                    }
                }
            }

            DELETE_ALARM_ACTION -> {
                val repository = SettingsRepository(context.dataStore, DataSource())
                runBlocking {
                    val settings = repository.settings.first()
                    val alarm = settings.alarms.find {
                        it.forHour.getMinutesNumber() == intent.extras?.getInt(ALARM_EXTRA)
                    }
                    if (alarm != null) {
                        createAlarmsManager.deleteAlarms(alarm)
                    }
                }
            }

            "android.intent.action.BOOT_COMPLETED" -> {
                val repository = SettingsRepository(context.dataStore, DataSource())
                runBlocking {
                    val settings = repository.settings.first()
                    if (settings.alarms.isNotEmpty() && settings.user != null) {
                        createAlarmsManager.scheduleNextAlarmCreation()
                        createAlarmsManager.createAlarmAndNotifyUser(
                            repository,
                            settings.user,
                            settings.alarms,
                            settings.usePreviousAlarm
                        )
                    } else {
                        disable(context)
                    }
                }
            }

            else -> {
                throw Error("Bad action ${intent.action}")
            }
        }

    }
}