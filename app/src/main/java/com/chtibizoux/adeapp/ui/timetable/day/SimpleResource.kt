package com.chtibizoux.adeapp.ui.timetable.day

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.ui.timetable.Background
import com.chtibizoux.adeapp.ui.timetable.EventElement
import com.chtibizoux.adeapp.ui.timetable.Hours
import com.chtibizoux.adeapp.ui.timetable.TIME_WIDTH
import com.chtibizoux.adeapp.ui.timetable.VERTICAL_PADDING
import com.chtibizoux.adeapp.ui.timetable.ZoomableComponent

@Composable
fun SimpleResource(events: List<Event>, navController: NavController, startHour: Int, endHour: Int) {
    fun getHourWidth(width: Float) = width.coerceAtLeast(1f)

    fun getHourHeight(height: Float) = (height - VERTICAL_PADDING * 2) / (endHour - startHour)
//    fun getHourHeight(height: Float) = HOUR_HEIGHT

    ZoomableComponent(
        TIME_WIDTH.dp,
        leftSide = { offset, height ->
            Hours(startHour, endHour, getHourHeight(height), offset)
        },
        enableHorizontalZoom = false
    ) { width, height ->
        val hourWidth = getHourWidth(width)
        val hourHeight = getHourHeight(height)

        Background(startHour, endHour, hourHeight, hourWidth)
        Box {
            events.forEach { event ->
                EventElement(navController, event, startHour, hourHeight, hourWidth)
            }
        }
    }
}
