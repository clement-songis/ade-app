package com.chtibizoux.adeapp.ui.login

import android.view.KeyEvent.ACTION_DOWN
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Login(settingsViewModel: SettingsViewModel, loginViewModel: LoginViewModel = viewModel()) {
    val focusManager = LocalFocusManager.current

    val context = LocalContext.current
    fun login() {
        loginViewModel.tryLogin()
        settingsViewModel.login(loginViewModel.username, loginViewModel.password) {
            Toast.makeText(context, R.string.login_failed, Toast.LENGTH_LONG).show()
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
                value = loginViewModel.username,
                onValueChange = { loginViewModel.updateUsername(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onPreviewKeyEvent {
                        if (it.key == Key.Tab && it.nativeKeyEvent.action == ACTION_DOWN){
                            focusManager.moveFocus(FocusDirection.Down)
                            true
                        } else {
                            false
                        }
                    }
                    .autofill(listOf(AutofillType.Username)) { loginViewModel.updateUsername(it) },
                label = { Text(stringResource(R.string.username)) },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                singleLine = true
            )

            OutlinedTextField(
                value = loginViewModel.password,
                onValueChange = { loginViewModel.updatePassword(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .onPreviewKeyEvent {
                        if (it.key == Key.Enter){
                            login()
                            true
                        } else {
                            false
                        }
                    }
                    .autofill(listOf(AutofillType.Password)) { loginViewModel.updatePassword(it) },
                label = { Text(stringResource(R.string.password)) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                keyboardActions = KeyboardActions(
                    onDone = { login() }
                ),
                singleLine = true
            )

            Button(onClick = { login() }, enabled = loginViewModel.canLogin) {
                Text(stringResource(R.string.login))
            }

            Text(stringResource(R.string.login_explanation), textAlign = TextAlign.Center)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(autofillTypes: List<AutofillType>, onFill: ((String) -> Unit)) = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)
    LocalAutofillTree.current += autofillNode

    this
        .onGloballyPositioned {
            autofillNode.boundingBox = it.boundsInWindow()
        }
        .onFocusChanged { focusState ->
            autofill?.run {
                if (focusState.isFocused) {
                    requestAutofillForNode(autofillNode)
                } else {
                    cancelAutofillForNode(autofillNode)
                }
            }
        }
}