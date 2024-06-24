package com.chtibizoux.adeapp.alarms

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ActivityInfo
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import com.chtibizoux.adeapp.BuildConfig
import com.chtibizoux.adeapp.R
import com.chtibizoux.adeapp.ui.theme.ADEAppTheme
import java.text.DateFormat
import java.util.Calendar
import java.util.Locale


// TODO
class AlarmActivity : ComponentActivity() {
    companion object {
        const val FINISH_ALARM_ACTIVITY_ACTION = BuildConfig.APPLICATION_ID + ".FINISH_ALARM_ACTIVITY"
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == FINISH_ALARM_ACTIVITY_ACTION) {
                finish()
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        volumeControlStream = AudioManager.STREAM_ALARM

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                    or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                    or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        actionBar?.hide()

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    )
        } else {
            window.insetsController?.let {
                it.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                it.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        enableEdgeToEdge()

        setContent {
            ADEAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FullScreenAlarm()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(broadcastReceiver, IntentFilter(FINISH_ALARM_ACTIVITY_ACTION), RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(broadcastReceiver, IntentFilter(FINISH_ALARM_ACTIVITY_ACTION))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(broadcastReceiver)
    }
}

@Composable
fun FullScreenAlarm() {
    val context = LocalContext.current
    val currentDateTime = remember { mutableStateOf(getCurrentDateTime()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentDateTime.value = getCurrentDateTime()
            kotlinx.coroutines.delay(1000L) // Update every second
        }
    }

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column {
            Text(text = currentDateTime.value.second, style = MaterialTheme.typography.bodyLarge)
            Text(text = currentDateTime.value.first, style = MaterialTheme.typography.bodySmall)
        }

        Button(
            onClick = {
                val snoozeAlarmIntent = Intent(
                    context,
                    AlarmsReceiver::class.java
                ).apply {
                    action = SNOOZE_ALARM_ACTION
                }
                context.sendBroadcast(snoozeAlarmIntent)
            }
        ) {
            Text(stringResource(R.string.snooze))
        }

        Button(
            onClick = {
                val stopAlarmIntent = Intent(
                    context,
                    AlarmsReceiver::class.java
                ).apply {
                    action = STOP_ALARM_ACTION
                }
                context.sendBroadcast(stopAlarmIntent)
            }
        ) {
            Text(stringResource(R.string.cancel))
        }
    }

}

fun getCurrentDateTime(): Pair<String, String> {
    val calendar = Calendar.getInstance()
    val dateFormat = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault())
    val timeFormat = DateFormat.getTimeInstance(DateFormat.LONG, Locale.getDefault())
    val currentDate = dateFormat.format(calendar.time)
    val currentTime = timeFormat.format(calendar.time)
    return Pair(currentDate, currentTime)
}