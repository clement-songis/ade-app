package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.ui.startup.TimePickerButton
import kotlinx.collections.immutable.persistentListOf

@Composable
fun Alarms(navController: NavHostController) {
    Surface(
        modifier = Modifier
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Text("Not implemented")

//        Column {
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
//        }
    }
}