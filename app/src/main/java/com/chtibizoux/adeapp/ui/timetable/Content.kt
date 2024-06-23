package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.xml.Calendar
import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.ui.home.SettingsButton
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale

// TODO: Make a week view

const val HOUR_HEIGHT = 60
const val MAIN_DIVIDER_HEIGHT = 2
const val SECONDARY_DIVIDER_HEIGHT = 1
const val VERTICAL_PADDING = 20

const val HEADER_HEIGHT = 20

const val TIME_WIDTH = 70

const val START_HOUR = 8
const val END_HOUR = 19

@Composable
fun WaitForCalendar(
    navController: NavController,
    calendar: Calendar?,
    children: List<Resource>,
    previousButton: Boolean = false,
    date: Date = Date(),
    refreshCalendar: suspend () -> Unit
) {
    if (calendar == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(stringResource(R.string.no_calendar), color = Color.White)
                CircularProgressIndicator(
                    modifier = Modifier.size(80.dp), strokeCap = StrokeCap.Round, strokeWidth = 8.dp
                )
            }
        }
    } else {
        TimetableContent(navController, calendar, children, previousButton, date, refreshCalendar)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TimetableContent(
    navController: NavController,
    calendar: Calendar,
    children: List<Resource>,
    previousButton: Boolean = false,
    date: Date,
    refreshCalendar: suspend () -> Unit
) {
    if (calendar.days.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.no_calendar), color = Color.White)
        }
    } else {
        val pagerState = rememberPagerState(
            initialPage = calendar.getPage(date),
            pageCount = { calendar.days.size })

        val coroutineScope = rememberCoroutineScope()
        val state = rememberPullToRefreshState()
        if (state.isRefreshing) {
            LaunchedEffect(true) {
                refreshCalendar()
                state.endRefresh()
            }
        }

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(title = {
                    TimetableTitle(calendar.days[pagerState.currentPage].getDate()) {
                        coroutineScope.launch {
                            pagerState.scrollToPage(calendar.getPage(it))
                        }
                    }
                }, navigationIcon = {
                    if (previousButton) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                stringResource(R.string.back)
                            )
                        }
                    }
                }, actions = {
                    SettingsButton(navController)
                }, colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                titleContentColor = MaterialTheme.colorScheme.primary,
                )
                )
            },
        ) { padding ->
            Box(
                Modifier.padding(padding).nestedScroll(state.nestedScrollConnection)
            ) {
                HorizontalPager(pagerState) { page ->
                    Day(navController, calendar.days[page], children) { offset ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(page + offset)
                        }
                    }
                }
                PullToRefreshContainer(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = state,
                )
            }
        }
    }
}

@Composable
fun TimetableTitle(date: Date, goTo: (Date) -> Unit) {
    var showDatePicker by remember { mutableStateOf(false) }

    Button(onClick = { showDatePicker = true }) {
        Text(
            DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(date),
            fontSize = 18.sp
        )
    }

    if (showDatePicker) {
        MyDatePickerDialog(date.time) {
            showDatePicker = false
            if (it != null) {
                goTo(Date(it))
            }
        }
    }
}

@Composable
private fun Day(
    navController: NavController,
    day: Day<Event>,
    children: List<Resource>,
    scrollTo: (offset: Int) -> Unit
) {
    if (children.isEmpty()) {
        SingleColumn(day.events, navController)
    } else {
        MultipleColumn(children, day.events, navController, scrollTo)
    }
}

@Composable
fun SingleColumn(events: List<Event>, navController: NavController) {
    Row(Modifier.verticalScroll(rememberScrollState())) {
        Hours(START_HOUR, END_HOUR, HOUR_HEIGHT)
        Box {
            Background(START_HOUR, END_HOUR, HOUR_HEIGHT)
            Box {
                events.forEach { event ->
                    EventElement(navController, event, START_HOUR, HOUR_HEIGHT)
                }
            }
        }
    }
}

@Composable
fun MultipleColumn(
    children: List<Resource>,
    events: List<Event>,
    navController: NavController,
    scrollTo: (offset: Int) -> Unit
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
        if (newOffset.x > 50) {
            scrollTo(-1)
        }
        if (newOffset.x < -width * (scale - 1) - 50) {
            scrollTo(1)
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
        Box(Modifier.padding(start = TIME_WIDTH.dp).clipToBounds()) {
            Header(leaves, hourWidth, offset.x)
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

@Composable
fun Header(leaves: List<Resource>, hourWidth: Float, offsetX: Float = 0f) {
    Row(
        modifier = Modifier.offset(x = offsetX.dp).height(HEADER_HEIGHT.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
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

@Composable
fun Hours(startHour: Int, endHour: Int, hourHeight: Int, offsetY: Float = 0f) {
    val ROW_HEIGHT = 20
    Column(
        modifier = Modifier.padding(vertical = (VERTICAL_PADDING - ROW_HEIGHT / 2f).dp)
            .offset(y = offsetY.dp).width(TIME_WIDTH.dp),
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
        Modifier.offset(y = offsetY.dp)
            .height((hourHeight * (endHour - startHour) + VERTICAL_PADDING * 2).dp),
        thickness = MAIN_DIVIDER_HEIGHT.dp
    )
}

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
            modifier = Modifier.height((hourHeight * (endHour - startHour) + VERTICAL_PADDING * 2).dp)
                .padding(horizontal = (hourWidth - 0.5).dp),
            horizontalArrangement = Arrangement.spacedBy((hourWidth - 1).dp)
        ) {
            (1..<columns).forEach { _ ->
                VerticalDivider()
            }
        }
    }
}

@Composable
fun EventElement(
    navController: NavController,
    event: Event,
    firstHour: Int,
    hourHeight: Int,
    hourWidth: Float? = null,
    size: Int = 1,
    index: Int = 0
) {
    var showDialog by remember { mutableStateOf(false) }

    val startHour = event.startHour.getHourNumber()
//    val endHour = event.endHour.getHourNumber()
//    val height = endHour - startHour
    val height = event.duration / 2f

    val yOffset = (VERTICAL_PADDING + (startHour - firstHour) * hourHeight).dp
    val mod = Modifier.height((height * hourHeight).dp)
    Surface(
        modifier = if (hourWidth == null) {
            mod.fillMaxWidth().offset(y = yOffset)
        } else {
            mod.width((hourWidth * size).dp).offset(x = (index * hourWidth).dp, y = yOffset)
        },
        color = event.getColor(),
        contentColor = Color.Black,
        shape = RoundedCornerShape(10.dp),
        onClick = { showDialog = true }
    ) {
        Column(
            modifier = Modifier.padding(vertical = 3.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                event.name,
                modifier = Modifier.height((hourHeight / 2f).dp),
                textAlign = TextAlign.Center,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            listOf("classroom", "trainee", "instructor").forEach { category ->
                val resources = event.resources.filter { it.category == category }
                if (resources.isNotEmpty()) {
                    Text(
                        resources.joinToString { it.name },
                        modifier = Modifier.height((hourHeight / 2f).dp),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1
                    )
                }
            }
        }
    }

    if (showDialog) {
        EventDialog(navController, event) { showDialog = false }
    }
}
