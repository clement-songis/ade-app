package com.chtibizoux.adeapp.data.xml

import android.icu.text.SimpleDateFormat
import com.chtibizoux.adeapp.data.Time
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.Locale



@Serializable
data class Calendar(val days: List<Day<Event>>) {
    fun getPage(date: Date = Date()): Int {
        val index = this.days.indexOfFirst { it.getDate() >= dateFormat.parse(dateFormat.format(date)) }
        return if (index == -1) this.days.size else index
    }
}

@Serializable
data class SimpleCalendar(
    val days: List<Day<SimpleEvent>>,
)

val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)

@Serializable
data class Day<T>(
    val date: String,
    val events: List<T>,
) {
    fun getDate(): Date {
        return dateFormat.parse(this.date)
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
)

@Serializable
data class SimpleEvent(
    val name: String,
    val date: String,// TODO: Parse date
    val startHour: Time,
    val endHour: Time,
)

data class ResourceTree(
    val categories: List<Category>
)

data class Category(
    val name: String,
    val resources: List<Resource>
)

data class Resource(
    val id: Int,
    val name: String,
    val children: List<Resource>
)

@Serializable
data class SimpleResource(
    val name: String,
    val category: String,
    val id: Int,
)
