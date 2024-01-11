package com.chtibizoux.adeapp.data

import androidx.datastore.core.DataStore
import com.chtibizoux.adeapp.data.xml.Calendar
import java.util.Date

class SettingsRepository(
    private val dataStore: DataStore<Settings>,
    private val dataSource: DataSource
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

    suspend fun setAlarms(alarms: List<Alarm>) {
        dataStore.updateData { settings -> settings.copy(alarms = alarms, firstTime = false) }
    }

    suspend fun getStartingTimes(user: User): Result<List<String>> {
        return dataSource.getStartingTimes(user)
    }

    suspend fun getStartingTime(user: User, date: Date): Result<String> {
        return dataSource.getStartingTime(user, date)
    }

    suspend fun updateCalendar(user: User): Boolean {
        val result = dataSource.getCalendar(user)
        if (result is Result.Success) {
            dataStore.updateData { settings -> settings.copy(calendar = Calendar(result.data.days.sortedBy { it.getDate() })) }
        }
        return result is Result.Success
    }
}