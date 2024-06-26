package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
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

@Composable
fun DeleteAlarmButton(onConfirm: () -> Unit) {
    var showConfirmDialog by remember { mutableStateOf(false) }
    Row(modifier = Modifier
        .clickable { showConfirmDialog = true }
        .fillMaxWidth()
        .minimumInteractiveComponentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(Icons.Filled.Delete, stringResource(R.string.delete))
        Text(stringResource(R.string.delete))
    }

    if (showConfirmDialog) {
        ConfirmDialog(stringResource(R.string.confirm_alarm_delete)) {
            showConfirmDialog = false
            if (it) {
                onConfirm()
            }
        }
    }
}
