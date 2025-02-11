package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.runtime.Composable
import com.chtibizoux.adeapp.data.xml.Event
import kotlin.collections.forEach

@Composable
fun NonOverlappingEvents(
    events: List<Event>,
    inSameGroup: (a: Event, b: Event) -> Boolean = { a, b -> true },
    content: @Composable (event: Event, width: Float, left: Float) -> Unit
) {
    val eventList = events.sortedWith(
        compareBy(
            { it.startHour.getMinutesNumber() },
            { it.endHour.getMinutesNumber() }
        )
    )

    val columns: MutableList<MutableList<Event>> = mutableListOf()
    var previousHighestEnd: Int? = null

    eventList.forEach { event ->
        if (previousHighestEnd !== null && event.startHour.getMinutesNumber() >= previousHighestEnd) {
            RenderEvents(columns, content)
            columns.clear()
            previousHighestEnd = null
        }

        var isInserted = false
        for (column in columns) {
            val last = column[column.count() - 1]
            if (!isColliding(last, event) && !inSameGroup(last, event)) {
                column.add(event)
                isInserted = true
                break
            }
        }
        if (!isInserted) {
            columns.add(mutableListOf(event))
        }

        if (previousHighestEnd === null || event.endHour.getMinutesNumber() > previousHighestEnd) {
            previousHighestEnd = event.endHour.getMinutesNumber()
        }
    }

    if (columns.count() > 0) {
        RenderEvents(columns, content)
    }
}

@Composable
fun RenderEvents(
    columns: List<List<Event>>,
    content: @Composable (event: Event, width: Float, left: Float) -> Unit
) {
    val count = columns.count().toFloat()
    val width = 1 / count
    columns.forEachIndexed { i, events ->
        val left = i / count
        events.forEach { event ->
            content(event, width, left)
        }
    }
}

fun isColliding(a: Event, b: Event): Boolean {
    return a.startHour.getMinutesNumber() < b.endHour.getMinutesNumber() && a.endHour.getMinutesNumber() > b.startHour.getMinutesNumber()
}