package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.DefaultAlarmSettings
import com.chtibizoux.adeapp.data.Time

@Composable
fun AddAlarmButton(
    show: Boolean,
    initial: Time,
    alarmSettings: DefaultAlarmSettings,
    onClick: (Alarm) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(show) }

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