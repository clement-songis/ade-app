package com.chtibizoux.adeapp.data.xml

import android.icu.text.SimpleDateFormat
import androidx.compose.ui.graphics.Color
import com.chtibizoux.adeapp.data.Time
import kotlinx.serialization.Serializable
import java.util.Calendar
import java.util.Date
import java.util.Locale

val calendarDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

@Serializable
data class Calendar(val days: List<Day<Event>>) {
    fun getPage(date: Date = Date()): Int {
        val index = this.days.indexOfFirst {
            val lastHour = it.events.last().endHour
            val calendar = Calendar.getInstance().apply {
                time = it.getDate()
                set(Calendar.HOUR_OF_DAY, lastHour.hour)
                set(Calendar.MINUTE, lastHour.minute)
            }
            calendar.time >= date
        }
        return if (index == -1) this.days.size - 1 else index
    }
}

@Serializable
data class SimpleCalendar(
    val days: List<Day<SimpleEvent>>,
)

@Serializable
data class Day<T>(
    val date: String,
    val events: List<T>,
) {
    fun getDate(): Date {
        return calendarDateFormat.parse(this.date)
    }
}

@Serializable
data class Event(
    val name: String,
    val date: String,
    val duration: Int,
    val startHour: Time,
    val endHour: Time,
    val color: String,
    val resources: List<SimpleResource>,
) {
    fun getColor(): Color? {
        val values = color.split(",")
        if (values.size != 3) return null
        val (r, g, b) = values.map { it.toIntOrNull() ?: return@getColor null }
        return Color(r, g, b)
    }
}

@Serializable
data class SimpleEvent(
    val name: String,
    val date: String,
    val startHour: Time,
    val endHour: Time,
)

@Serializable
data class SimpleResource(
    val name: String,
    val category: String,
    val id: Int,
)
