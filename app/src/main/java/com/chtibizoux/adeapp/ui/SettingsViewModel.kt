package com.chtibizoux.adeapp.ui

import android.content.Context
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chtibizoux.adeapp.AlarmReceiver
import com.chtibizoux.adeapp.BootReceiver
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.DataSource
import com.chtibizoux.adeapp.data.Result
import com.chtibizoux.adeapp.data.SettingsRepository
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.data.dataStore
import kotlinx.coroutines.launch


enum class LoginState {
    LOADING, DISCONNECTED, CONNECTED, FIRST_CONNECTION
}

class SettingsViewModel(private val repository: SettingsRepository, private val context: Context) :
    ViewModel() {
    var loginState by mutableStateOf(LoginState.LOADING)
        private set

    val alarms = mutableStateListOf<Alarm>()

    var defaultAlarmInterval by mutableStateOf("60")
        private set
    var defaultAlarmRepeat by mutableStateOf("1")
        private set
    var defaultInterval by mutableStateOf("1")
        private set

    val canSubmit
        get() = run {
            val alarmRepeat = defaultAlarmRepeat.atLeast(1)
            val alarmInterval = defaultAlarmInterval.atLeast(0)
            val interval = defaultInterval.atLeast(0)
            alarmRepeat == defaultAlarmRepeat.toIntOrNull() && alarmInterval == defaultAlarmInterval.toIntOrNull() && interval == defaultInterval.toIntOrNull()
        }

    fun login(username: String, password: String) {
        loginState = LoginState.LOADING
        viewModelScope.launch {
            val result = repository.login(username, password)
            if (result is Result.Error) {
                loginState = LoginState.DISCONNECTED
                Toast.makeText(context, R.string.login_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun updateAlarms() {
        val alarmRepeat = defaultAlarmRepeat.atLeast(1)
        val alarmInterval = defaultAlarmInterval.atLeast(0)
        val interval = defaultInterval.atLeast(0)
        alarms.replaceAll { alarm ->
            Alarm(alarm.forHour, (0..<alarmRepeat).map {
                val time = Time.fromString(alarm.forHour)!!
                Time(time.getMinutesNumber() - alarmInterval + it * interval)
            })
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
        defaultAlarmInterval = interval
        updateAlarms()
    }

    fun setAlarmRepeat(repeat: String) {
        defaultAlarmRepeat = repeat
        updateAlarms()
    }

    fun setInterval(interval: String) {
        defaultInterval = interval
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
                            alarms.addAll(result.data.map { Alarm(it, listOf()) })
                            updateAlarms()
                        } else {
                            Toast.makeText(context, "Get calendar failed", Toast.LENGTH_LONG).show()
//                            TODO: Add retry popup or screen
                            throw Error("Get calendar failed")
//                            loginState = LoginState.RETRY
//                            Toast.makeText(R.string.get_calendar_failed)
                        }
                    }
                    LoginState.FIRST_CONNECTION
                } else {
                    if (settings.alarms.isNotEmpty()) {
                        BootReceiver.enable(context)
                        AlarmReceiver.setBackgroundWork(context)
//                        AlarmReceiver.setAlarmAndNotifyUser(context, repository, settings)
                    } else {
                        BootReceiver.disable(context)
                        AlarmReceiver.removeBackgroundWork(context)
                    }
                    LoginState.CONNECTED
                }
            }
        }
    }
}

fun String.atLeast(min: Int) = this.toIntOrNull()?.coerceAtLeast(min) ?: min

class SettingsViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel(
                SettingsRepository(context.dataStore, DataSource()),
                context
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}