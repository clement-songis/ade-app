package com.chtibizoux.adeapp.ui.home.resourceSelector

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.SettingsButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceSelector(
    navController: NavController,
    viewModel: SettingsViewModel,
    selectorViewModel: ResourceSelectorViewModel = viewModel()
) {
    val resources by selectorViewModel.resourceTree.collectAsState()
    val context = LocalContext.current
    val state = rememberPullToRefreshState()
    if (resources == null || state.isRefreshing) {
        LaunchedEffect(true) {
            val newResources = viewModel.getResources()
            if (newResources != null) {
                selectorViewModel.setResources(newResources)
            } else {
                Toast.makeText(
                    context, R.string.unable_to_get_resources, Toast.LENGTH_LONG
                ).show()
            }
            state.endRefresh()
        }
    }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.other_timetables)) },
            actions = { SettingsButton(navController) }
        )
    }) { padding ->
        Surface(
            modifier = Modifier
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(state.nestedScrollConnection),
                contentAlignment = Alignment.Center
            ) {
                if (resources == null) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 8.dp
                    )
                } else {
                    SelectorContent(navController, resources!!, selectorViewModel)
                }
                PullToRefreshContainer(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = state,
                )
            }
        }
    }
}
