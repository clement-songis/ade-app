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
import com.chtibizoux.adeapp.data.DefaultAlarmSettings
import com.chtibizoux.adeapp.data.Result
import com.chtibizoux.adeapp.data.Settings
import com.chtibizoux.adeapp.data.SettingsRepository
import com.chtibizoux.adeapp.data.Time
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
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val defaultAlarmSettings = repository.settings.map { it.defaultAlarmSettings }
        .stateIn(viewModelScope, SharingStarted.Eagerly, DefaultAlarmSettings())

    val usePreviousAlarm = repository.settings.map { it.usePreviousAlarm }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val alarms = repository.settings.map { it.alarms }
        .stateIn(viewModelScope, SharingStarted.Eagerly, listOf())
    val calendar = repository.settings.map { it.calendar }
        .stateIn(viewModelScope, SharingStarted.Eagerly, null)

    lateinit var startingTimes: List<Time>
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

    fun setUsePreviousAlarm(active: Boolean) {
        viewModelScope.launch {
            repository.setUsePreviousAlarm(active)
        }
    }

    fun setDefaultAlarmSettings(settings: DefaultAlarmSettings) {
        viewModelScope.launch {
            repository.setDefaultAlarmSettings(settings)
        }
    }

    fun updateRepeat(repeat: Int) {
        viewModelScope.launch {
            repository.updateRepeat(repeat)
        }
    }

    fun updateInterval(interval: Int) {
        viewModelScope.launch {
            repository.updateInterval(interval)
        }
    }

    fun updateTimeUntilEvent(timeUntilEvent: Int) {
        viewModelScope.launch {
            repository.updateTimeUntilEvent(timeUntilEvent)
        }
    }

    fun addAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.addAlarm(alarm)
        }
    }

    fun updateLabel(i: Int, time: String) {
        viewModelScope.launch {
            repository.updateLabel(i, time)
        }
    }

    fun addTime(i: Int, time: Time) {
        viewModelScope.launch {
            repository.addTime(i, time)
        }
    }

    fun updateTime(i: Int, hourIndex: Int, time: Time) {
        viewModelScope.launch {
            repository.updateTime(i, hourIndex, time)
        }
    }

    fun removeTime(i: Int, time: Time) {
        viewModelScope.launch {
            repository.removeTime(i, time)
        }
    }

    fun updateForHour(i: Int, time: Time) {
        viewModelScope.launch {
            repository.updateForHour(i, time)
        }
    }

    fun removeAlarm(alarm: Alarm) {
        viewModelScope.launch {
            repository.removeAlarm(alarm)
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
            } else {
                println("Calendar updated")
            }
        }
    }

    fun initAlarms(context: Context) {
        viewModelScope.launch {
            if (alarms.value.isNotEmpty()) {
                BootReceiver.enable(context)
                AlarmReceiver.setBackgroundWork(context)
//                AlarmReceiver.setAlarmAndNotifyUser(
//                    context,
//                    repository,
//                    user.value!!,
//                    alarms.value,
//                    usePreviousAlarm.value
//                )
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
//              TODO: Maybe remove all settings if they are set
                appState = AppState.DISCONNECTED
            } else if (settings.firstTime) {
//              TODO: Maybe remove alarms and calendar if they are set
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