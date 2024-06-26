package com.chtibizoux.adeapp.ui.settings

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.chtibizoux.adeapp.R

@Composable
fun LogoutButton(onLogout: (Boolean) -> Unit) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Button(onClick = { showLogoutDialog = true }) {
        Text(stringResource(R.string.logout))
    }

    if (showLogoutDialog) {
        LogoutDialog { clearData ->
            showLogoutDialog = false
            if (clearData != null) {
                onLogout(clearData)
            }
        }
    }
}
