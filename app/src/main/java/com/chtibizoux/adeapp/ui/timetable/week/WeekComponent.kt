package com.chtibizoux.adeapp.ui.timetable.week

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.ui.timetable.END_HOUR
import com.chtibizoux.adeapp.ui.timetable.START_HOUR

@Composable
fun WeekComponent(
    navController: NavController,
    week: List<Day<Event>>,
    children: List<Resource>,
) {
    if (children.isEmpty()) {
        SimpleResource(week, navController, START_HOUR, END_HOUR)
    } else {
        MultipleResource(week, navController, children, START_HOUR, END_HOUR)
    }
}
