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

const val CREATE_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".CREATE_ALARM"
const val DELETE_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".DELETE_ALARM"
const val START_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".START_ALARM"
const val SNOOZE_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".SNOOZE_ALARM"
const val STOP_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".STOP_ALARM"

const val VIEW_ALARMS_ACTION = BuildConfig.APPLICATION_ID + ".VIEW_ALARMS"
const val NEW_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".NEW_ALARM"

const val TIME_EXTRA = "time"

class AlarmsReceiver : BroadcastReceiver() {
    companion object {
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
        val alarmsManager = AlarmsManager(context)
        val repository = SettingsRepository(context.dataStore, DataSource())
        runBlocking {
            val settings = repository.settings.first()
            when (intent.action) {
                CREATE_ALARM_ACTION -> runBlocking {
                    if (settings.alarms.isNotEmpty() && settings.user != null) {
                        alarmsManager.createAlarmAndNotifyUser(
                            repository,
                            settings.user,
                            settings.alarms,
                            settings.usePreviousAlarm
                        )
                        alarmsManager.scheduleNextAlarmCreation()
                    }
                }

                DELETE_ALARM_ACTION -> {
                    val alarm = settings.alarms.find {
                        it.forHour.getMinutesNumber() == intent.extras?.getInt("alarm")
                    }
                    if (alarm != null) {
                        alarmsManager.deleteAlarms(alarm)
                    }
                }

                START_ALARM_ACTION -> {
                    alarmsManager.startAlarm()
                }

                SNOOZE_ALARM_ACTION -> {

                }

                STOP_ALARM_ACTION -> {
                    println("STOP")
                    alarmsManager.stopAlarm()
                }

                "android.intent.action.BOOT_COMPLETED" -> {
                    if (settings.alarms.isNotEmpty() && settings.user != null) {
                        alarmsManager.scheduleNextAlarmCreation()
                        alarmsManager.createAlarmAndNotifyUser(
                            repository,
                            settings.user,
                            settings.alarms,
                            settings.usePreviousAlarm
                        )
                    } else {
                        disable(context)
                    }
                }

                else -> {
                    throw Error("Bad action")
                }
            }
        }
    }
}