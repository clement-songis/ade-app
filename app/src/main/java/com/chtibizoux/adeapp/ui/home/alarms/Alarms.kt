package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.DefaultAlarmSettings
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.home.SettingsButton
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Alarms(navController: NavController, viewModel: SettingsViewModel) {
    val alarms by viewModel.alarms.collectAsState()
    val alarmSettings by viewModel.defaultAlarmSettings.collectAsState()
    var selected by remember { mutableIntStateOf(-1) }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(stringResource(R.string.title_alarms))
            }, actions = {
                SettingsButton(navController)
            })
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
                alarms.forEachIndexed { i, alarm ->
                    AlarmComponent(alarm, i, selected, viewModel, alarmSettings) {
                        selected = if (selected == i) -1 else i
                    }
                }
            }
            AddAlarmButton(
                if (alarms.isEmpty()) {
                    Time(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), 0)
                } else {
                    alarms.last().forHour + 60
                }, alarmSettings
            ) {
                viewModel.addAlarm(it)
            }
        }
    }
}

@Composable
fun AlarmComponent(
    alarm: Alarm,
    i: Int,
    selected: Int,
    viewModel: SettingsViewModel,
    alarmSettings: DefaultAlarmSettings,
    onClick: () -> Unit
) {
    Surface(
        tonalElevation = 3.dp, shape = RoundedCornerShape(24.dp), onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(
                start = 20.dp, end = 20.dp
            )
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Label(selected == i, alarm.label, onClick, {
                        viewModel.updateLabel(i, it)
                    })
                    ForTime(alarm.forHour, selected == i || alarm.label.isNotEmpty()) {
                        viewModel.updateForHour(i, it)
                    }
                }
                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    stringResource(R.string.more),
                    modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50)
                        )
                        .rotate(if (selected == i) 180f else 0f)
                )
            }
            if (selected == i) {
                Text(
                    stringResource(R.string.alarms_empty),
                )
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    alarm.hours.forEachIndexed { index, time ->
                        AlarmHour(time, {
                            viewModel.updateTime(i, index, it)
                        }, {
                            viewModel.removeTime(i, time)
                        })
                    }
                    AddTimeButton(
                        if (alarm.hours.isEmpty()) {
                            alarm.forHour - alarmSettings.timeUntilEvent
                        } else {
                            alarm.hours.last() + alarmSettings.interval
                        }
                    ) {
                        viewModel.addTime(i, it)
                    }
                }
                Row(modifier = Modifier
                    .clickable { viewModel.removeAlarm(alarm) }
                    .fillMaxWidth()
                    .minimumInteractiveComponentSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Icon(Icons.Filled.Delete, stringResource(R.string.delete))
                    Text(stringResource(R.string.delete))
                }
            } else {
                Text(
                    stringResource(R.string.alarms, alarm.hours.joinToString()),
                    modifier = Modifier.padding(bottom = 20.dp)
                )
            }
        }

    }
}

@Composable
fun ForTime(forHour: Time, padding: Boolean, updateTime: (time: Time) -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        verticalAlignment = Alignment.Bottom, modifier = Modifier.padding(
            top = if (padding) 0.dp else 20.dp, bottom = 20.dp
        )
    ) {
        Text(
            stringResource(R.string.for_alarm),
            modifier = Modifier.padding(bottom = 8.dp),
            fontSize = 18.sp,
        )
        Text(forHour.toString(), fontSize = 44.sp, modifier = Modifier.clickable {
            showTimePicker = true
        })
    }
    if (showTimePicker) {
        TimePickerDialog(forHour) {
            showTimePicker = false
            if (it != null) {
                updateTime(it)
            }
        }
    }
}

@Composable
fun Label(
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit,
    onChange: (label: String) -> Unit
) {
    var showLabelPicker by remember { mutableStateOf(false) }
    if (isSelected || label.isNotEmpty()) {
        Surface(
            onClick = {
                if (isSelected) {
                    showLabelPicker = true
                } else {
                    onClick()
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
                        Icons.Outlined.Label, stringResource(R.string.label)
                    )
                }
                Text(
                    label.ifEmpty { stringResource(R.string.add_label) },
                    color = if (label.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (showLabelPicker) {
            TextPicker(label, stringResource(R.string.label)) {
                showLabelPicker = false
                if (it != null) {
                    onChange(it)
                }
            }
        }
    }
}

@Composable
fun AlarmHour(hour: Time, updateTime: (Time) -> Unit, deleteTime: () -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(hour.toString(), fontSize = 28.sp, modifier = Modifier.clickable {
            showTimePicker = true
        })
        IconButton(onClick = deleteTime) {
            Icon(Icons.Filled.RemoveCircleOutline, stringResource(R.string.delete))
        }
    }

    if (showTimePicker) {
        TimePickerDialog(hour) {
            showTimePicker = false
            if (it != null) {
                updateTime(it)
            }
        }
    }
}

@Composable
fun AddTimeButton(initial: Time, onClick: (Time) -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }

    IconButton(onClick = { showTimePicker = true }) {
        Icon(Icons.Filled.Add, stringResource(R.string.alarm_add), modifier = Modifier.size(36.dp))
    }

    if (showTimePicker) {
        TimePickerDialog(initial) {
            showTimePicker = false
            if (it != null) {
                onClick(it)
            }
        }
    }
}

@Composable
fun AddAlarmButton(initial: Time, alarmSettings: DefaultAlarmSettings, onClick: (Alarm) -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.BottomCenter) {
        FloatingActionButton(
            modifier = Modifier
                .padding(30.dp)
                .size(75.dp),
            onClick = {
                showTimePicker = true
            },
            shape = CircleShape,
        ) {
            Icon(Icons.Filled.Add, stringResource(R.string.alarm_add))
        }
    }
    if (showTimePicker) {
        TimePickerDialog(initial) { time ->
            showTimePicker = false
            if (time != null) {
                onClick(Alarm(time, (0..<alarmSettings.repeat).map {
                    Time(time.getMinutesNumber() - alarmSettings.timeUntilEvent + it * alarmSettings.interval)
                }))
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
                    }) { Text(stringResource(R.string.ok)) }
                }
            }
        }
    }
}

@Composable
fun TextPicker(initial: String, label: String, onClose: (String?) -> Unit) {
    var text by remember { mutableStateOf(initial) }

    Dialog(
        onDismissRequest = { onClose(null) },
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
                OutlinedTextField(value = text,
                    onValueChange = { text = it },
                    label = { Text(label) })
                Row(
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth()
                ) {
                    TextButton(onClick = {
                        onClose(null)
                    }) { Text(stringResource(R.string.cancel)) }
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(onClick = {
                        onClose(text)
                    }) { Text(stringResource(R.string.ok)) }
                }
            }
        }
    }
}
