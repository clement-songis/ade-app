package com.chtibizoux.adeapp.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chtibizoux.adeapp.data.dataStore
import com.chtibizoux.adeapp.ui.login.Login
import com.chtibizoux.adeapp.ui.startup.Startup
import com.chtibizoux.adeapp.ui.theme.ADEAppTheme

@Composable
fun Application(
    viewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(LocalContext.current.dataStore)
    )
) {
    ADEAppTheme {
        when (viewModel.appState) {
            AppState.CONNECTED -> Root(viewModel)
            AppState.FIRST_CONNECTION -> Startup(viewModel)
            AppState.LOADING -> Loading()
            AppState.GET_STARTING_TIMES_FAILED -> Retry(viewModel)
            AppState.DISCONNECTED -> Login(viewModel)
        }
    }
}
