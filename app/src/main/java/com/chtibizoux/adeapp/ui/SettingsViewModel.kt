package com.chtibizoux.adeapp.ui

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
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
import com.chtibizoux.adeapp.data.xml.Calendar
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

enum class LoginState {
    LOADING, DISCONNECTED, CONNECTED, FIRST_CONNECTION
}

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    var loginState by mutableStateOf(LoginState.LOADING)
        private set

    var startingHours: List<String>? by mutableStateOf(null)
        private set

    val settings = repository.settings

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

    fun setAlarms(alarms: List<Alarm>) {
        loginState = LoginState.LOADING
        viewModelScope.launch {
            repository.setAlarms(alarms)
        }
    }

    init {
        viewModelScope.launch {
            settings.collect { settings ->
                loginState = if (settings.user == null) {
                    LoginState.DISCONNECTED
                } else if (settings.firstTime) {
                    if (startingHours == null) {
                        val result = repository.getStartingHours(settings.user)
                        if (result is Result.Success) {
                            startingHours = result.data
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