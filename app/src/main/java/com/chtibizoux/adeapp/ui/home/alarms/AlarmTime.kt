package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Time

@Composable
fun AlarmTime(time: Time, updateTime: (Time) -> Unit, deleteTime: () -> Unit) {
    var showTimePicker by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(time.toString(), fontSize = 28.sp, modifier = Modifier.clickable {
            showTimePicker = true
        })
        IconButton(onClick = deleteTime) {
            Icon(Icons.Filled.RemoveCircleOutline, stringResource(R.string.delete))
        }
    }

    if (showTimePicker) {
        TimePickerDialog(time) {
            showTimePicker = false
            if (it != null) {
                updateTime(it)
            }
        }
    }
}
