package com.chtibizoux.adeapp

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chtibizoux.adeapp.ui.LoginState
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.SettingsViewModelFactory
import com.chtibizoux.adeapp.data.dataStore
import com.chtibizoux.adeapp.ui.home.Home
import com.chtibizoux.adeapp.ui.login.Login
import com.chtibizoux.adeapp.ui.theme.ADEAppTheme
import com.chtibizoux.adeapp.ui.home.timetable.TimetableTitle
import com.chtibizoux.adeapp.ui.startup.Startup

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
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.toastMessage.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    ADEAppTheme {
        when (viewModel.loginState) {
            LoginState.CONNECTED -> Home()
            LoginState.FIRST_CONNECTION -> Startup()
            LoginState.LOADING -> Loading()
            LoginState.DISCONNECTED -> Login(viewModel)
        }
    }
}

@Composable
private fun Loading() {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(modifier = Modifier.size(80.dp), strokeCap = StrokeCap.Round, strokeWidth = 8.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    Application()
}