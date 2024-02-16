package com.chtibizoux.adeapp.ui.home.timetable

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.timetable.WaitForCalendar

@Composable
fun UserTimetable(navController: NavController, viewModel: SettingsViewModel) {
    val calendar by viewModel.userCalendar.collectAsState()
    val context = LocalContext.current
    // TODO: Maybe get resource children ?
    LaunchedEffect(true) {
        viewModel.tryUpdateCalendar {
            Toast.makeText(
                context,
                if (it) R.string.calendar_updated else R.string.unable_to_update_calendar,
                Toast.LENGTH_LONG
            ).show()
        }
    }
    WaitForCalendar(navController, calendar, listOf(), "")
}
