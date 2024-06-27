package com.chtibizoux.adeapp.ui.home.alarms

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.DefaultAlarmSettings
import com.chtibizoux.adeapp.data.Time

@Composable
fun AddAlarmButton(
    initial: Time,
    alarmSettings: DefaultAlarmSettings,
    onClick: (Alarm) -> Unit
) {
    val context = LocalContext.current
    var intentTime by remember { mutableStateOf(getActionTime(context)) }
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
    if (showTimePicker || intentTime != null) {
        TimePickerDialog(intentTime ?: initial) { time ->
            showTimePicker = false
            intentTime = null
            if (time != null) {
                onClick(Alarm(time, (0..<alarmSettings.repeat).map {
                    Time(time.getMinutesNumber() - alarmSettings.timeUntilEvent + it * alarmSettings.interval)
                }))
            }
        }
    }
}

fun getActionTime(context: Context): Time? {
    val intent = context.findActivity()?.intent
    if (intent?.action == NEW_ALARM_ACTION) {
        val extra = intent.getStringExtra(TIME_EXTRA)
        if (extra != null) {
            return Time.fromString(extra)
        }
    }
    return null
}

fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}