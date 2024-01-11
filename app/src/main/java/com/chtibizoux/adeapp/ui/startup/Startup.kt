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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.atLeast

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
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.alarm_choice), Modifier.padding(20.dp), fontSize = 25.sp)
            Column {
                // TODO: Better ui, fields, focus, animations
                OutlinedTextField(
                    value = startupViewModel.repeat,
                    onValueChange = {
                        startupViewModel.updateRepeat(it)
                    },
                    modifier = Modifier.padding(bottom = 20.dp),
                    label = { Text(stringResource(R.string.repeat_interval)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (startupViewModel.repeat.atLeast(1) > 1) {
                    OutlinedTextField(
                        value = startupViewModel.interval,
                        onValueChange = {
                            startupViewModel.updateInterval(it)
                        },
                        modifier = Modifier.padding(bottom = 20.dp),
                        label = { Text(stringResource(R.string.default_interval)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                OutlinedTextField(
                    value = startupViewModel.timeUntilEvent,
                    onValueChange = {
                        startupViewModel.updateTimeUntilEvent(it)
                    },
                    modifier = Modifier.padding(bottom = 20.dp),
                    label = { Text(stringResource(R.string.default_alarm_interval)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
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
                    onClick = {
                        settingsViewModel.setDefaultAlarmSettings(startupViewModel.alarmSettings)
                        settingsViewModel.setAlarms(startupViewModel.alarms)
                    },
                    enabled = startupViewModel.canSubmit
                ) {
                    Text(stringResource(R.string.add))
                }
            }
        }
    }
}
