package com.chtibizoux.adeapp.ui.home.alarms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.BuildConfig
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.ui.SettingsButton
import com.chtibizoux.adeapp.ui.SettingsViewModel
import java.util.Calendar

const val VIEW_ALARMS_ACTION = BuildConfig.APPLICATION_ID + ".VIEW_ALARMS"
const val NEW_ALARM_ACTION = BuildConfig.APPLICATION_ID + ".NEW_ALARM"

const val TIME_EXTRA = "time"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Alarms(navController: NavController, viewModel: SettingsViewModel) {
    val alarms by viewModel.alarms.collectAsState()
    val alarmSettings by viewModel.defaultAlarmSettings.collectAsState()
    var selected by remember { mutableIntStateOf(-1) }
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.title_alarms)) },
            actions = { SettingsButton(navController) }
        )
    }) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp, 24.dp, 24.dp, 135.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                alarms.forEachIndexed { i, alarm ->
                    AlarmComponent(alarm, i, selected, viewModel, alarmSettings) {
                        selected = if (selected == i) -1 else i
                    }
                }
            }
            AddAlarmButton(
                if (alarms.isEmpty()) {
                    Time(Calendar.getInstance().get(Calendar.HOUR_OF_DAY), 0)
                } else {
                    alarms.last().forHour + 60
                },
                alarmSettings
            ) {
                viewModel.addAlarm(it)
            }
        }
    }
}