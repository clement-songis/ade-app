package com.chtibizoux.adeapp.ui.startup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.DefaultAlarmSettings
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.ui.atLeast

class StartupViewModel(startingTimes: List<String>, default: DefaultAlarmSettings) : ViewModel() {
    val alarms = mutableStateListOf<Alarm>()

    var timeUntilEvent by mutableStateOf(default.timeUntilEvent.toString())
        private set
    var repeat by mutableStateOf(default.repeat.toString())
        private set
    var interval by mutableStateOf(default.interval.toString())
        private set

    val alarmSettings
        get() = DefaultAlarmSettings(
            repeat.atLeast(1), interval.atLeast(0), timeUntilEvent.atLeast(0)
        )

    val canSubmit
        get() = run {
            val repeatInt = repeat.atLeast(1)
            val alarmIntervalInt = timeUntilEvent.atLeast(0)
            val intervalInt = interval.atLeast(0)
            repeatInt == repeat.toIntOrNull() && alarmIntervalInt == timeUntilEvent.toIntOrNull() && intervalInt == interval.toIntOrNull()
        }

    fun updateTimeUntilEvent(text: String) {
        timeUntilEvent = text
        updateAlarms()
    }

    fun updateRepeat(text: String) {
        repeat = text
        updateAlarms()
    }

    fun updateInterval(text: String) {
        interval = text
        updateAlarms()
    }

    private fun updateAlarms() {
        val repeatInt = repeat.atLeast(1)
        val timeUntilEventInt = timeUntilEvent.atLeast(0)
        val intervalInt = interval.atLeast(0)
        alarms.replaceAll { alarm ->
            Alarm(alarm.forHour, (0..<repeatInt).map {
                Time(alarm.forHour.getMinutesNumber() - timeUntilEventInt + it * intervalInt)
            })
        }
    }

    init {
        alarms.addAll(startingTimes.map { startingTime ->
            val time = Time.fromString(startingTime)!!
            Alarm(time, (0..<default.repeat).map {
                Time(time.getMinutesNumber() - default.timeUntilEvent + it * default.interval)
            })
        })
    }
}

class StartupViewModelFactory(
    private val startingTimes: List<String>, private val default: DefaultAlarmSettings
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartupViewModel::class.java)) {
            return StartupViewModel(startingTimes, default) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}