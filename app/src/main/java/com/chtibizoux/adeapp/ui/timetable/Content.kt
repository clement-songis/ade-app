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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
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
    navController: NavController, calendar: Calendar?, previousButton: Boolean = false
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
                    modifier = Modifier.size(80.dp),
                    strokeCap = StrokeCap.Round,
                    strokeWidth = 8.dp
                )
            }
        }
    } else {
        TimetableContent(navController, calendar, previousButton)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TimetableContent(navController: NavController, calendar: Calendar, previousButton: Boolean) {
    val pagerState =
        rememberPagerState(initialPage = calendar.getPage(), pageCount = { calendar.days.size })
    val coroutineScope = rememberCoroutineScope()
//    val refreshScope = rememberCoroutineScope()
//    var refreshing by remember { mutableStateOf(false) }
//    val context = LocalContext.current
//    fun refresh() = refreshScope.launch {
//        refreshing = true
//        val success = viewModel.refreshCalendar()
//        if (!success) {
//            Toast.makeText(context, "Unable to update calendar from ADE", Toast.LENGTH_LONG).show()
//        }
//        refreshing = false
//    }
//    val state = rememberPullRefreshState(refreshing, ::refresh)
//    Box(Modifier.pullRefresh(state)) {
//        /*Content*/
//        PullRefreshIndicator(refreshing, state, Modifier.align(Alignment.TopCenter))
//    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    TimetableTitle(calendar.days[pagerState.currentPage].getDate()) {
                        coroutineScope.launch {
                            pagerState.scrollToPage(calendar.getPage(it))
                        }
                    }
                }, navigationIcon = {
                    if (previousButton) {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    }
                },
                actions = {
                    SettingsButton(navController)
                },
                colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                titleContentColor = MaterialTheme.colorScheme.primary,
                )
            )
        },
    ) { padding ->
        HorizontalPager(pagerState, Modifier.padding(padding)) { page ->
            Day(navController, calendar.days[page])
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
private fun Day(navController: NavController, day: Day<Event>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background),
    ) {
        Background(START_HOUR, END_HOUR)
        day.events.forEach { event ->
            EventComponent(navController, event)
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
                    Divider(thickness = 1.dp)
                }
            } else {
                Divider(
                    Modifier.padding(start = (TIME_PADDING + 20).dp, end = 20.dp),
                    thickness = SECONDARY_DIVIDER_HEIGHT.dp
                )
            }
        }
    }
    Divider(
        modifier = Modifier
            .offset(x = TIME_PADDING.dp)
            .height((BOX_HEIGHT * (endHour - startHour) + MAIN_DIVIDER_HEIGHT + PADDING * 2).dp)
            .width(1.dp)
    )
}

@Composable
fun EventComponent(navController: NavController, event: Event) {
    var showDialog by remember { mutableStateOf(false) }
    val startHour = event.startHour.getHourNumber()
//    val endHour = event.endHour.getHourNumber()
//    val height = endHour - startHour
    val height = event.duration / 2f
    val (r, g, b) = event.color.split(",")
    Surface(modifier = Modifier
        .offset(y = (PADDING + MAIN_DIVIDER_HEIGHT / 2 + (startHour - START_HOUR) * BOX_HEIGHT + 1).dp)
        .padding(start = TIME_PADDING.dp)
        .fillMaxWidth()
        .height((height * BOX_HEIGHT - 1).dp),
        color = Color(r.toInt(), g.toInt(), b.toInt()),
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
