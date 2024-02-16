package com.chtibizoux.adeapp.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.chtibizoux.adeapp.ui.home.Home
import com.chtibizoux.adeapp.ui.settings.Settings
import com.chtibizoux.adeapp.ui.timetable.Timetable

enum class RootScreen { Home, Timetable, Settings }

@Composable
fun Root(viewModel: SettingsViewModel) {
    // TODO: change icon
    val navController = rememberNavController()
    NavHost(
        navController, startDestination = RootScreen.Home.name
    ) {
        composable(RootScreen.Home.name) { Home(navController, viewModel) }
        composable(
            RootScreen.Timetable.name + "/{resourceId}",
            arguments = listOf(
                navArgument("resourceId") { type = NavType.IntType }
            )
        ) {
            val resourceId = it.arguments?.getInt("resourceId") ?: 0
            if (resourceId == 0) {
                navController.navigateUp()
            } else {
                Timetable(resourceId, navController, viewModel)
            }
        }
        composable(RootScreen.Settings.name) { Settings(navController, viewModel) }
    }
}