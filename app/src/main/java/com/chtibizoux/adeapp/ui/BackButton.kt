package com.chtibizoux.adeapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R

@Composable
fun BackButton(navController: NavController) {
    IconButton(onClick = { navController.navigateUp() }) {
        Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(R.string.back))
    }
}