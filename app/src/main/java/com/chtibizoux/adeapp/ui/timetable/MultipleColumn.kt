package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.data.xml.Resource

@Composable
fun MultipleColumn(
    events: List<Event>,
    navController: NavController,
    children: List<Resource>,
    scrollTo: (offset: Int) -> Unit,
    refresh: () -> Unit
) {
    val localDensity = LocalDensity.current

    val leaves = getLeaves(children)
    val allChildren = getAllChildren(children)

    var width by remember { mutableIntStateOf(leaves.size) }
    var height by remember { mutableIntStateOf(0) }

    var scale by remember { mutableFloatStateOf(1f) }

    val hourWidth = (width / leaves.size.toFloat()) * scale

    var offset by remember { mutableStateOf(Offset.Zero) }
    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceAtLeast(1f)

        val newOffset = offset + offsetChange / 2f
        offset = Offset(
            newOffset.x.coerceAtMost(0f).coerceAtLeast(-width * (scale - 1)),
            newOffset.y.coerceAtMost(0f).coerceAtLeast(
                (height.toFloat() - (HOUR_HEIGHT * (END_HOUR - START_HOUR) + VERTICAL_PADDING * 2))
                    .coerceAtMost(0f)
            )
        )
        if (newOffset.x > MINIMUM_SWIPE_OFFSET) {
            scrollTo(-1)
        } else if (newOffset.x < -width * (scale - 1) - MINIMUM_SWIPE_OFFSET) {
            scrollTo(1)
        } else if (newOffset.y > MINIMUM_SWIPE_OFFSET) {
            refresh()
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                width =
                    with(localDensity) { coordinates.size.width.toDp().value.toInt() - TIME_WIDTH }
                height =
                    with(localDensity) { coordinates.size.height.toDp().value.toInt() - HEADER_HEIGHT }
            }
            .transformable(transformableState)
            .wrapContentSize(unbounded = true, align = Alignment.TopStart)
    ) {
        Box(
            Modifier
                .padding(start = TIME_WIDTH.dp)
                .clipToBounds()
        ) {
            MultipleResourcesHeader(leaves, hourWidth, offset.x)
        }
        Row(Modifier.clipToBounds()) {
            Hours(START_HOUR, END_HOUR, HOUR_HEIGHT, offset.y)
            Box(
                Modifier.clipToBounds()
            ) {
                Box(Modifier.offset(offset.x.dp, offset.y.dp)) {
                    Background(START_HOUR, END_HOUR, HOUR_HEIGHT, hourWidth, leaves.size)
                    Box {
                        events.forEach { event ->
                            val resource =
                                allChildren.find { resource -> event.resources.find { resource.name == it.name && resource.id == it.id } != null }

                            if (resource != null) {
                                val (index, length) = if (resource.children.isNotEmpty()) {
                                    val eventResourceLeaves = getLeaves(resource.children)
                                    Pair(
                                        leaves.indexOf(eventResourceLeaves.first()),
                                        eventResourceLeaves.size
                                    )
                                } else {
                                    Pair(leaves.indexOf(resource), 1)
                                }

                                EventElement(
                                    navController,
                                    event,
                                    START_HOUR,
                                    HOUR_HEIGHT,
                                    hourWidth,
                                    length,
                                    index
                                )
                            } else {
                                EventElement(
                                    navController,
                                    event,
                                    START_HOUR,
                                    HOUR_HEIGHT,
                                    hourWidth,
                                    leaves.size
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun getLeaves(resources: List<Resource>): List<Resource> {
    val leaves: MutableList<Resource> = mutableListOf()
    resources.forEach {
        if (it.children.isEmpty()) {
            leaves.add(it)
        } else {
            leaves.addAll(getLeaves(it.children))
        }
    }
    return leaves
}

fun getAllChildren(resources: List<Resource>): List<Resource> {
    return resources + resources.flatMap { getAllChildren(it.children) }
}
