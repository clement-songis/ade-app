package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Time

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
