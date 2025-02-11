package com.chtibizoux.adeapp.ui.home.resourceSelector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.ResourceTree

@Composable
fun Categories(navController: NavController, resources: ResourceTree) {
    var tabIndex by remember { mutableIntStateOf(0) }
    ScrollableTabRow(selectedTabIndex = tabIndex, edgePadding = 0.dp) {
        resources.categories.forEachIndexed { index, category ->
            Tab(text = { Text(category.name?.let { stringResource(it) } ?: category.category) },
                selected = tabIndex == index,
                onClick = { tabIndex = index })
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        val category = resources.categories[tabIndex]
        category.resources.sortedBy { it.name }.forEach { resource ->
            ResourceComponent(navController, resource)
        }
    }
}
