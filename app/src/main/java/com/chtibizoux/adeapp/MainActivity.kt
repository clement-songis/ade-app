package com.chtibizoux.adeapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chtibizoux.adeapp.data.dataStore
import com.chtibizoux.adeapp.ui.AppState
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.SettingsViewModelFactory
import com.chtibizoux.adeapp.ui.home.Home
import com.chtibizoux.adeapp.ui.home.timetable.TimetableTitle
import com.chtibizoux.adeapp.ui.login.Login
import com.chtibizoux.adeapp.ui.startup.Startup
import com.chtibizoux.adeapp.ui.theme.ADEAppTheme

const val ALARMS_CHANNEL_ID = "alarm_id"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val channelName = getString(R.string.title_alarms)
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                ALARMS_CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val launcher =
                    registerForActivityResult(ActivityResultContracts.RequestPermission()) {}
                launcher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

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

    data object Timetable : Screen(
        "timetable",
        R.string.title_timetable,
        Icons.Filled.CalendarMonth,
        { TimetableTitle() }
    )

    data object Alarms : Screen("alarms", R.string.title_alarms, Icons.Filled.Alarm)
}

val screens = listOf(
    Screen.Timetable,
    Screen.Alarms,
)

@Composable
fun Application(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current.dataStore)
    )
) {
    ADEAppTheme {
        when (viewModel.appState) {
            AppState.CONNECTED -> Home(viewModel)
            AppState.FIRST_CONNECTION -> Startup(viewModel)
            AppState.LOADING -> Loading()
            AppState.GET_STARTING_TIMES_FAILED -> Retry(viewModel)
            AppState.DISCONNECTED -> Login(viewModel)
        }
    }
}

@Composable
private fun Retry(viewModel: SettingsViewModel) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                Text(stringResource(R.string.get_starting_times_error))
                Button(onClick = { viewModel.retry() }) {
                    Text(stringResource(R.string.retry))
                }
            }
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                strokeCap = StrokeCap.Round,
                strokeWidth = 8.dp
            )
        }
    }
}

@Composable
private fun Loading() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                strokeCap = StrokeCap.Round,
                strokeWidth = 8.dp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    Application()
}