package com.chtibizoux.adeapp.ui.home.resourceSelector

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.data.xml.ResourceTree
import com.chtibizoux.adeapp.ui.RootScreen
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.home.SettingsButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResourceSelector(navController: NavController, viewModel: SettingsViewModel) {
    var resources: ResourceTree? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.getResources {
            if (it != null) {
                resources = it
            } else {
                Toast.makeText(
                    context, R.string.unable_to_get_resources, Toast.LENGTH_LONG
                ).show()
            }
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
                ResourceSelectorContent(navController, resources!!)
            }
        }
    }
}

@Composable
fun ResourceSelectorContent(navController: NavController, resources: ResourceTree) {
    var tabIndex by remember { mutableIntStateOf(0) }
    Column {
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
//                .padding(20.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
//            verticalArrangement = Arrangement.spacedBy(20.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val category = resources.categories[tabIndex]
            category.resources.sortedBy { it.name }.forEach { resource ->
                ResourceComponent(navController, category.category, resource)
            }
        }
    }
}

@Composable
fun ResourceComponent(navController: NavController, category: String, resource: Resource) {
    if (resource.children.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    navController.navigate("${RootScreen.Timetable.name}/${resource.id}")
                }
                .padding(10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(resource.name)
        }
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
                            navController.navigate("${RootScreen.Timetable.name}/${resource.id}?category=${category}")
                        }
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(resource.name)
                }

                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    stringResource(R.string.more),
                    modifier = Modifier
//                        .padding(start = 20.dp, top = 20.dp, bottom = 20.dp)
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50)
                        )
                        .rotate(if (opened) 180f else 0f)
                )
            }

            /*TODO*/
            if (opened) {
                Column(Modifier.padding(start = 20.dp)) {
                    resource.children.sortedBy { it.name }.forEach {
                        ResourceComponent(navController, category, it)
                    }
                }
            }
        }
    }
}