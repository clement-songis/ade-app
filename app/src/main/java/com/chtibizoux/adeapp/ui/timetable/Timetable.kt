package com.chtibizoux.adeapp.ui.timetable

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.xml.Calendar
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.ui.SettingsViewModel
import java.util.Date

@Composable
fun Timetable(resourceId: Int, navController: NavController, viewModel: SettingsViewModel, date: Date) {
    var resources: List<Resource> by remember { mutableStateOf(listOf()) }
    var calendar: Calendar? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    LaunchedEffect(true) {
        resources = viewModel.getChildren(resourceId) ?: run {
            Toast.makeText(
                context,
                R.string.unable_to_get_calendar,
                Toast.LENGTH_LONG
            ).show()
            navController.navigateUp()
            listOf()
        }

        calendar = viewModel.getCalendar(resourceId)
        if (calendar == null) {
            Toast.makeText(
                context,
                R.string.unable_to_get_calendar,
                Toast.LENGTH_LONG
            ).show()
            navController.navigateUp()
        }
    }
    WaitForCalendar(navController, calendar, resources, true, date) {
        calendar = viewModel.getCalendar(resourceId)
        if (calendar == null) {
            Toast.makeText(
                context,
                R.string.unable_to_get_calendar,
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
