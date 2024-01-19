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
import com.chtibizoux.adeapp.ui.SettingsViewModel

@Composable
fun Timetable(resourceId: Int, category: String?, navController: NavController, viewModel: SettingsViewModel) {
    var calendar: Calendar? by remember { mutableStateOf(null) }
    val context = LocalContext.current
    LaunchedEffect(true) {
        viewModel.getCalendar(resourceId) {
            if (it != null) {
                calendar = it
            } else {
                Toast.makeText(
                    context,
                    R.string.unable_to_get_calendar,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    WaitForCalendar(navController, calendar, category, true)
}
