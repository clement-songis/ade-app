package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.ui.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Alarms(viewModel: SettingsViewModel) {
    val alarms by viewModel.alarms.collectAsState()
    var selected by remember { mutableIntStateOf(-1) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(stringResource(R.string.title_alarms))
                }
            )
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp, 24.dp, 24.dp, 135.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // TODO: alarm ui add delete modify
                alarms.forEachIndexed { i, alarm ->
                    AlarmComponent(alarm, i, selected) {
                        selected = if (selected == i) -1 else i
                    }
                }
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
            }
            Box(contentAlignment = Alignment.BottomCenter) {
                FloatingActionButton(
                    modifier = Modifier
                        .padding(30.dp)
                        .size(75.dp),
                    onClick = {
//                      TODO: Add alarm
                    },
                    shape = CircleShape,
                ) {
                    Icon(Icons.Filled.Add, stringResource(R.string.alarm_add))
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmComponent(alarm: Alarm, i: Int, selected: Int, onClick: () -> Unit) {
    Surface(
        tonalElevation = 3.dp,
        shape = RoundedCornerShape(24.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(
                start = 20.dp, end = 20.dp, bottom = 20.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Label(selected == i, alarm.label)
                    ForTime(alarm.forHour, selected == i || alarm.label.isNotEmpty())
                }
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    stringResource(R.string.more),
                    modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(50)
                        )
                        .rotate(if (selected == i) 180f else 0f)
                )
            }
            if (selected == i) {
                Text(
                    "Réveils :",
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(
                        10.dp,
                        Alignment.CenterHorizontally
                    ),
                    verticalArrangement = Arrangement.Center
                ) {
                    alarm.hours.forEach {
                        Text(
                            it.toString(),
                            fontSize = 30.sp,
                            modifier = Modifier.clickable {
                                /*updateHour*/
                            }
                        )
                    }
                    IconButton(onClick = { /*addHour*/ }) {
                        Icon(Icons.Filled.Add, stringResource(R.string.alarm_add))
                    }
                }
            } else {
                Text(
                    "Réveils : ${alarm.hours.joinToString()}",
                )
            }
        }

    }
}

@Composable
fun ForTime(forHour: String, padding: Boolean) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier.padding(
            top = if (padding) 0.dp else 20.dp,
            bottom = 20.dp
        )
    ) {
        Text(
            "Pour ",
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 18.sp,
        )
        Text(forHour,
            fontSize = 44.sp,
            modifier = Modifier.clickable {

            })
    }
}

@Composable
fun Label(isSelected: Boolean, label: String) {
    if (isSelected || label.isNotEmpty()) {
        Surface(
            onClick = {
                if (isSelected) {
//                            updateLabel
                }
            }, modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    Icon(
                        Icons.Outlined.Label,
                        stringResource(R.string.update_label)
                    )
                }
                Text(
                    label.ifEmpty { stringResource(R.string.add_label) },
                    color = if (label.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onBackground
                )
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
