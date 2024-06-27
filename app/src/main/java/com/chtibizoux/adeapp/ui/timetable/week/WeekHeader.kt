package com.chtibizoux.adeapp.ui.timetable.week

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.ui.timetable.MAIN_DIVIDER_HEIGHT
import com.chtibizoux.adeapp.ui.timetable.SECONDARY_DIVIDER_HEIGHT

@Composable
fun WeekHeader(week: List<Day<Event>>, hourWidth: Float, offset: IntOffset = IntOffset.Zero) {
    Row(
        Modifier
            .offset { offset }
            .width((hourWidth * week.size).dp)
            .height(IntrinsicSize.Min)
    ) {
        week.forEach {
            Text(
                it.date,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            if (it !== week.last()) {
                VerticalDivider(thickness = SECONDARY_DIVIDER_HEIGHT.dp)
            }
        }
    }
}
