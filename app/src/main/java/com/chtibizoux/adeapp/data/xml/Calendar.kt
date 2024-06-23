package com.chtibizoux.adeapp.data.xml

import android.icu.text.SimpleDateFormat
import androidx.compose.ui.graphics.Color
import com.chtibizoux.adeapp.data.Time
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.Locale

val calendarDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

@Serializable
data class Calendar(val days: List<Day<Event>>) {
    fun getPage(date: Date = Date()): Int {
        val index = this.days.indexOfFirst { it.getDate() >= calendarDateFormat.parse(calendarDateFormat.format(date)) }
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
    val date: String,// TODO: Parse date
    val duration: Int,
    val startHour: Time,
    val endHour: Time,
    val color: String,// TODO: Parse color
    val resources: List<SimpleResource>,
) {
    fun getColor(): Color {
        val (r, g, b) = color.split(",")
        return Color(r.toInt(), g.toInt(), b.toInt())
    }
}

@Serializable
data class SimpleEvent(
    val name: String,
    val date: String,// TODO: Parse date
    val startHour: Time,
    val endHour: Time,
)

@Serializable
data class SimpleResource(
    val name: String,
    val category: String,
    val id: Int,
)
