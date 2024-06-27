package com.chtibizoux.adeapp.ui.timetable.week

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.ui.timetable.Background
import com.chtibizoux.adeapp.ui.timetable.EventElement
import com.chtibizoux.adeapp.ui.timetable.HOUR_HEIGHT
import com.chtibizoux.adeapp.ui.timetable.Hours
import com.chtibizoux.adeapp.ui.timetable.TIME_WIDTH
import com.chtibizoux.adeapp.ui.timetable.VERTICAL_PADDING
import com.chtibizoux.adeapp.ui.timetable.ZoomableComponent

@Composable
fun SimpleResource(
    week: List<Day<Event>>,
    navController: NavController,
    startHour: Int,
    endHour: Int,
    xScrollEnabled: MutableState<Boolean>,
    yScrollEnabled: MutableState<Boolean>
) {
    fun getHourWidth(width: Float) = (width / week.size).coerceAtLeast(1f)

    //    fun getHourHeight(height: Float) = (height - VERTICAL_PADDING * 2) / (END_HOUR - START_HOUR)
    fun getHourHeight(height: Float) = HOUR_HEIGHT

    ZoomableComponent(
        xScrollEnabled,
        yScrollEnabled,
        TIME_WIDTH.dp,
        header = { offset, width ->
            WeekHeader(week, getHourWidth(width), offset)
        },
        leftSide = { offset, height ->
            Hours(startHour, endHour, getHourHeight(height), offset)
        }
    ) { width, height ->
        val hourWidth = getHourWidth(width)
        val hourHeight = getHourHeight(height)

        Background(startHour, endHour, hourHeight * (endHour - startHour) + VERTICAL_PADDING * 2, hourWidth, week.size)
        Row {
            week.forEach { day ->
                Box(Modifier.width(hourWidth.dp)) {
                    day.events.forEach { event ->
                        EventElement(
                            navController,
                            event,
                            startHour,
                            hourHeight
                        )
                    }
                }
            }
        }
    }
}
