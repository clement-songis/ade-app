package com.chtibizoux.adeapp.ui.timetable.day

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.data.xml.getAllChildren
import com.chtibizoux.adeapp.data.xml.getLeaves
import com.chtibizoux.adeapp.ui.timetable.Background
import com.chtibizoux.adeapp.ui.timetable.EventElement
import com.chtibizoux.adeapp.ui.timetable.Hours
import com.chtibizoux.adeapp.ui.timetable.MultipleResourcesHeader
import com.chtibizoux.adeapp.ui.timetable.TIME_WIDTH
import com.chtibizoux.adeapp.ui.timetable.VERTICAL_PADDING
import com.chtibizoux.adeapp.ui.timetable.ZoomableComponent

@Composable
fun MultipleResource(
    events: List<Event>,
    navController: NavController,
    children: List<Resource>,
    startHour: Int,
    endHour: Int
) {
    val leaves = getLeaves(children)
    val allChildren = getAllChildren(children)

    fun getHourWidth(width: Float) = (width / leaves.size).coerceAtLeast(1f)

    fun getHourHeight(height: Float) = (height - VERTICAL_PADDING * 2) / (endHour - startHour)

    ZoomableComponent(
        TIME_WIDTH.dp,
        header = { offset, width ->
            MultipleResourcesHeader(leaves, getHourWidth(width), offset)
        },
        leftSide = { offset, height ->
            Hours(startHour, endHour, getHourHeight(height), offset)
        }
    ) { width, height ->
        val hourWidth = getHourWidth(width)
        val hourHeight = getHourHeight(height)

        Background(startHour, endHour, hourHeight, hourWidth, leaves.size)
        Box {
            events.forEach { event ->
                val resource =
                    allChildren.find { resource -> event.resources.find { resource.name == it.name && resource.id == it.id } != null }

                if (resource != null) {
                    val (index, length) = if (resource.children.isNotEmpty()) {
                        val eventResourceLeaves = getLeaves(resource.children)
                        Pair(
                            leaves.indexOf(eventResourceLeaves.first()),
                            eventResourceLeaves.size
                        )
                    } else {
                        Pair(leaves.indexOf(resource), 1)
                    }

                    EventElement(
                        navController,
                        event,
                        startHour,
                        hourHeight,
                        hourWidth,
                        length,
                        index
                    )
                } else {
                    EventElement(
                        navController,
                        event,
                        startHour,
                        hourHeight,
                        hourWidth,
                        leaves.size
                    )
                }
            }
        }
    }
}
