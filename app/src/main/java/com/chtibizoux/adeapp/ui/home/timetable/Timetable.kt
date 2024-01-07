package com.chtibizoux.adeapp.ui.home.timetable

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TimetableTitle() {
    var date by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var showDatePicker by remember { mutableStateOf(false) }

    Button(onClick = { showDatePicker = true }) {
        Text(DateFormat.getDateInstance(DateFormat.FULL, Locale.getDefault()).format(Date(date)))
    }

    if (showDatePicker) {
        MyDatePickerDialog(date) {
            showDatePicker = false
            if (it != null) {
                date = it
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyDatePickerDialog(initialDate: Long, onDateSelected: (Long?) -> Unit) {
    val datePickerState = rememberDatePickerState(initialDate)

    DatePickerDialog(onDismissRequest = { onDateSelected(null) }, confirmButton = {
        TextButton(onClick = {
            onDateSelected(datePickerState.selectedDateMillis)
        }) {
            Text("Ok")
        }
    }) {
        DatePicker(datePickerState)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Timetable(navController: NavHostController) {
    val pagerState = rememberPagerState(pageCount = { 10 })
    HorizontalPager(pagerState) { page ->
//            days[page]
        Day()
    }
}

@Composable
private fun Day() {
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Background(8, 19)
//    Column {
//        events.forEach { event ->
//            Event(event)
//        }
//    }
    }
}

//data class Hour(var hour: Int, var minutes: Int) {
//    constructor(minutes: Int) : this(minutes / 60, minutes % 60)
//
//    var minutesNumber
//        get() = hour * 60 + minutes
//        set(nb) {
//            hour = nb / 60
//            minutes = nb % 60
//        }
//
//    override fun toString(): String =
//        "${hour.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
//}

@Composable
fun Background(startHour: Int, endHour: Int) {
    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceAround) {
        (startHour..endHour * 2).forEachIndexed { _, nb ->
            if (nb % 2 == 0) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${(nb / 2).toString().padStart(2, '0')}:00",
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 15.dp, end = 10.dp)
                    )
                    Divider(thickness = 1.dp)
                }
            } else {
                Divider(Modifier.padding(start = 90.dp, end = 20.dp), thickness = 1.dp)
            }
        }
    }
    Box {
        Divider(
            modifier = Modifier
                .offset(x = 70.dp)
                .fillMaxHeight()
                .width(1.dp)
        )
    }
}

//@Composable
//fun Event(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        fontSize = 100.sp,
//        modifier = modifier
//    )
//}
