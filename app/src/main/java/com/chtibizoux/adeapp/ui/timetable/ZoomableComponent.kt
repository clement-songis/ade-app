package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@Composable
fun ZoomableComponent(
    sideWidth: Dp = 0.dp,
    header: @Composable (offset: IntOffset, width: Float) -> Unit = { _, _ -> },
    leftSide: @Composable (offset: IntOffset, height: Float) -> Unit = { _, _ -> },
    content: @Composable (width: Float, height: Float) -> Unit,
) {
    val localDensity = LocalDensity.current

    var width by remember { mutableIntStateOf(0) }
    var height by remember { mutableIntStateOf(0) }
    var contentWidth by remember { mutableIntStateOf(0) }
    var contentHeight by remember { mutableIntStateOf(0) }

    var scaleX by remember { mutableFloatStateOf(1f) }
    var scaleY by remember { mutableFloatStateOf(1f) }

    val scaledWidth = with(localDensity) { (width * scaleX).toDp().value }
    val scaledHeight = with(localDensity) { (height * scaleY).toDp().value }

    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Column(
        Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    awaitFirstDown()
                    do {
                        val event = awaitPointerEvent()
                        val zoomChange = event.calculateZoom()

                        scaleX = (scaleX * zoomChange).coerceAtLeast(1f)
                        // TODO: separate zoom
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        Box(
            Modifier
                .padding(start = sideWidth)
                .overflowable()
        ) {
            header(IntOffset(-horizontalScrollState.value, 0), scaledWidth)
        }
        Row {
            Box(
                Modifier
                    .width(sideWidth)
                    .overflowable()
            ) {
                leftSide(IntOffset(0, -verticalScrollState.value), scaledHeight)
            }
            Box(
                Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        width = coordinates.size.width
                        height = coordinates.size.height
                    }
            ) {
                Box(
                    Modifier
                        .horizontalScroll(horizontalScrollState)
                        .verticalScroll(verticalScrollState)
                        .onGloballyPositioned { coordinates ->
                            contentWidth = coordinates.size.width
                            contentHeight = coordinates.size.height
                        }
                ) {
                    content(scaledWidth, scaledHeight)
                }
            }
        }
    }
}

fun Modifier.overflowable() =
    this
        .clipToBounds()
        .wrapContentSize(unbounded = true, align = Alignment.TopStart)