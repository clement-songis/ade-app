package com.chtibizoux.adeapp.alarms.ui

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.alarms.AlarmService

@Composable
fun FullScreenAlarm() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(vertical = 80.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimeComponent()

            Button(
                onClick = {
                    context.sendBroadcast(Intent(AlarmService.SNOOZE_ALARM_ACTION))
                },
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(
                    stringResource(R.string.snooze),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(horizontal = 30.dp, vertical = 20.dp)
                )
            }

            Button(
                onClick = {
                    context.sendBroadcast(Intent(AlarmService.STOP_ALARM_ACTION))
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp)
            ) {
                Text(
                    stringResource(R.string.cancel),
                    fontSize = 20.sp,
                    modifier = Modifier.padding(vertical = 20.dp)
                )
            }
        }
    }
}
