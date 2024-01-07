package com.chtibizoux.adeapp.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.DataSource
import com.chtibizoux.adeapp.data.Result
import com.chtibizoux.adeapp.data.Settings
import com.chtibizoux.adeapp.data.SettingsRepository
import com.chtibizoux.adeapp.data.Time
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

enum class LoginState {
    LOADING, DISCONNECTED, CONNECTED, FIRST_CONNECTION
}

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    var loginState by mutableStateOf(LoginState.LOADING)
        private set

    val alarms = mutableStateListOf<Alarm>()

    var defaultAlarmInterval by mutableIntStateOf(60)
        private set
    var defaultAlarmRepeat by mutableIntStateOf(1)
        private set
    var defaultInterval by mutableIntStateOf(1)
        private set

    private val _toastMessage = MutableSharedFlow<@receiver:StringRes Int>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun login(username: String, password: String) {
        loginState = LoginState.LOADING
        viewModelScope.launch {
            val result = repository.login(username, password)
            if (result is Result.Error) {
                loginState = LoginState.DISCONNECTED
                _toastMessage.emit(R.string.login_failed)
            }
        }
    }

    private fun updateAlarms() {
        alarms.replaceAll { alarm ->
            Alarm(alarm.forHour, (0..<defaultAlarmRepeat).map {
                val time = Time.fromString(alarm.forHour)!!
                Time(time.getMinutesNumber() + defaultAlarmInterval + it * defaultInterval)
            }.toPersistentList())
        }
    }

    fun noAlarm() {
        loginState = LoginState.LOADING
        viewModelScope.launch {
            repository.closeStartup()
        }
    }

    fun setAlarms() {
        loginState = LoginState.LOADING
        viewModelScope.launch {
            repository.setAlarms(alarms.toList())
        }
//        TODO: Go to alarm page
    }

    fun setAlarmInterval(interval: String) {
        if (interval.isEmpty()) {
            defaultAlarmInterval = 0
        }
        val i = interval.toIntOrNull()
        if (i != null && i >= 0) {
            defaultAlarmInterval = i
        }
        updateAlarms()
    }

    fun setAlarmRepeat(repeat: String) {
        if (repeat.isEmpty()) {
            defaultAlarmRepeat = 1
        }
        val r = repeat.toIntOrNull()
        if (r != null && r >= 1) {
            defaultAlarmRepeat = r
        }
        updateAlarms()
    }

    fun setInterval(interval: String) {
        if (interval.isEmpty()) {
            defaultInterval = 0
        }
        val i = interval.toIntOrNull()
        if (i != null && i >= 0) {
            defaultInterval = i
        }
        updateAlarms()
    }

    init {
        viewModelScope.launch {
            repository.settings.collect { settings ->
                loginState = if (settings.user == null) {
                    LoginState.DISCONNECTED
                } else if (settings.firstTime) {
                    if (alarms.isEmpty()) {
                        val result = repository.getStartingHours(settings.user)
                        if (result is Result.Success) {
                            alarms.addAll(result.data.map { Alarm(it, persistentListOf()) })
                            updateAlarms()
                        } else {
//                            TODO: Add retry popup or screen
                            throw Error("Get calendar failed")
//                            loginState = LoginState.RETRY
//                            _toastMessage.emit(R.string.get_calendar_failed)
                        }
                    }
                    LoginState.FIRST_CONNECTION
                } else {
                    LoginState.CONNECTED
                }
            }
        }
    }
}

class SettingsViewModelFactory(
    private val dataStore: DataStore<Settings>
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(SettingsRepository(dataStore, DataSource())) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}