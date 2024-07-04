package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.chtibizoux.adeapp.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


@Composable
fun TimetableTitle(date: Date, isWeekView: Boolean, goTo: (Date) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }

    Button(onClick = { showDatePicker = true }) {
        Text(
            if (isWeekView) getWeekTitle(date) else getDayTitle(date),
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

private fun getDayTitle(date: Date) =
    DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(date)

@Composable
private fun getWeekTitle(date: Date): String {
    val firstDayOfWeek = Calendar.getInstance().apply {
        time = date
        set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
    }

    val lastDayOfWeek = (firstDayOfWeek.clone() as Calendar).apply {
        add(Calendar.DAY_OF_WEEK, 6)
    }

    return when {
        firstDayOfWeek.get(Calendar.YEAR) != lastDayOfWeek.get(Calendar.YEAR) -> {
            val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM)

            val formatFirstDay = dateFormat.format(firstDayOfWeek.time)
            val formatLastDay = dateFormat.format(lastDayOfWeek.time)
            stringResource(R.string.big_interval, formatFirstDay, formatLastDay)
        }

        firstDayOfWeek.get(Calendar.MONTH) != lastDayOfWeek.get(Calendar.MONTH) -> {
            val monthAndYearFormat =
                SimpleDateFormat(stringResource(R.string.date_month_pattern), Locale.getDefault())

            val formatFirstDay = monthAndYearFormat.format(firstDayOfWeek.time)
            val formatLastDay = monthAndYearFormat.format(lastDayOfWeek.time)
            stringResource(
                R.string.medium_interval,
                formatFirstDay,
                formatLastDay,
                firstDayOfWeek.get(Calendar.YEAR)
            )
        }

        else -> {
            // SimpleDateFormat("MMMM", Locale.getDefault()).format(firstDayOfWeek.time)
            val monthName =
                firstDayOfWeek.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
                    ?: "why ?"
            stringResource(
                R.string.short_interval,
                firstDayOfWeek.get(Calendar.DAY_OF_MONTH),
                lastDayOfWeek.get(Calendar.DAY_OF_MONTH),
                monthName,
                firstDayOfWeek.get(Calendar.YEAR)
            )
        }
    }
}