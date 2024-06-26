package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.DefaultAlarmSettings
import com.chtibizoux.adeapp.ui.SettingsViewModel

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
                    Label(selected == i, alarm.label, onClick) {
                        viewModel.updateLabel(i, it)
                    }
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
                        AlarmTime(time, {
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
                DeleteAlarmButton {
                    viewModel.removeAlarm(alarm)
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
