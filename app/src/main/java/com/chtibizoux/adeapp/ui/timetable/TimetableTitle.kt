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
import java.time.DayOfWeek
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.time.temporal.TemporalAdjusters
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
    val localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
    val firstDayOfWeek = localDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val lastDayOfWeek = firstDayOfWeek.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))

    return when {
        firstDayOfWeek.year != lastDayOfWeek.year -> {
            val formatter = DateTimeFormatter.ofPattern(
                stringResource(R.string.date_month_pattern),
                Locale.getDefault()
            )

            val formatFirstDay = firstDayOfWeek.format(formatter)
            val formatLastDay = lastDayOfWeek.format(formatter)
            stringResource(R.string.big_interval, formatFirstDay, formatLastDay)
        }

        firstDayOfWeek.month != lastDayOfWeek.month -> {
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
                .withLocale(Locale.getDefault())

            val formatFirstDay = firstDayOfWeek.format(formatter)
            val formatLastDay = lastDayOfWeek.format(formatter)
            stringResource(
                R.string.medium_interval,
                formatFirstDay,
                formatLastDay,
                firstDayOfWeek.year
            )
        }

        else -> stringResource(
            R.string.short_interval,
            firstDayOfWeek.dayOfMonth,
            lastDayOfWeek.dayOfMonth,
            firstDayOfWeek.month.getDisplayName(TextStyle.FULL, Locale.getDefault()),
            firstDayOfWeek.year
        )
    }
}