package com.chtibizoux.adeapp.ui.startup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.settings.AlarmsSettings
import com.chtibizoux.adeapp.ui.settings.FieldValueManager

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
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(stringResource(R.string.alarm_choice), fontSize = 25.sp)
            Column(verticalArrangement = Arrangement.spacedBy(20.dp)) {
                AlarmsSettings(
                    repeat = FieldValueManager(
                        startupViewModel.repeat,
                        startupViewModel::updateRepeat,
                        !startupViewModel.validRepeat
                    ),
                    interval = FieldValueManager(
                        startupViewModel.interval,
                        startupViewModel::updateInterval,
                        !startupViewModel.validInterval
                    ),
                    timeUntilEvent = FieldValueManager(
                        startupViewModel.timeUntilEvent,
                        startupViewModel::updateTimeUntilEvent,
                        !startupViewModel.validTimeUntilEvent
                    ),
                    ::submit
                )
            }
            AlarmsComponent(startupViewModel.alarms)
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.End)
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
