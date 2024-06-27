package com.chtibizoux.adeapp.ui.timetable.week

import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Event
import com.chtibizoux.adeapp.data.xml.calendarDateFormat
import java.util.Calendar
import java.util.Date

class Weeks(days: List<Day<Event>>) : List<List<Day<Event>>> by days.groupBy({
    val calendar = Calendar.getInstance()
    calendar.setTime(it.getDate())
    calendar.get(Calendar.WEEK_OF_YEAR)
}).values.toList() {
    fun getPage(date: Date = Date()): Int {
        val index = this.indexOfFirst {
            val lastHour = it.last().events.last().endHour
            val calendar = Calendar.getInstance().apply {
                time = it.last().getDate()
                set(Calendar.HOUR_OF_DAY, lastHour.hour)
                set(Calendar.MINUTE, lastHour.minute)
            }
            calendar.time >= date
        }
        return if (index == -1) this.size - 1 else index
    }
}
