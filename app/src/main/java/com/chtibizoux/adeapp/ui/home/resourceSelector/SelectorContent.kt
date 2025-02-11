package com.chtibizoux.adeapp.ui.home.resourceSelector

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.xml.ResourceTree

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectorContent(
    navController: NavController,
    resources: ResourceTree,
    selectorViewModel: ResourceSelectorViewModel
) {
    val searchText by selectorViewModel.searchText.collectAsState()
    val isSearching by selectorViewModel.isSearching.collectAsState()
    val resourceList by selectorViewModel.resourceList.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = searchText,
                    onQueryChange = selectorViewModel::onSearchTextChange,
                    onSearch = selectorViewModel::onSearchTextChange,
                    expanded = isSearching,
                    onExpandedChange = selectorViewModel::onActiveChange,
                    placeholder = { Text(stringResource(R.string.search_resource)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                )
            },
            expanded = isSearching,
            onExpandedChange = selectorViewModel::onActiveChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = if (isSearching) 0.dp else 16.dp),
        ) {
            LazyColumn {
                items(resourceList) { resource ->
                    SimpleResourceComponent(navController, resource)
                }
            }
        }
        if (!isSearching) {
            Categories(navController, resources)
        }
    }
}
