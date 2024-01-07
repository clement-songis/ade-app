package com.chtibizoux.adeapp.data.xml

import kotlinx.serialization.Serializable


@Serializable
data class Calendar(
    val days: List<Day<Event>>,
)

@Serializable
data class SimpleCalendar(
    val days: List<Day<SimpleEvent>>,
)

@Serializable
data class Day<T>(
    val date: String,
    val events: List<T>,
)

@Serializable
data class SimpleEvent(
    val name: String,
    val date: String,
    val startHour: String,
    val endHour: String,
)

@Serializable
data class Event(
    val name: String,
    val date: String,
    val duration: Int,
    val startHour: String,
    val endHour: String,
    val color: String,
    val resources: List<Resource>,
)

@Serializable
data class Resource(
    val name: String,
    val category: String,
)
