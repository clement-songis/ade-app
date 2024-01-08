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
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.atLeast

@Composable
fun Startup(viewModel: SettingsViewModel) {
    if (viewModel.alarms.isEmpty()) {
//        TODO: Retry
        throw Error("No calendar")
    }

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
                    value = viewModel.defaultAlarmRepeat,
                    onValueChange = {
                        viewModel.setAlarmRepeat(it)
                    },
                    modifier = Modifier.padding(bottom = 20.dp),
                    label = { Text(stringResource(R.string.repeat_interval)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (viewModel.defaultAlarmRepeat.atLeast(1) > 1) {
                    OutlinedTextField(
                        value = viewModel.defaultInterval,
                        onValueChange = {
                            viewModel.setInterval(it)
                        },
                        modifier = Modifier.padding(bottom = 20.dp),
                        label = { Text(stringResource(R.string.default_interval)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                OutlinedTextField(
                    value = viewModel.defaultAlarmInterval,
                    onValueChange = {
                        viewModel.setAlarmInterval(it)
                    },
                    modifier = Modifier.padding(bottom = 20.dp),
                    label = { Text(stringResource(R.string.default_alarm_interval)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Column {
                viewModel.alarms.forEach { alarm ->
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
                    viewModel.noAlarm()
                }) {
                    Text(stringResource(R.string.cancel))
                }
                Button(onClick = { viewModel.setAlarms() }, enabled = viewModel.canSubmit) {
                    Text(stringResource(R.string.add))
                }
            }
        }
    }
}
