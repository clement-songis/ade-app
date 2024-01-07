package com.chtibizoux.adeapp.data

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import java.util.Date

class SettingsRepository(
    private val dataStore: DataStore<Settings>,
    private val dataSource: DataSource
) {
    val settings: Flow<Settings> = dataStore.data

    suspend fun logout() {
        dataStore.updateData { settings -> settings.copy(user = null) }
    }

    suspend fun login(username: String, password: String): Result<User> {
        val result = dataSource.login(username, password)
        if (result is Result.Success) {
            setUser(result.data)
        }
        return result
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

    suspend fun getStartingHours(user: User): Result<List<String>> {
        return dataSource.getStartingHours(user)
    }

    suspend fun getStartingHour(user: User, date: Date): Result<String> {
        return dataSource.getStartingHour(user, date)
    }

//    getStartingHour
}