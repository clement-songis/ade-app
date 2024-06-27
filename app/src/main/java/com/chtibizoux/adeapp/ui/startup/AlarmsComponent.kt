package com.chtibizoux.adeapp.ui.startup

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.data.Alarm

@Composable
fun AlarmsComponent(alarms: List<Alarm>) {
    Column {
        alarms.forEach { alarm ->
            Text(
                text = "${alarm.forHour} ➜ ${alarm.hours.joinToString { it.toString() }}",
                modifier = Modifier.padding(10.dp)
            )
        }
    }
}