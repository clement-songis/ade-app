package com.chtibizoux.adeapp.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.BackButton
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.atLeast
import com.chtibizoux.adeapp.ui.clearFocusKeyboardAction
import com.chtibizoux.adeapp.ui.nextFocus
import com.chtibizoux.adeapp.ui.nextFocusKeyboardAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController, viewModel: SettingsViewModel) {
    Scaffold(topBar = {
        CenterAlignedTopAppBar(
            title = { Text(stringResource(R.string.settings)) },
            navigationIcon = { BackButton(navController) }
        )
    }) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val defaultAlarm by viewModel.defaultAlarmSettings.collectAsState()
                val usePreviousAlarm by viewModel.usePreviousAlarm.collectAsState()

                var timeUntilEvent by remember { mutableStateOf(defaultAlarm.timeUntilEvent.toString()) }
                var repeat by remember { mutableStateOf(defaultAlarm.repeat.toString()) }
                var interval by remember { mutableStateOf(defaultAlarm.interval.toString()) }

                AlarmsSettings(
                    repeat = FieldValueManager(
                        repeat,
                        update = {
                            repeat = it
                            viewModel.updateRepeat(it.atLeast(1))
                        },
                        repeat != defaultAlarm.repeat.toString()
                    ),
                    interval = FieldValueManager(
                        interval,
                        update = {
                            interval = it
                            viewModel.updateInterval(it.atLeast(0))
                        },
                        interval != defaultAlarm.interval.toString()
                    ),
                    timeUntilEvent = FieldValueManager(
                        timeUntilEvent,
                        update = {
                            timeUntilEvent = it
                            viewModel.updateTimeUntilEvent(it.atLeast(0))
                        },
                        timeUntilEvent != defaultAlarm.timeUntilEvent.toString()
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),/*Arrangement.SpaceBetween*/
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(stringResource(R.string.use_previous_alarm), Modifier.weight(1f))
                    Switch(
                        checked = usePreviousAlarm,
                        onCheckedChange = viewModel::setUsePreviousAlarm
                    )
                }

                LogoutButton { viewModel.logout(it) }
            }
        }
    }
}
