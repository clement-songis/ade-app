package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Label
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import com.chtibizoux.adeapp.R

@Composable
fun Label(
    isSelected: Boolean,
    label: String,
    onClick: () -> Unit,
    onChange: (label: String) -> Unit
) {
    var showLabelPicker by remember { mutableStateOf(false) }
    if (isSelected || label.isNotEmpty()) {
        Surface(
            onClick = {
                if (isSelected) {
                    showLabelPicker = true
                } else {
                    onClick()
                }
            }, modifier = Modifier
                .height(60.dp)
                .fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSelected) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Label, stringResource(R.string.label)
                    )
                }
                Text(
                    label.ifEmpty { stringResource(R.string.add_label) },
                    color = if (label.isEmpty()) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.onBackground
                )
            }
        }

        if (showLabelPicker) {
            TextPicker(label, stringResource(R.string.label)) {
                showLabelPicker = false
                if (it != null) {
                    onChange(it)
                }
            }
        }
    }
}
