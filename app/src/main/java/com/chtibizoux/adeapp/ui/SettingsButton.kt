package com.chtibizoux.adeapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R

@Composable
fun SettingsButton(navController: NavController) {
    IconButton(onClick = { navController.navigate(RootScreen.Settings.name) }) {
        Icon(Icons.Filled.Settings, stringResource(R.string.settings))
    }
}
