package com.chtibizoux.adeapp

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.chtibizoux.adeapp.data.DataSource
import com.chtibizoux.adeapp.data.SettingsRepository
import com.chtibizoux.adeapp.data.dataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class BootReceiver : BroadcastReceiver() {
    companion object {
        fun enable(context: Context) {
            val receiver = ComponentName(context, BootReceiver::class.java)
            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }

        fun disable(context: Context) {
            val receiver = ComponentName(context, BootReceiver::class.java)
            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            runBlocking {
                val repository = SettingsRepository(context.dataStore, DataSource())
                val settings = repository.settings.first()
                if (settings.alarms.isNotEmpty() && settings.user != null) {
                    SetAlarmReceiver.setBackgroundWork(context)
                    SetAlarmReceiver.setAlarmAndNotifyUser(context, repository, settings.user, settings.alarms, settings.usePreviousAlarm)
                } else {
                    disable(context)
                }
            }
        }
    }
}