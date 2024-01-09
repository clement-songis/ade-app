package com.chtibizoux.adeapp.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chtibizoux.adeapp.AlarmReceiver
import com.chtibizoux.adeapp.BootReceiver
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.DataSource
import com.chtibizoux.adeapp.data.Result
import com.chtibizoux.adeapp.data.Settings
import com.chtibizoux.adeapp.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


enum class AppState {
    LOADING, DISCONNECTED, CONNECTED, FIRST_CONNECTION, GET_STARTING_TIMES_FAILED
}

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    var appState by mutableStateOf(AppState.LOADING)
        private set

    private val user = repository.settings.map { it.user }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val alarms = repository.settings.map { it.alarms }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
    val calendar = repository.settings.map { it.calendar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    lateinit var startingTimes: List<String>
        private set

    var updateCalendar = false
    var alarmPageFirst = false

    suspend fun refreshCalendar(): Boolean {
        return repository.updateCalendar(user.value!!)
    }

    fun login(username: String, password: String, onFailed: () -> Unit) {
        appState = AppState.LOADING
        viewModelScope.launch {
            val success = repository.login(username, password)
            if (success) {
                firstTime()
            } else {
                appState = AppState.DISCONNECTED
                onFailed()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            appState = AppState.DISCONNECTED
        }
    }

    fun noAlarm() {
        appState = AppState.LOADING
        viewModelScope.launch {
            repository.closeStartup()
            goToMain()
        }
    }

    fun setAlarms(alarms: List<Alarm>) {
        appState = AppState.LOADING
        viewModelScope.launch {
            repository.setAlarms(alarms.toList())
            alarmPageFirst = true
            goToMain()
        }
    }

    fun retry() {
        appState = AppState.LOADING
        viewModelScope.launch {
            firstTime()
        }
    }

    private suspend fun firstTime() {
        val result = repository.getStartingTimes(user.value!!)
        if (result is Result.Success) {
            startingTimes = result.data
            appState = AppState.FIRST_CONNECTION
        } else {
            appState = AppState.GET_STARTING_TIMES_FAILED
        }
    }

    private suspend fun goToMain() {
        if (calendar.value == null) {
            val success = repository.updateCalendar(user.value!!)
            if (!success) {
                updateCalendar = true
            }
        } else {
            updateCalendar = true
        }
        appState = AppState.CONNECTED
    }

    fun tryUpdateCalendar(onError: () -> Unit) {
        viewModelScope.launch {
            val success = repository.updateCalendar(user.value!!)
            if (!success) {
                onError()
            }
        }
    }

    fun initAlarms(context: Context) {
        viewModelScope.launch {
            if (alarms.value.isNotEmpty()) {
                BootReceiver.enable(context)
                AlarmReceiver.setBackgroundWork(context)
                AlarmReceiver.setAlarmAndNotifyUser(context, repository, user.value!!, alarms.value)
            } else {
                BootReceiver.disable(context)
                AlarmReceiver.removeBackgroundWork(context)
            }
        }
    }

    init {
        viewModelScope.launch {
            val settings = repository.settings.first()
            if (settings.user == null) {
//                Remove all settings if they are set
                appState = AppState.DISCONNECTED
            } else if (settings.firstTime) {
//                Remove alarms and calendar if they are set
                firstTime()
            } else {
                goToMain()
            }
        }
    }
}

fun String.atLeast(min: Int) = this.toIntOrNull()?.coerceAtLeast(min) ?: min

class SettingsViewModelFactory(
    private val dataStore: DataStore<Settings>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                SettingsRepository(dataStore, DataSource())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}