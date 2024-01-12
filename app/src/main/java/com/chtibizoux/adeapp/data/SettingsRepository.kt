package com.chtibizoux.adeapp.data

import androidx.datastore.core.DataStore
import com.chtibizoux.adeapp.data.xml.Calendar
import java.util.Date

class SettingsRepository(
    private val dataStore: DataStore<Settings>, private val dataSource: DataSource
) {
    val settings = dataStore.data

    suspend fun logout() {
        dataStore.updateData { Settings() }
    }

    suspend fun login(username: String, password: String): Boolean {
        val result = dataSource.login(username, password)
        if (result is Result.Success) {
            setUser(result.data)
        }
        return result is Result.Success
    }

    private suspend fun setUser(user: User) {
        dataStore.updateData { settings -> settings.copy(user = user) }
    }

    suspend fun closeStartup() {
        dataStore.updateData { settings -> settings.copy(firstTime = false) }
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
        dataStore.updateData { settings -> settings.copy(alarms = alarms, firstTime = false) }
    }

    suspend fun getStartingTimes(user: User): Result<List<Time>> {
        return dataSource.getStartingTimes(user)
    }

    suspend fun getStartingTime(user: User, date: Date): Result<Time> {
        return dataSource.getStartingTime(user, date)
    }

    suspend fun updateCalendar(user: User): Boolean {
        val result = dataSource.getCalendar(user)
        if (result is Result.Success) {
            dataStore.updateData { settings -> settings.copy(calendar = Calendar(result.data.days.sortedBy { it.getDate() })) }
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