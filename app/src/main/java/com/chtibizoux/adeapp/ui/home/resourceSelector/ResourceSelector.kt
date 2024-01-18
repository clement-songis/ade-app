package com.chtibizoux.adeapp.ui.home.resourceSelector

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.xml.ResourceTree
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
                    context,
                    R.string.unable_to_get_resources,
                    Toast.LENGTH_LONG
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
    Column(
        modifier = Modifier
            .padding(20.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /*TODO*/
        resources.categories.forEach { category ->
            Text(
                category.name?.let { stringResource(it) } ?: category.category,
                fontWeight = FontWeight.Bold
            )
            category.resources.forEach { resource ->
                Text(resource.name)
            }
//            navController.navigate("${RootScreen.Timetable.name}/${resource.id}")
        }
    }
}