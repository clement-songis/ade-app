package com.chtibizoux.adeapp.ui.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    content: @Composable (page: Int) -> Unit,
) {
    val pagerState =
        rememberPagerState(initialPage = getPage(initialDate), pageCount = { pageCount })

    var isRefreshing by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val onRefresh: () -> Unit = {
        isRefreshing = true
        coroutineScope.launch {
            refreshCalendar()
            isRefreshing = false
        }
    }

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
        PullToRefreshBox(
            modifier = Modifier.padding(padding),
            isRefreshing = isRefreshing,
            onRefresh = onRefresh,
        ) {
            HorizontalPager(pagerState) { page ->
                content(page)
            }
        }
    }
}
