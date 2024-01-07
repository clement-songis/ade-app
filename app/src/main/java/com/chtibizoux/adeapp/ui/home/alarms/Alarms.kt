package com.chtibizoux.adeapp.ui.home.alarms

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.chtibizoux.adeapp.data.Time

@Composable
fun Alarms(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text("Not implemented")
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(10.dp),
//                horizontalArrangement = Arrangement.SpaceAround
//            ) {
//                Text("Pour")
//                Text("Réveil à")
//            }
//            viewModel.startingHours!!.forEach { forHour ->
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(10.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceAround
//                ) {
//                    Text(forHour)
//                    Row {
//                        TimePickerButton(
//                            Time.fromString(forHour)!!.add(dafaultAlarmInterval),
//                            viewModel.alarms.find { it.forHour == forHour }?.hours?.first()
//                        ) { t ->
//                            val hours = persistentListOf(t, )
//                            val i = viewModel.alarms.indexOfFirst { it.forHour == forHour }
//                            if (i == -1) {
//                                viewModel.alarms.add(Alarm(forHour, hours))
//                            } else {
//                                viewModel.alarms[i] = Alarm(forHour, hours)
//                            }
//                        }
//                        if (viewModel.alarms.find { it.forHour == forHour } != null) {
//                            TextButton(onClick = {
//                                viewModel.alarms.remove(viewModel.alarms.find { it.forHour == forHour })
//                            }) {
//                                Text("-")
//                            }
//                        }
//                    }
//                }
//            }
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
