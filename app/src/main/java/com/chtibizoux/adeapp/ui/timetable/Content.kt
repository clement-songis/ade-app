package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
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

const val SPACE = 20
const val MAIN_DIVIDER_HEIGHT = 20
const val SECONDARY_DIVIDER_HEIGHT = 1
const val PADDING = 10
const val TIME_PADDING = 70
const val START_HOUR = 8
const val END_HOUR = 19
const val BOX_HEIGHT = (MAIN_DIVIDER_HEIGHT + SECONDARY_DIVIDER_HEIGHT + SPACE * 2)

@Composable
fun WaitForCalendar(
    navController: NavController,
    calendar: Calendar?,
    children: List<Resource>,
    previousButton: Boolean = false,
    refreshCalendar: suspend () -> Unit
) {
    if (calendar == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
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
        TimetableContent(navController, calendar, children, previousButton, refreshCalendar)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TimetableContent(
    navController: NavController,
    calendar: Calendar,
    children: List<Resource>,
    previousButton: Boolean = false,
    refreshCalendar: suspend () -> Unit
) {
    if (calendar.days.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.no_calendar), color = Color.White)
        }
    } else {
        val pagerState =
            rememberPagerState(initialPage = calendar.getPage(), pageCount = { calendar.days.size })
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
                Modifier
                    .padding(padding)
                    .nestedScroll(state.nestedScrollConnection)
            ) {
                HorizontalPager(pagerState) { page ->
                    Day(navController, calendar.days[page], children)
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
    navController: NavController, day: Day<Event>, children: List<Resource>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
    ) {
        Background(START_HOUR, END_HOUR)
        if (children.isEmpty()) {
            SingleColumn(day.events, navController)
        } else {
            MultipleColumn(children, day.events, navController)
        }
    }
}

@Composable
fun Background(startHour: Int, endHour: Int) {
    Column(
        modifier = Modifier.padding(vertical = PADDING.dp),
        verticalArrangement = Arrangement.spacedBy(SPACE.dp)
    ) {
        (startHour * 2..endHour * 2).forEachIndexed { _, nb ->
            if (nb % 2 == 0) {
                Row(
                    Modifier.height(MAIN_DIVIDER_HEIGHT.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${(nb / 2).toString().padStart(2, '0')}:00",
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 15.dp, end = 10.dp)
                    )
                    HorizontalDivider(thickness = 1.dp)
                }
            } else {
                HorizontalDivider(
                    Modifier.padding(start = (TIME_PADDING + 20).dp, end = 20.dp),
                    thickness = SECONDARY_DIVIDER_HEIGHT.dp
                )
            }
        }
    }
    VerticalDivider(
        modifier = Modifier
            .offset(x = TIME_PADDING.dp)
//            .height((BOX_HEIGHT * (endHour - startHour) + MAIN_DIVIDER_HEIGHT + PADDING * 2).dp)
//            .width(1.dp)
    )
}

@Composable
fun SingleColumn(events: List<Event>, navController: NavController) {
    Box(Modifier.padding(start = TIME_PADDING.dp)) {
        events.forEach { event ->
            EventElement(navController, event)
        }
    }
}

@Composable
fun MultipleColumn(children: List<Resource>, events: List<Event>, navController: NavController) {
    val leaves = getLeaves(children)
    val allChildren = getAllChildren(children)
    Column(Modifier.padding(start = TIME_PADDING.dp)) {
        Row {
            leaves.forEach {
                Text(it.name)
            }
        }
        Box {
            events.forEach { event ->
                val resource =
                    allChildren.find { resource -> event.resources.find { resource.name == it.name && resource.id == it.id } != null }

                if (resource != null) {
                    val (index, length) = if (resource.children.isEmpty()) {
                        Pair(leaves.indexOf(resource), 1)
                    } else {
                        val eventResourceLeaves = getLeaves(resource.children)
                        Pair(leaves.indexOf(eventResourceLeaves.first()), eventResourceLeaves.size)
                    }
                    println("${event.name} $index $length")
                    EventElement(navController, event)
                } else {
                    EventElement(navController, event)
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
fun EventElement(navController: NavController, event: Event) {
    var showDialog by remember { mutableStateOf(false) }

    val startHour = event.startHour.getHourNumber()
//    val endHour = event.endHour.getHourNumber()
//    val height = endHour - startHour
    val height = event.duration / 2f

    Surface(modifier = Modifier
        .fillMaxWidth()
        .offset(y = (PADDING + MAIN_DIVIDER_HEIGHT / 2 + (startHour - START_HOUR) * BOX_HEIGHT + 1).dp)
        .height((height * BOX_HEIGHT - 1).dp),
        color = event.getColor(),
        contentColor = Color.Black,
        shape = RoundedCornerShape(10.dp),
        onClick = { showDialog = true }) {
        Column(
            modifier = Modifier.padding(vertical = 3.dp, horizontal = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                event.name,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(event.resources.filter { it.category == "classroom" }.joinToString { it.name },
                fontSize = 13.sp,
                textAlign = TextAlign.Center
            )
        }
    }

    if (showDialog) {
        EventDialog(navController, event) { showDialog = false }
    }
}
