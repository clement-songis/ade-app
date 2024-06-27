package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.data.xml.Resource

@Composable
fun MultipleResourcesHeader(leaves: List<Resource>, hourWidth: Float, offset: IntOffset = IntOffset.Zero) {
    Row(Modifier.offset { offset }) {
        leaves.forEach {
            Text(
                it.name,
                modifier = Modifier.width((hourWidth).dp),
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }
    }
}
