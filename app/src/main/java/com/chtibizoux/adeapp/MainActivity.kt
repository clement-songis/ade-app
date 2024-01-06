package com.chtibizoux.adeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chtibizoux.adeapp.ui.alarms.Alarms
import com.chtibizoux.adeapp.ui.theme.ADEAppTheme
import com.chtibizoux.adeapp.ui.timetable.Timetable
import com.chtibizoux.adeapp.ui.timetable.TimetableTitle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Application()
        }
    }
}

sealed class Screen(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector,
    val title: (@Composable () -> Unit)?
) {
    constructor(route: String, title: Int, icon: ImageVector) : this(route, title, icon, {
        Text(stringResource(title))
    })

    data object Timetable :
        Screen(
            "timetable",
            R.string.title_timetable,
            Icons.Filled.CalendarMonth,
            { TimetableTitle() })

    data object Alarms : Screen("alarms", R.string.title_alarms, Icons.Filled.Alarm)
}

val screens = listOf(
    Screen.Timetable,
    Screen.Alarms,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Application() {
    ADEAppTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(colors = topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ), title = {
                    screens.find { it.route == currentDestination?.route }?.title?.invoke()
                        ?: Text(stringResource(R.string.app_name))
                })
            },
            bottomBar = {
                NavigationBar {
                    screens.forEach { screen ->
                        NavigationBarItem(icon = {
                            Icon(
                                screen.icon, contentDescription = stringResource(screen.label)
                            )
                        },
                            label = { Text(stringResource(screen.label)) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            })
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController, startDestination = Screen.Timetable.route, Modifier.padding(padding)
            ) {
                composable(Screen.Timetable.route) { Timetable(navController) }
                composable(Screen.Alarms.route) { Alarms(navController) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    Application()
}