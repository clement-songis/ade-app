package com.chtibizoux.adeapp.ui.startup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.ui.SettingsViewModel

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
                OutlinedTextField(
                    value = viewModel.defaultAlarmRepeat.toString(),
                    onValueChange = {
                        viewModel.setAlarmRepeat(it)
                    },
                    modifier = Modifier.padding(bottom = 20.dp),
                    label = { Text(stringResource(R.string.repeat_interval)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                if (viewModel.defaultAlarmRepeat > 1) {
                    OutlinedTextField(
                        value = viewModel.defaultInterval.toString(),
                        onValueChange = {
                            viewModel.setInterval(it)
                        },
                        modifier = Modifier.padding(bottom = 20.dp),
                        label = { Text(stringResource(R.string.default_interval)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
                OutlinedTextField(
                    value = viewModel.defaultAlarmInterval.toString(),
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
                Button(onClick = {
                    viewModel.setAlarms()
                }) {
                    Text(stringResource(R.string.add))
                }
            }
        }
    }
}

@Composable
fun TimePickerButton(initialTime: Time, time: Time?, updateTime: (time: Time) -> Unit) {

    var showTimePicker by remember { mutableStateOf(false) }

    Button(onClick = { showTimePicker = true }) {
        Text(time?.toString() ?: "+")
    }

    if (showTimePicker) {
        TimePickerDialog(time ?: initialTime) {
            showTimePicker = false
            if (it != null) {
                updateTime(it)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(initial: Time, onTimeSelected: (Time?) -> Unit) {
    val timePickerState = rememberTimePickerState(initial.hour, initial.minute)

    Dialog(
        onDismissRequest = { onTimeSelected(null) },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(
                    shape = MaterialTheme.shapes.extraLarge,
                    color = MaterialTheme.colorScheme.surface
                ),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TimePicker(timePickerState)
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        onTimeSelected(Time(timePickerState.hour, timePickerState.minute))
                    }) { Text("OK") }
                }
            }
        }
    }
}
