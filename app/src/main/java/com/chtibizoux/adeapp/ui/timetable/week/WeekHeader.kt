package com.chtibizoux.adeapp.ui.timetable.week

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import com.chtibizoux.adeapp.ui.timetable.SECONDARY_DIVIDER_HEIGHT
import java.util.Locale

@Composable
fun WeekHeader(week: List<Day<Event>>, hourWidth: Float, offset: IntOffset = IntOffset.Zero) {
    val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
    Row(
        Modifier
            .offset { offset }
            .width((hourWidth * week.size).dp)
            .height(IntrinsicSize.Min)
    ) {
        week.forEach {
            val day = dayFormat.format(it.getDate())
            Text(
                day.substring(0, 1).uppercase() + day.substring(1),
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
