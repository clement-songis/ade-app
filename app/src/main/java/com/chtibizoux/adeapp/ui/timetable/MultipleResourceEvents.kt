package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.data.xml.getAllChildren
import com.chtibizoux.adeapp.data.xml.getLeaves

@Composable
fun MultipleResourceEvents(
    events: List<Event>,
    children: List<Resource>,
    leaves: List<Resource>,
    navController: NavController,
    startHour: Int,
    hourHeight: Float,
    hourWidth: Float,
) {
    val allChildren = getAllChildren(children)

    val eventsByResource = mutableMapOf<Resource, MutableList<Event>>()
    val otherEvents = mutableListOf<Event>()
    for (event in events) {
        var added = false
        for (resource in allChildren) {
            if (event.resources.any { resource.name == it.name && resource.id == it.id }) {
                val r = eventsByResource[resource]
                if (r === null) {
                    eventsByResource[resource] = mutableListOf(event)
                } else {
                    r.add(event)
                }
                added = true
            }
        }
        if (!added) {
            otherEvents.add(event)
        }
    }

    for ((resource, events) in eventsByResource) {
        val (index, length) = if (resource.children.isNotEmpty()) {
            val eventResourceLeaves = getLeaves(resource.children)
            Pair(
                leaves.indexOf(eventResourceLeaves.first()),
                eventResourceLeaves.size
            )
        } else {
            Pair(leaves.indexOf(resource), 1)
        }

        NonOverlappingEvents(events) { event, width, left ->
            EventElement(
                navController,
                event,
                startHour,
                hourHeight,
                hourWidth,
                length * width,
                index + left
            )
        }
    }
    NonOverlappingEvents(otherEvents) { event, width, left ->
        EventElement(
            navController,
            event,
            startHour,
            hourHeight,
            hourWidth,
            leaves.size * width,
            left
        )
    }
}