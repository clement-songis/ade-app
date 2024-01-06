package com.chtibizoux.adeapp.data.ics

import kotlinx.collections.immutable.PersistentList
import kotlinx.serialization.Serializable

@Serializable
data class MyCalendar(val days: PersistentList<Day>)

@Serializable
data class MyDate(val day: Int, val month: Int, val year: Int)

@Serializable
data class Day(
//    val date: String,
    val date: MyDate,
    val events: PersistentList<Event>,
)

@Serializable
data class Event(
    val summary: String,
    val description: String,
    val location: String? = null,
    val dtStart: Long,
    val dtEnd: Long,
)

data class EventBuilder(
    var summary: String? = null,
    var description: String? = null,
    var location: String? = null,
    var dtStart: Long? = null,
    var dtEnd: Long? = null) {
    fun build() = Event(summary!!, description!!, location, dtStart!!, dtEnd!!)
}