package com.chtibizoux.adeapp.ui.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
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
import androidx.navigation.navDeepLink
import com.chtibizoux.adeapp.NEW_ALARM_ACTION
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.VIEW_ALARMS_ACTION
import com.chtibizoux.adeapp.ui.RootScreen
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.home.alarms.Alarms
import com.chtibizoux.adeapp.ui.home.resourceSelector.ResourceSelector
import com.chtibizoux.adeapp.ui.home.timetable.Timetable

sealed class HomeScreen(
    val route: String, @StringRes val label: Int, val icon: ImageVector
) {
    data object Timetable :
        HomeScreen("timetable", R.string.title_timetable, Icons.Filled.CalendarMonth)

    data object Alarms : HomeScreen("alarms", R.string.title_alarms, Icons.Filled.Alarm)
    data object ResourceSelector : HomeScreen("resourceSelector", R.string.other_timetables, Icons.Filled.CalendarToday)
}

val screens = listOf(
    HomeScreen.Timetable,
    HomeScreen.Alarms,
    HomeScreen.ResourceSelector
)

fun navigate(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
fun Home(rootNavController: NavController, viewModel: SettingsViewModel) {
    val homeNavController = rememberNavController()
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val context = LocalContext.current
    LaunchedEffect(true) {
        if (viewModel.alarmPageFirst) {
            navigate(homeNavController, HomeScreen.Alarms.route)
            viewModel.alarmPageFirst = false
        }
        viewModel.initAlarms(context)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(tonalElevation = 0.dp) {
                screens.forEach { screen ->
                    NavigationBarItem(icon = {
                        Icon(screen.icon, contentDescription = stringResource(screen.label))
                    },
                        label = { Text(stringResource(screen.label)) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = { navigate(homeNavController, screen.route) })
                }
            }
        },
    ) { padding ->
        NavHost(
            homeNavController, startDestination = HomeScreen.Timetable.route, Modifier.padding(padding)
        ) {
            composable(HomeScreen.Timetable.route) { Timetable(rootNavController, viewModel) }
            composable(
                HomeScreen.Alarms.route,
                deepLinks = listOf(navDeepLink { action = VIEW_ALARMS_ACTION },
                    navDeepLink { action = NEW_ALARM_ACTION })
            ) { Alarms(rootNavController, viewModel) }
            composable(HomeScreen.ResourceSelector.route) { ResourceSelector(rootNavController, viewModel) }
        }
    }
}

@Composable
fun SettingsButton(navController: NavController) {
    IconButton(onClick = { navController.navigate(RootScreen.Settings.name) }) {
        Icon(Icons.Filled.Settings, stringResource(R.string.settings))
    }
}
