package com.chtibizoux.adeapp.data.xml

import kotlinx.collections.immutable.PersistentList
import kotlinx.serialization.Serializable


@Serializable
data class Calendar(
    val days: PersistentList<Day>,
)

@Serializable
data class Day(
    val date: String,
    val events: PersistentList<Event>,
)

@Serializable
data class Event(
    val name: String,
    val date: String,
    val duration: Int,
    val startHour: String,
    val endHour: String,
    val color: String,
    val resources: PersistentList<Resource>,
)

@Serializable
data class Resource(
    val name: String,
    val category: String,
)
