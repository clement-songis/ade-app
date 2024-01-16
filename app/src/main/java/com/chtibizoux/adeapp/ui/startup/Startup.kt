package com.chtibizoux.adeapp.ui.startup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.atLeast
import com.chtibizoux.adeapp.ui.nextFocus
import com.chtibizoux.adeapp.ui.nextFocusKeyboardAction
import com.chtibizoux.adeapp.ui.submitKeyboardAction
import com.chtibizoux.adeapp.ui.submitOnEnter

@Composable
fun Startup(
    settingsViewModel: SettingsViewModel,
    startupViewModel: StartupViewModel = viewModel(
        factory = StartupViewModelFactory(
            settingsViewModel.startingTimes,
            settingsViewModel.defaultAlarmSettings.value
        )
    )
) {
    fun submit() {
        settingsViewModel.setDefaultAlarmSettings(startupViewModel.alarmSettings)
        settingsViewModel.setAlarms(startupViewModel.alarms)
    }
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.alarm_choice), Modifier.padding(20.dp), fontSize = 25.sp)
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                // TODO: Better ui, fields, focus, animations
                OutlinedTextField(
                    value = startupViewModel.repeat,
                    onValueChange = {
                        startupViewModel.updateRepeat(it)
                    },
                    modifier = Modifier
                        .nextFocus(),
                    label = { Text(stringResource(R.string.repeat_interval)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = nextFocusKeyboardAction(),
                    singleLine = true
                )
                if (startupViewModel.repeat.atLeast(1) > 1) {
                    OutlinedTextField(
                        value = startupViewModel.interval,
                        onValueChange = {
                            startupViewModel.updateInterval(it)
                        },
                        modifier = Modifier
                            .nextFocus(),
                        label = { Text(stringResource(R.string.default_interval)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = nextFocusKeyboardAction(),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = startupViewModel.timeUntilEvent,
                    onValueChange = {
                        startupViewModel.updateTimeUntilEvent(it)
                    },
                    modifier = Modifier.submitOnEnter(::submit),
                    label = { Text(stringResource(R.string.default_alarm_interval)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = submitKeyboardAction(::submit),
                    singleLine = true
                )
            }
            Column {
                startupViewModel.alarms.forEach { alarm ->
                    Text(
                        text = "${alarm.forHour} ➜ ${alarm.hours.joinToString { it.toString() }}",
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    settingsViewModel.noAlarm()
                }) {
                    Text(stringResource(R.string.cancel))
                }
                Button(
                    onClick = ::submit,
                    enabled = startupViewModel.canSubmit
                ) {
                    Text(stringResource(R.string.add))
                }
            }
        }
    }
}
