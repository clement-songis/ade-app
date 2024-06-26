package com.chtibizoux.adeapp.ui.timetable.day

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.chtibizoux.adeapp.ui.timetable.MyDatePickerDialog
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun Title(date: Date, goTo: (Date) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }

    Button(onClick = { showDatePicker = true }) {
        Text(
            DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(date),
            fontSize = 18.sp
        )
    }

    if (showDatePicker) {
        MyDatePickerDialog(date.time) {
            showDatePicker = false
            if (it != null) {
                goTo(Date(it))
            }
        }
    }
}
