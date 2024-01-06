package com.chtibizoux.adeapp.ui

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.DataSource
import com.chtibizoux.adeapp.data.Result
import com.chtibizoux.adeapp.data.Settings
import com.chtibizoux.adeapp.data.SettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

enum class LoginState {
    LOADING, DISCONNECTED, CONNECTED, FIRST_CONNECTION
}

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    var loginState by mutableStateOf(LoginState.LOADING)
        private set

    val settings = repository.settings

    private val _toastMessage = MutableSharedFlow<@receiver:StringRes Int>()
    val toastMessage = _toastMessage.asSharedFlow()

    fun login(username: String, password: String) {
        loginState = LoginState.LOADING
        viewModelScope.launch {
            val result = repository.login(username, password)
            if (result is Result.Success) {
                loginState = LoginState.FIRST_CONNECTION
            } else {
                loginState = LoginState.DISCONNECTED
                _toastMessage.emit(R.string.login_failed)
            }
        }
    }

    init {
        viewModelScope.launch {
            settings.collect { settings ->
                loginState = if (settings.user == null) {
                    LoginState.DISCONNECTED
                } else if (settings.firstTime) {
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