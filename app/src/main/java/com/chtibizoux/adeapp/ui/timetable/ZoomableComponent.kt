package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculatePan
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import kotlin.math.roundToInt

@Composable
fun ZoomableComponent(
    xScrollEnabled: MutableState<Boolean>,
    yScrollEnabled: MutableState<Boolean>,
    sideWidth: Dp = 0.dp,
    header: @Composable (offset: IntOffset, width: Float) -> Unit = { _, _ -> },
    leftSide: @Composable (offset: IntOffset, height: Float) -> Unit = { _, _ -> },
    content: @Composable (width: Float, height: Float) -> Unit,
) {
    var width by remember { mutableIntStateOf(0) }
    var height by remember { mutableIntStateOf(0) }
    var contentWidth by remember { mutableIntStateOf(0) }
    var contentHeight by remember { mutableIntStateOf(0) }

    var scaleX by remember { mutableFloatStateOf(1f) }
    var scaleY by remember { mutableFloatStateOf(1f) }

    var offset by remember { mutableStateOf(Offset.Zero) }

    Column(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        val zoomChange = event.calculateZoom()
                        val offsetChange = event.calculatePan()

                        scaleX = (scaleX * zoomChange).coerceAtLeast(1f)
                        // TODO: separate zoom

                        val widthOverflow = (width - contentWidth)
                            .coerceAtMost(0)
                            .toFloat()
                        val heightOverflow = (width - contentWidth)
                            .coerceAtMost(0)
                            .toFloat()

                        val newOffset = offset + offsetChange / 2f
                        offset = Offset(
                            newOffset.x.coerceIn(widthOverflow, 0f),
                            newOffset.y.coerceIn(heightOverflow, 0f)
                        )

                        val overflowOffset = newOffset - offset
                        xScrollEnabled.value = overflowOffset.x != 0f
                        yScrollEnabled.value = overflowOffset.y != 0f
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        Box(
            Modifier
                .padding(start = sideWidth)
                .overflowable()
        ) {
            header(IntOffset(offset.x.roundToInt(), 0), scaleX * width)
        }
        Row {
            Box(
                Modifier
                    .width(sideWidth)
                    .overflowable()
            ) {
                leftSide(IntOffset(0, offset.y.roundToInt()), scaleY * height)
            }
            Box(
                Modifier
                    .overflowable()
                    .onGloballyPositioned { coordinates ->
                        width = coordinates.size.width
                        height = coordinates.size.height
                    }
            ) {
                Box(
                    Modifier
                        .offset { offset.round() }
                        .onGloballyPositioned { coordinates ->
                            contentWidth = coordinates.size.width
                            contentHeight = coordinates.size.height
                        }
                ) {
                    content(scaleX * width, scaleY * height)
                }
            }
        }
    }
}

fun Modifier.overflowable() =
    this
        .clipToBounds()
        .wrapContentSize(unbounded = true, align = Alignment.TopStart)