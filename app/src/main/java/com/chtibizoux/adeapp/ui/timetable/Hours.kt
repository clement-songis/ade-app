package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

const val TIME_WIDTH = 70

private const val ROW_HEIGHT = 20

@Composable
fun Hours(startHour: Int, endHour: Int, hourHeight: Float, offset: IntOffset = IntOffset.Zero) {
    Row(Modifier.width(TIME_WIDTH.dp)) {
        Column(
            modifier = Modifier
                .padding(vertical = (VERTICAL_PADDING - ROW_HEIGHT / 2f).dp)
                .offset { offset }
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy((hourHeight - ROW_HEIGHT).dp)
        ) {
            (startHour..endHour).forEachIndexed { _, nb ->
                Row(
                    Modifier.height(ROW_HEIGHT.dp), verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${nb.toString().padStart(2, '0')}:00",
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 15.dp, end = 10.dp)
                    )
                    HorizontalDivider(thickness = MAIN_DIVIDER_HEIGHT.dp)
                }
            }
        }
        VerticalDivider(
            Modifier
                .offset { offset }
                .height((hourHeight * (endHour - startHour) + VERTICAL_PADDING * 2).dp),
            thickness = MAIN_DIVIDER_HEIGHT.dp
        )
    }
}
