package com.chtibizoux.adeapp.ui.timetable.week

import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Event
import java.util.Calendar
import java.util.Date

class Weeks(days: List<Day<Event>>) : List<List<Day<Event>>> by days.groupBy({
    val calendar = Calendar.getInstance()
    calendar.setTime(it.getDate())
    calendar.get(Calendar.WEEK_OF_YEAR) + calendar.weekYear * calendar.weeksInWeekYear
}).values.toList() {
    fun getPage(date: Date = Date()): Int {
        val index = this.indexOfFirst { days ->
            val lastHour = days.last().events.sortedWith(
                compareBy({ it.startHour.getMinutesNumber() },
                    { it.endHour.getMinutesNumber() })
            ).last().endHour
            val calendar = Calendar.getInstance().apply {
                time = days.last().getDate()
                set(Calendar.HOUR_OF_DAY, lastHour.hour)
                set(Calendar.MINUTE, lastHour.minute)
            }
            calendar.time >= date
        }
        return if (index == -1) this.size - 1 else index
    }
}
