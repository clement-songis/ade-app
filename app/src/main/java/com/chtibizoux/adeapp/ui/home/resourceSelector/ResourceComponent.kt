package com.chtibizoux.adeapp.ui.home.resourceSelector

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.ui.RootScreen

@Composable
fun ResourceComponent(navController: NavController, resource: Resource) {
    if (resource.children.isEmpty()) {
        SimpleResourceComponent(navController, resource)
    } else {
        var opened by remember { mutableStateOf(false) }
        Column(modifier = Modifier.clickable { opened = !opened }) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clickable {
                            navController.navigate("${RootScreen.Timetable.name}/${resource.id}")
                        }
                        .padding(10.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text("${resource.name} (${resource.id})")
                }

                Icon(
                    Icons.Filled.KeyboardArrowDown,
                    stringResource(R.string.more),
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(50)
                        )
                        .rotate(if (opened) 180f else 0f)
                )
            }

            if (opened) {
                Column(Modifier.padding(start = 20.dp)) {
                    resource.children.sortedBy { it.name }.forEach {
                        ResourceComponent(navController, it)
                    }
                }
            }
        }
    }
}
