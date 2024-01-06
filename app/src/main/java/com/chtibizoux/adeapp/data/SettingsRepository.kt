package com.chtibizoux.adeapp.data

import androidx.datastore.core.DataStore
import com.chtibizoux.adeapp.data.xml.Calendar
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    suspend fun getCalendar(user: User): Result<Calendar> {
        val result = dataSource.getCalendar(user)

//        if (result is Result.Success) {
//            setCalendar(result.data)
//        }

        return result
    }

//    private suspend fun setCalendar(calendar: Calendar) {
//        userPreferencesStore.updateData { preferences ->
//            preferences.toBuilder().setCalendar(calendar).build()
//        }
//    }
}