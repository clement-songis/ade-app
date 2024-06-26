package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chtibizoux.adeapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(initialDate: Long, onDateSelected: (Long?) -> Unit) {
    val datePickerState = rememberDatePickerState(initialDate)

    DatePickerDialog(onDismissRequest = { onDateSelected(null) }, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
        }) {
            Text(stringResource(R.string.ok))
        }
    }) {
        DatePicker(datePickerState)
    }
}
