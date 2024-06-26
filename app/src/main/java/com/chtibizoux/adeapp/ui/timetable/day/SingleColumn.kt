package com.chtibizoux.adeapp.ui.timetable.day

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.ui.timetable.Background
import com.chtibizoux.adeapp.ui.timetable.EventElement
import com.chtibizoux.adeapp.ui.timetable.HOUR_HEIGHT
import com.chtibizoux.adeapp.ui.timetable.Hours

@Composable
fun SingleColumn(events: List<Event>, navController: NavController, startHour: Int, endHour: Int) {
    Row(Modifier.verticalScroll(rememberScrollState())) {
        Hours(startHour, endHour, HOUR_HEIGHT)
        Box {
            Background(startHour, endHour, HOUR_HEIGHT)
            Box {
                events.forEach { event ->
                    EventElement(navController, event, startHour, HOUR_HEIGHT)
                }
            }
        }
    }
}
