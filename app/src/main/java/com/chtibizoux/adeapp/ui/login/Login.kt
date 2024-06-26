package com.chtibizoux.adeapp.ui.login

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.nextFocus
import com.chtibizoux.adeapp.ui.submitKeyboardAction

@Composable
fun Login(settingsViewModel: SettingsViewModel, loginViewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    fun login() {
        val user = loginViewModel.checkLink()
        if (user != null) {
            settingsViewModel.login(user) {
                Toast.makeText(context, R.string.login_failed, Toast.LENGTH_LONG).show()
            }
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
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
                value = loginViewModel.link,
                onValueChange = { loginViewModel.updateLink(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .nextFocus(),
                label = { Text(stringResource(R.string.link)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                keyboardActions = submitKeyboardAction(::login),
                isError = loginViewModel.linkError != null,
                supportingText = {
                    if (loginViewModel.linkError != null) {
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = stringResource(loginViewModel.linkError!!),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                },
                singleLine = true
            )

            Button(onClick = ::login, enabled = loginViewModel.linkError == null) {
                Text(stringResource(R.string.login))
            }
        }
    }
}
