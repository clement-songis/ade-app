package com.chtibizoux.adeapp.ui.login

import androidx.annotation.StringRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel

class LoginViewModel : ViewModel() {
    var username by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set

    @get:StringRes
    var usernameError: Int? by mutableStateOf(null)
        private set

    @get:StringRes
    var passwordError: Int? by mutableStateOf(null)
        private set

    val canLogin get() = usernameError == null && passwordError == null

    fun updateUsername(username: String) {
        this.username = username
        usernameError = if (!isUserNameValid(username)) R.string.invalid_username else null
    }

    fun updatePassword(password: String) {
        this.password = password
        passwordError = if (!isPasswordValid(password)) R.string.invalid_password else null
    }

    fun login(viewModel: SettingsViewModel) {
        usernameError = if (!isUserNameValid(username)) R.string.invalid_username else null
        passwordError = if (!isPasswordValid(password)) R.string.invalid_password else null
        if (canLogin) {
            viewModel.login(username, password)
        }
    }

    private fun isUserNameValid(username: String): Boolean {
        return username.isNotBlank() && username.length <= 25
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.isNotBlank() && password.length <= 25
    }
}
