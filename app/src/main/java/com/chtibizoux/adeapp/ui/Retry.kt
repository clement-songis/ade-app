package com.chtibizoux.adeapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.chtibizoux.adeapp.R


@Composable
fun Retry(viewModel: SettingsViewModel) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center) {
            Column {
                Text(stringResource(R.string.get_starting_times_error))
                Button(onClick = { viewModel.retry() }) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}
