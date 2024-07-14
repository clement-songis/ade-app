package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.Event
import kotlin.random.Random

private const val TEXT_HEIGHT = 25

@Composable
fun EventElement(
    navController: NavController,
    event: Event,
    firstHour: Int,
    hourHeight: Float,
    hourWidth: Float? = null,
    size: Int = 1,
    index: Int = 0
) {
    var showDialog by remember { mutableStateOf(false) }

    val startHour = event.startHour.getHourNumber()
    val endHour = event.endHour.getHourNumber()
    val height = endHour - startHour

    val textHeight = (height * hourHeight) / ((height * hourHeight).toInt() / TEXT_HEIGHT)

    val yOffset = (VERTICAL_PADDING + (startHour - firstHour) * hourHeight).dp
    val mod = Modifier.height((height * hourHeight).dp)
    Surface(
        modifier = if (hourWidth == null) {
            mod
                .fillMaxWidth()
                .offset(y = yOffset)
        } else {
            mod
                .width((hourWidth * size).dp)
                .offset(x = (index * hourWidth).dp, y = yOffset)
        },
        color = event.getColor() ?: Color(Random.nextInt(0xffffff)),
        contentColor = Color.Black,
        shape = RoundedCornerShape(10.dp),
        onClick = { showDialog = true }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.height(textHeight.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    event.name,
                    textAlign = TextAlign.Center,
                    // overflow = TextOverflow.Ellipsis, // Don't use it because of wierd text position glitches
                    maxLines = 1
                )
            }
            listOf("classroom", "trainee", "instructor").forEach { category ->
                val resources = event.resources.filter { it.category == category }
                if (resources.isNotEmpty()) {
                    Box(
                        modifier = Modifier.height(textHeight.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            resources.joinToString { it.name },
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            // overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }

    if (showDialog) {
        EventDialog(navController, event) { showDialog = false }
    }
}
