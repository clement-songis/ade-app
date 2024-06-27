package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.chtibizoux.adeapp.data.xml.Resource

@Composable
fun MultipleResourcesHeader(
    leaves: List<Resource>,
    hourWidth: Float,
    offset: IntOffset = IntOffset.Zero
) {
    Row(
        Modifier
            .offset { offset }
            .width((hourWidth * leaves.size).dp)
            .height(IntrinsicSize.Min)
    ) {
        leaves.forEach {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clipToBounds()
                    .wrapContentSize(unbounded = true, align = Alignment.CenterEnd)
            ) {
                TextWithMinWidth(
                    it.name,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(hourWidth.dp)
                )
            }
            if (it !== leaves.last()) {
                VerticalDivider(thickness = SECONDARY_DIVIDER_HEIGHT.dp)
            }
        }
    }
}


@Composable
fun TextWithMinWidth(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current
) {
    var textWidth by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    Box(
        Modifier
            .widthIn(min = with(density) { textWidth.toDp() })
            .then(modifier)
            .wrapContentSize(unbounded = true, align = Alignment.Center)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = fontSize,
            fontStyle = fontStyle,
            fontWeight = fontWeight,
            fontFamily = fontFamily,
            letterSpacing = letterSpacing,
            textDecoration = textDecoration,
            textAlign = textAlign,
            lineHeight = lineHeight,
            overflow = overflow,
            softWrap = softWrap,
            maxLines = maxLines,
            minLines = minLines,
            onTextLayout = { textLayoutResult: TextLayoutResult ->
                textWidth = textLayoutResult.size.width.toFloat()
            },
            style = style
        )
    }
}

