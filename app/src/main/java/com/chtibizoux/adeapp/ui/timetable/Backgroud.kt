package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun Background(
    startHour: Int,
    endHour: Int,
    hourHeight: Int,
    hourWidth: Float? = null,
    columns: Int = 1
) {
    val mod = Modifier.padding(vertical = (VERTICAL_PADDING - MAIN_DIVIDER_HEIGHT / 2f).dp)
    Column(
        modifier = if (hourWidth == null) mod else mod.width((hourWidth * columns).dp),
        verticalArrangement = Arrangement.spacedBy(((hourHeight - MAIN_DIVIDER_HEIGHT - SECONDARY_DIVIDER_HEIGHT) / 2f).dp)
    ) {
        (startHour * 2..endHour * 2).forEachIndexed { _, nb ->
            if (nb % 2 == 0) {
                HorizontalDivider(thickness = MAIN_DIVIDER_HEIGHT.dp)
            } else {
                HorizontalDivider(
                    Modifier.padding(start = 20.dp, end = 20.dp),
                    thickness = SECONDARY_DIVIDER_HEIGHT.dp
                )
            }
        }
    }
    if (hourWidth != null) {
        Row(
            modifier = Modifier
                .height((hourHeight * (endHour - startHour) + VERTICAL_PADDING * 2).dp)
                .padding(horizontal = (hourWidth - 0.5).dp),
            horizontalArrangement = Arrangement.spacedBy((hourWidth - 1).dp)
        ) {
            (1..<columns).forEach { _ ->
                VerticalDivider()
            }
        }
    }
}
