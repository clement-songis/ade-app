package com.chtibizoux.adeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chtibizoux.adeapp.ui.theme.ADEAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ADEAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {
                    Background(Hour(8, 0), Hour(18, 0), 30)
                    Greeting("noob")
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!", fontSize = 100.sp, modifier = modifier
    )
}

//@Composable
//fun Day(events: List<Event>, modifier: Modifier = Modifier) {
//    Column {
//        events.forEach { event ->
//            Event(event)
//        }
//    }
//}

data class Hour(var hour: Int, var minutes: Int) {
    constructor(minutes: Int) : this(minutes / 60, minutes % 60)

    var minutesNumber
        get() = hour * 60 + minutes
        set(nb) {
            hour = nb / 60
            minutes = nb % 60
        }

    override fun toString(): String =
        "${hour.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}"
}

@Composable
fun Background(startHour: Hour, endHour: Hour, spacing: Int) {
    Column/*(modifier = Spacing(16.dp), mainAxisSize = LayoutSize.Expand)*/ {
        (startHour.minutesNumber..endHour.minutesNumber step spacing).forEachIndexed { nb, _ ->
            Row {
                Text(
                    text = Hour(nb).toString(),
                    fontSize = 22.sp,
                )
                Divider(
                    color = Color.DarkGray,
                    thickness = 2.dp,
                )
            }
//            Divider(
//                color = Color.Blue, thickness = 2.dp,
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .width(1.dp)
//            )
        }
    }
//    Divider(color = Color.DarkGray, thickness = 1.dp)
//    startHour.hour
}

//@Composable
//fun Event(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        fontSize = 100.sp,
//        modifier = modifier
//    )
//}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ADEAppTheme {
        Greeting("Android")
    }
}