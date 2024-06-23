package com.chtibizoux.adeapp.ui.home.resourceSelector

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.data.xml.ResourceTree
import com.chtibizoux.adeapp.ui.RootScreen
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.home.SettingsButton

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
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
//                containerColor = MaterialTheme.colorScheme.primaryContainer,
//                titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(stringResource(R.string.other_timetables))
            }, actions = {
                SettingsButton(navController)
            })
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            if (resources == null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterVertically)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(80.dp),
                        strokeCap = StrokeCap.Round,
                        strokeWidth = 8.dp
                    )
                }
            } else {
                Box(Modifier.nestedScroll(state.nestedScrollConnection)) {
                    ResourceSelectorContent(navController, resources!!, selectorViewModel)
                    PullToRefreshContainer(
                        modifier = Modifier.align(Alignment.TopCenter),
                        state = state,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceSelectorContent(
    navController: NavController,
    resources: ResourceTree,
    selectorViewModel: ResourceSelectorViewModel
) {
    val searchText by selectorViewModel.searchText.collectAsState()
    val isSearching by selectorViewModel.isSearching.collectAsState()
    val resourceList by selectorViewModel.resourceList.collectAsState()

    Column {
        SearchBar(
            query = searchText,
            onQueryChange = selectorViewModel::onSearchTextChange,
            placeholder = {
                Text(stringResource(R.string.search_resource))
            },
            onSearch = selectorViewModel::onSearchTextChange,
            active = isSearching,
            onActiveChange = selectorViewModel::onActiveChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isSearching) 0.dp else 16.dp)
        ) {
            LazyColumn {
                items(resourceList) { resource ->
                    SimpleResourceComponent(navController, resource)
                }
            }
        }
        if (!isSearching) {
            ResourceCategories(navController, resources)
        }
    }
}

@Composable
fun ResourceCategories(navController: NavController, resources: ResourceTree) {
    var tabIndex by remember { mutableIntStateOf(0) }
    ScrollableTabRow(
        selectedTabIndex = tabIndex, edgePadding = 0.dp
    ) {
        resources.categories.forEachIndexed { index, category ->
            Tab(text = { Text(category.name?.let { stringResource(it) } ?: category.category) },
                selected = tabIndex == index,
                onClick = { tabIndex = index })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        val category = resources.categories[tabIndex]
        category.resources.sortedBy { it.name }.forEach { resource ->
            ResourceComponent(navController, resource)
        }
    }
}

@Composable
fun ResourceComponent(navController: NavController, resource: Resource) {
    if (resource.children.isEmpty()) {
        SimpleResourceComponent(navController, resource)
    } else {
        var opened by remember { mutableStateOf(false) }
        Column(modifier = Modifier.clickable { opened = !opened }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clickable {
                            navController.navigate("${RootScreen.Timetable.name}/${resource.id}")
                        }
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("${resource.name} (${resource.id})")
                }

                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    stringResource(R.string.more),
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50)
                        )
                        .rotate(if (opened) 180f else 0f)
                )
            }

            if (opened) {
                Column(Modifier.padding(start = 20.dp)) {
                    resource.children.sortedBy { it.name }.forEach {
                        ResourceComponent(navController, it)
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleResourceComponent(navController: NavController, resource: Resource) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("${RootScreen.Timetable.name}/${resource.id}")
            }
            .padding(10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text("${resource.name} (${resource.id})")
    }
}
