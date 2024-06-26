package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.xml.Calendar
import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.ui.home.SettingsButton
import com.chtibizoux.adeapp.ui.timetable.day.Day
import com.chtibizoux.adeapp.ui.timetable.day.SingleColumn
import com.chtibizoux.adeapp.ui.timetable.day.Title
import kotlinx.coroutines.launch
import java.util.Date
import kotlin.math.roundToInt

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
        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
        ) {
            Text(stringResource(R.string.no_calendar), color = Color.White)
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp), strokeCap = StrokeCap.Round, strokeWidth = 8.dp
            )
        }
    } else if (calendar.days.isEmpty()) {
        Box(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center,
        ) {
            Text(stringResource(R.string.no_calendar), color = Color.White)
        }
    } else {
        TimetableContent(
            navController,
            calendar,
            children,
            previousButton,
            date,
            refreshCalendar
        )
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
    val pagerState =
        rememberPagerState(initialPage = calendar.getPage(date), pageCount = { calendar.days.size })

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
                Title(calendar.days[pagerState.currentPage].getDate()) {
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
                Day(navController, calendar.days[page], children, { offset ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page + offset)
                    }
                }) {
                    state.startRefresh()
                }
            }
            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = state,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Preview
@Composable
fun HorizontalPagerWithScrollableContent() {
    // This is a sample using NestedScroll and Pager.
    // We use the toolbar offset changing example from
    // androidx.compose.ui.samples.NestedScrollConnectionSample

    val pagerState = rememberPagerState { 10 }

    val toolbarHeight = 48.dp
    val toolbarHeightPx = with(LocalDensity.current) { toolbarHeight.roundToPx().toFloat() }
    var toolbarOffsetHeightPx by remember { mutableFloatStateOf(0f) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                val delta = available.y
                val newOffset = toolbarOffsetHeightPx + delta
                toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx, 0f)
                return Offset.Zero
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().nestedScroll(nestedScrollConnection)) {
        TopAppBar(
            modifier =
            Modifier.height(toolbarHeight).offset {
                IntOffset(x = 0, y = toolbarOffsetHeightPx.roundToInt())
            },
            title = { Text("Toolbar offset is $toolbarOffsetHeightPx") }
        )

        val paddingOffset =
            toolbarHeight + with(LocalDensity.current) { toolbarOffsetHeightPx.toDp() }

        HorizontalPager(
            modifier = Modifier.fillMaxSize(),
            state = pagerState,
            contentPadding = PaddingValues(top = paddingOffset)
        ) {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                repeat(20) {
                    Box(
                        modifier =
                        Modifier.fillMaxWidth()
                            .height(64.dp)
                            .padding(4.dp)
                            .background(if (it % 2 == 0) Color.Black else Color.Yellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = it.toString(),
                            color = if (it % 2 != 0) Color.Black else Color.Yellow
                        )
                    }
                }
            }
        }
    }
}