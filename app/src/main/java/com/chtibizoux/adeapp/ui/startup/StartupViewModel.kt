package com.chtibizoux.adeapp.ui.startup

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.chtibizoux.adeapp.data.Alarm
import com.chtibizoux.adeapp.data.Time
import com.chtibizoux.adeapp.ui.atLeast

class StartupViewModel(startingTimes: List<String>) : ViewModel() {
    val alarms = mutableStateListOf<Alarm>()

    var defaultAlarmInterval by mutableStateOf("60")
        private set
    var defaultAlarmRepeat by mutableStateOf("1")
        private set
    var defaultInterval by mutableStateOf("1")
        private set

    val canSubmit
        get() = run {
            val alarmRepeat = defaultAlarmRepeat.atLeast(1)
            val alarmInterval = defaultAlarmInterval.atLeast(0)
            val interval = defaultInterval.atLeast(0)
            alarmRepeat == defaultAlarmRepeat.toIntOrNull() && alarmInterval == defaultAlarmInterval.toIntOrNull() && interval == defaultInterval.toIntOrNull()
        }

    fun setAlarmInterval(interval: String) {
        defaultAlarmInterval = interval
        updateAlarms()
    }

    fun setAlarmRepeat(repeat: String) {
        defaultAlarmRepeat = repeat
        updateAlarms()
    }

    fun setInterval(interval: String) {
        defaultInterval = interval
        updateAlarms()
    }

    private fun updateAlarms() {
        val alarmRepeat = defaultAlarmRepeat.atLeast(1)
        val alarmInterval = defaultAlarmInterval.atLeast(0)
        val interval = defaultInterval.atLeast(0)
        alarms.replaceAll { alarm ->
            Alarm(alarm.forHour, (0..<alarmRepeat).map {
                val time = Time.fromString(alarm.forHour)!!
                Time(time.getMinutesNumber() - alarmInterval + it * interval)
            })
        }
    }

    init {
        val alarmRepeat = defaultAlarmRepeat.atLeast(1)
        val alarmInterval = defaultAlarmInterval.atLeast(0)
        val interval = defaultInterval.atLeast(0)
        alarms.addAll(startingTimes.map { startingTime ->
            Alarm(startingTime, (0..<alarmRepeat).map {
                val time = Time.fromString(startingTime)!!
                Time(time.getMinutesNumber() - alarmInterval + it * interval)
            })
        })
    }
}

class StartupViewModelFactory(private val startingTimes: List<String>) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StartupViewModel::class.java)) {
            return StartupViewModel(startingTimes) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}