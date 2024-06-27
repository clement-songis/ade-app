package com.chtibizoux.adeapp.ui.timetable.week

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.ui.timetable.MultipleResourcesHeader

@Composable
fun WeekMultipleResourcesHeader(leaves: List<Resource>, week: List<Day<Event>>, hourWidth: Float, offset: IntOffset = IntOffset.Zero) {
    Row(Modifier.offset { offset }) {
        week.forEach {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    it.date,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width((hourWidth * leaves.size).dp),
                    maxLines = 1
                )
                MultipleResourcesHeader(leaves, hourWidth)
            }
        }
    }
}
