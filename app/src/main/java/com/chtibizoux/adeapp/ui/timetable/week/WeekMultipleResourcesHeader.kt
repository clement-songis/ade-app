package com.chtibizoux.adeapp.ui.timetable.week

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.ui.timetable.MAIN_DIVIDER_HEIGHT
import com.chtibizoux.adeapp.ui.timetable.MultipleResourcesHeader

@Composable
fun WeekMultipleResourcesHeader(
    leaves: List<Resource>,
    week: List<Day<Event>>,
    hourWidth: Float,
    offset: IntOffset = IntOffset.Zero
) {
    Row(
        Modifier
            .offset { offset }
            .width((hourWidth * leaves.size * week.size).dp)
            .height(IntrinsicSize.Min)
    ) {
        week.forEach {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    it.date,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                MultipleResourcesHeader(leaves, hourWidth)
            }
            if (it !== week.last()) {
                VerticalDivider(thickness = MAIN_DIVIDER_HEIGHT.dp)
            }
        }
    }
}
