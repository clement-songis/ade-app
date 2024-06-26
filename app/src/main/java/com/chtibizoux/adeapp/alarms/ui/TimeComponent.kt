package com.chtibizoux.adeapp.alarms.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun TimeComponent() {
    val currentDateTime = remember { mutableStateOf(getCurrentDateTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime.value = getCurrentDateTime()
            delay(1000L) // Update every second
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = currentDateTime.value.second, fontSize = 60.sp)
        Text(text = currentDateTime.value.first, fontSize = 20.sp)
    }
}

fun getCurrentDateTime(): Pair<String, String> {
    val calendar = Calendar.getInstance()
    val dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
    val timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
    val currentDate = dateFormat.format(calendar.time)
    val currentTime = timeFormat.format(calendar.time)
    return Pair(currentDate, currentTime)
}