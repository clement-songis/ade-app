package com.chtibizoux.adeapp.ui.home.resourceSelector

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.ui.RootScreen

@Composable
fun SimpleResourceComponent(navController: NavController, resource: Resource) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("${RootScreen.Timetable.name}/${resource.id}")
            }
            .padding(10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text("${resource.name} (${resource.id})")
    }
}
