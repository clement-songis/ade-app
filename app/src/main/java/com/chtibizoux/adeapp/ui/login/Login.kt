package com.chtibizoux.adeapp.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel

@Composable
fun Login(settingsViewModel: SettingsViewModel, loginViewModel: LoginViewModel = viewModel()) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.padding(20.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically)
        ) {
            Text(stringResource(R.string.welcome_message), fontSize = 20.sp)
            Text(stringResource(R.string.app_description), textAlign = TextAlign.Center)
            Text(
                stringResource(R.string.login_text),
                fontSize = 17.sp,
                textAlign = TextAlign.Center
            )

            OutlinedTextField(
                value = loginViewModel.username,
                onValueChange = { loginViewModel.updateUsername(it) },
                label = { Text(stringResource(R.string.username)) },
                singleLine = true
            )

            OutlinedTextField(
                value = loginViewModel.password, onValueChange = { loginViewModel.updatePassword(it) },
                Modifier.onFocusChanged {  },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true
            )
//            android:autofillHints = "password"

            Button(onClick = {
                settingsViewModel.login(loginViewModel.username, loginViewModel.password)
            }, enabled = loginViewModel.canLogin) {
                Text(stringResource(R.string.login))
            }

            Text(stringResource(R.string.login_explanation), textAlign = TextAlign.Center)
        }
    }
}
