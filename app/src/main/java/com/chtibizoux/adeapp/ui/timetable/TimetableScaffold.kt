package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewDay
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.BackButton
import com.chtibizoux.adeapp.ui.SettingsButton
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun TimetableScaffold(
    isWeekView: Boolean,
    initialDate: Date,
    pageCount: Int,
    getPage: (date: Date) -> Int,
    getCurrentDate: (currentPage: Int) -> Date,
    toggleView: (currentPage: Int) -> Unit,
    navController: NavController,
    previousButton: Boolean,
    refreshCalendar: suspend () -> Unit,
    content: @Composable (page: Int, xScrollEnabled: MutableState<Boolean>, yScrollEnabled: MutableState<Boolean>) -> Unit,
) {
    val pagerState = rememberPagerState(initialPage = getPage(initialDate), pageCount = { pageCount })

    val coroutineScope = rememberCoroutineScope()

    val state = rememberPullToRefreshState()
    if (state.isRefreshing) {
        LaunchedEffect(true) {
            refreshCalendar()
            state.endRefresh()
        }
    }

    val xScrollEnabled = remember { mutableStateOf(true) }
    val yScrollEnabled = remember { mutableStateOf(true) }

    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = {
                TimetableTitle(getCurrentDate(pagerState.currentPage), isWeekView) {
                    coroutineScope.launch {
                        pagerState.scrollToPage(getPage(it))
                    }
                }
            },
            navigationIcon = {
                if (previousButton) {
                    BackButton(navController)
                }
            },
            actions = {
                IconButton(onClick = { toggleView(pagerState.currentPage) }) {
                    Icon(
                        if (isWeekView) Icons.Filled.CalendarViewDay else Icons.Filled.CalendarViewWeek,
                        stringResource(R.string.change_timetable_view)
                    )
                }
                SettingsButton(navController)
            }
        )
    }) { padding ->
        val modifier = Modifier.padding(padding)
        Box(if (yScrollEnabled.value) modifier.nestedScroll(state.nestedScrollConnection) else modifier) {
            HorizontalPager(pagerState, userScrollEnabled = xScrollEnabled.value) { page ->
                content(page, xScrollEnabled, yScrollEnabled)
            }
            PullToRefreshContainer(
                modifier = Modifier.align(Alignment.TopCenter),
                state = state,
            )
        }
    }
}
