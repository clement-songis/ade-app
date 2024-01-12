package com.chtibizoux.adeapp.ui.home.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.SettingsViewModel
import com.chtibizoux.adeapp.ui.atLeast
import com.chtibizoux.adeapp.ui.clearFocusKeyboardAction
import com.chtibizoux.adeapp.ui.nextFocus
import com.chtibizoux.adeapp.ui.nextFocusKeyboardAction
import com.chtibizoux.adeapp.ui.submitKeyboardAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Settings(navController: NavController, viewModel: SettingsViewModel) {
    val defaultAlarm by viewModel.defaultAlarmSettings.collectAsState()
    val usePreviousAlarm by viewModel.usePreviousAlarm.collectAsState()

    var timeUntilEvent by remember { mutableStateOf(defaultAlarm.timeUntilEvent.toString()) }
    var repeat by remember { mutableStateOf(defaultAlarm.repeat.toString()) }
    var interval by remember { mutableStateOf(defaultAlarm.interval.toString()) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(colors = TopAppBarDefaults.topAppBarColors(
//                    containerColor = MaterialTheme.colorScheme.primaryContainer,
//                    titleContentColor = MaterialTheme.colorScheme.primary,
            ), title = {
                Text(stringResource(R.string.settings))
            }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Filled.ArrowBack, stringResource(R.string.back))
                }
            })
        },
    ) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                OutlinedTextField(
                    value = repeat,
                    onValueChange = {
                        repeat = it
                        viewModel.updateRepeat(it.atLeast(1))
                    },
                    modifier = Modifier
                        .nextFocus()
                        .fillMaxWidth(),
                    label = { Text(stringResource(R.string.repeat_interval)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                    keyboardActions = nextFocusKeyboardAction(),
                    singleLine = true
                )
                if (repeat.atLeast(1) > 1) {
                    OutlinedTextField(
                        value = interval,
                        onValueChange = {
                            interval = it
                            viewModel.updateInterval(it.atLeast(0))
                        },
                        modifier = Modifier
                            .nextFocus()
                            .fillMaxWidth(),
                        label = { Text(stringResource(R.string.default_interval)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
                        keyboardActions = nextFocusKeyboardAction(),
                        singleLine = true
                    )
                }
                OutlinedTextField(
                    value = timeUntilEvent,
                    onValueChange = {
                        timeUntilEvent = it
                        viewModel.updateTimeUntilEvent(it.atLeast(0))
                    },
                    modifier = Modifier
                        .nextFocus()
                        .fillMaxWidth(),
                    label = { Text(stringResource(R.string.default_alarm_interval)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = clearFocusKeyboardAction(),
                    singleLine = true
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
            }
        }
    }
}