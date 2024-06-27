package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.xml.Calendar
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.ui.timetable.day.DayComponent
import com.chtibizoux.adeapp.ui.timetable.week.WeekComponent
import com.chtibizoux.adeapp.ui.timetable.week.Weeks
import java.util.Date

const val HOUR_HEIGHT = 60f
const val MAIN_DIVIDER_HEIGHT = 2
const val SECONDARY_DIVIDER_HEIGHT = 1
const val VERTICAL_PADDING = 20

const val START_HOUR = 8
const val END_HOUR = 19

@Composable
fun WaitForCalendar(
    navController: NavController,
    calendar: Calendar?,
    children: List<Resource>,
    previousButton: Boolean = false,
    date: Date = Date(),
    refreshCalendar: suspend () -> Unit
) {
    if (calendar == null) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            Text(stringResource(R.string.no_calendar), color = Color.White)
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp), strokeCap = StrokeCap.Round, strokeWidth = 8.dp
            )
        }
    } else if (calendar.days.isEmpty()) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.no_calendar), color = Color.White)
        }
    } else {
        TimetableContent(
            navController,
            calendar,
            sortResources(children),
            previousButton,
            date,
            refreshCalendar
        )
    }
}

fun sortResources(resources: List<Resource>): List<Resource> {
    return resources.map { it.copy(children = sortResources(it.children)) }.sortedBy { it.name }
}

@Composable
fun TimetableContent(
    navController: NavController,
    calendar: Calendar,
    children: List<Resource>,
    previousButton: Boolean = false,
    date: Date,
    refreshCalendar: suspend () -> Unit
) {
    var isWeekView by remember { mutableStateOf(false) }
    var initialDate by remember { mutableStateOf(date) }

    if (isWeekView) {
        val weeks = Weeks(calendar.days)
        TimetableScaffold(
            true,
            initialDate,
            weeks.size,
            { weeks.getPage(it) },
            { weeks[it].first().getDate() },
            {
                initialDate = weeks[it].first().getDate()
                isWeekView = false
            },
            navController,
            previousButton,
            refreshCalendar
        ) { page, xScrollEnabled, yScrollEnabled ->
            WeekComponent(
                navController,
                weeks[page],
                children,
                xScrollEnabled,
                yScrollEnabled
            )
        }
    } else {
        TimetableScaffold(
            false,
            initialDate,
            calendar.days.size,
            { calendar.getPage(it) },
            { calendar.days[it].getDate() },
            {
                initialDate = calendar.days[it].getDate()
                isWeekView = true
            },
            navController,
            previousButton,
            refreshCalendar
        ) { page, xScrollEnabled, yScrollEnabled ->
            DayComponent(
                navController,
                calendar.days[page],
                children,
                xScrollEnabled,
                yScrollEnabled
            )
        }
    }

//    val weeks = Weeks(calendar.days)
//    TimetableScaffold(
//        isWeekView,
//        initialDate,
//        if (isWeekView) weeks.size else calendar.days.size,
//        { if (isWeekView) weeks.getPage(it) else calendar.getPage(it) },
//        { (if (isWeekView) weeks[it].first() else calendar.days[it]).getDate() },
//        {
//            initialDate = (if (isWeekView) weeks[it].first() else calendar.days[it]).getDate()
//            isWeekView = true
//        },
//        navController,
//        previousButton,
//        refreshCalendar
//    ) { page, xScrollEnabled, yScrollEnabled ->
//        if (isWeekView) {
//            WeekComponent(
//                navController,
//                weeks[page],
//                children,
//                xScrollEnabled,
//                yScrollEnabled
//            )
//        } else {
//            DayComponent(
//                navController,
//                calendar.days[page],
//                children,
//                xScrollEnabled,
//                yScrollEnabled
//            )
//        }
//    }
}
