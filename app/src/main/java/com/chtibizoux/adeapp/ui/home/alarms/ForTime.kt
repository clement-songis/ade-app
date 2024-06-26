package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Time

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
