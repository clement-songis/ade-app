package com.chtibizoux.adeapp.ui.home

import android.widget.Toast
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chtibizoux.adeapp.AlarmReceiver
import com.chtibizoux.adeapp.BootReceiver
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.Screen
import com.chtibizoux.adeapp.screens
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.home.alarms.Alarms
import com.chtibizoux.adeapp.ui.home.timetable.Timetable

fun navigate(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Home(viewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val context = LocalContext.current
    LaunchedEffect(true) {
        if (viewModel.alarmPageFirst) {
            navigate(navController, Screen.Alarms.route)
            viewModel.alarmPageFirst = false
        }
        if (viewModel.updateCalendar) {
            viewModel.tryUpdateCalendar {
                Toast.makeText(context, R.string.unable_to_update_calendar, Toast.LENGTH_LONG).show()
            }
            viewModel.updateCalendar = false
        }
        viewModel.initAlarms(context)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    screens.find { it.route == currentDestination?.route }?.title?.invoke() ?: Text(
                        stringResource(R.string.app_name)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(tonalElevation = 0.dp) {
                screens.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(screen.icon, contentDescription = stringResource(screen.label))
                        },
                        label = { Text(stringResource(screen.label)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { navigate(navController, screen.route) })
                }
            }
        },
    ) { padding ->
        NavHost(
            navController, startDestination = Screen.Timetable.route, Modifier.padding(padding)
        ) {
            composable(Screen.Timetable.route) { Timetable(viewModel) }
            composable(Screen.Alarms.route) { Alarms(viewModel) }
        }
    }
}