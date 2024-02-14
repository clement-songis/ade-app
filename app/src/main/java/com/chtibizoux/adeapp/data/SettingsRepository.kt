package com.chtibizoux.adeapp.data

import androidx.datastore.core.DataStore
import com.chtibizoux.adeapp.data.xml.Calendar
import com.chtibizoux.adeapp.data.xml.ResourceTree
import java.util.Date

class SettingsRepository(
    private val dataStore: DataStore<Settings>, private val dataSource: DataSource
) {
    val settings = dataStore.data

    suspend fun clearAll() {
        dataStore.updateData { Settings() }
    }

    suspend fun logout() {
        dataStore.updateData { settings -> settings.copy(user = null, calendar = null) }
    }

    suspend fun login(user: User): Boolean {
        // TODO: Check connection
        dataStore.updateData { settings -> settings.copy(user = user) }
        return true
    }

    suspend fun closeStartup() {
        dataStore.updateData { settings -> settings.copy(setupAlarms = false) }
    }

    suspend fun addAlarm(alarm: Alarm) {
        dataStore.updateData { settings ->
            settings.copy(alarms = (settings.alarms + alarm).sortedBy { it.forHour.getMinutesNumber() })
        }
    }

    suspend fun updateLabel(i: Int, label: String) {
        dataStore.updateData { settings ->
            val alarms = settings.alarms.toMutableList()
            alarms[i] = alarms[i].copy(label = label)
            settings.copy(alarms = alarms)
        }
    }

    suspend fun updateForHour(i: Int, time: Time) {
        dataStore.updateData { settings ->
            val alarms = settings.alarms.toMutableList()
            alarms[i] = alarms[i].copy(forHour = time)
            settings.copy(alarms = alarms.sortedBy { it.forHour.getMinutesNumber() })
        }
    }

    suspend fun addTime(i: Int, time: Time) {
        dataStore.updateData { settings ->
            val alarms = settings.alarms.toMutableList()
            alarms[i] =
                alarms[i].copy(hours = (alarms[i].hours + time).sortedBy { it.getMinutesNumber() })
            settings.copy(alarms = alarms)
        }
    }

    suspend fun updateTime(i: Int, hourIndex: Int, time: Time) {
        dataStore.updateData { settings ->
            val alarms = settings.alarms.toMutableList()
            val hours = alarms[i].hours.toMutableList()
            hours[hourIndex] = time
            alarms[i] = alarms[i].copy(hours = hours.sortedBy { it.getMinutesNumber() })
            settings.copy(alarms = alarms)
        }
    }

    suspend fun removeTime(i: Int, time: Time) {
        dataStore.updateData { settings ->
            val alarms = settings.alarms.toMutableList()
            alarms[i] = alarms[i].copy(hours = alarms[i].hours - time)
            settings.copy(alarms = alarms)
        }
    }

    suspend fun removeAlarm(alarm: Alarm) {
        dataStore.updateData { settings ->
            settings.copy(alarms = settings.alarms - alarm)
        }
    }

    suspend fun setAlarms(alarms: List<Alarm>) {
        dataStore.updateData { settings -> settings.copy(alarms = alarms, setupAlarms = false) }
    }

    suspend fun getStartingTimes(user: User): Result<List<Time>> {
        return dataSource.getStartingTimes(user)
    }

    suspend fun getStartingTime(user: User, date: Date): Result<Time> {
        return dataSource.getStartingTime(user, date)
    }

    suspend fun getCalendar(user: User, resourceId: Int): Result<Calendar> {
        return dataSource.getCalendar(user.copy(resourceId = resourceId))
    }

    suspend fun getResources(user: User): Result<ResourceTree> {
        return dataSource.getResources(user)
    }

    suspend fun updateCalendar(user: User): Boolean {
        val result = dataSource.getCalendar(user)
        if (result is Result.Success) {
            dataStore.updateData { settings -> settings.copy(calendar = result.data) }
        }
        return result is Result.Success
    }

    suspend fun setDefaultAlarmSettings(alarmSettings: DefaultAlarmSettings) {
        dataStore.updateData { settings ->
            settings.copy(defaultAlarmSettings = alarmSettings)
        }
    }

    suspend fun setUsePreviousAlarm(active: Boolean) {
        dataStore.updateData { settings ->
            settings.copy(usePreviousAlarm = active)
        }
    }

    suspend fun updateRepeat(repeat: Int) {
        dataStore.updateData { settings ->
            settings.copy(defaultAlarmSettings = settings.defaultAlarmSettings.copy(repeat = repeat))
        }
    }

    suspend fun updateInterval(interval: Int) {
        dataStore.updateData { settings ->
            settings.copy(defaultAlarmSettings = settings.defaultAlarmSettings.copy(interval = interval))
        }
    }

    suspend fun updateTimeUntilEvent(timeUntilEvent: Int) {
        dataStore.updateData { settings ->
            settings.copy(defaultAlarmSettings = settings.defaultAlarmSettings.copy(timeUntilEvent = timeUntilEvent))
        }
    }
}