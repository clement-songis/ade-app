package com.chtibizoux.adeapp.data.ics

import com.chtibizoux.adeapp.data.xml.calendarDateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Parser {
    fun parse(ics: String, strict: Boolean = false): MyCalendar {
        val lines = ics.replace(Regex("\r?\n "), "").split(Regex("\r?\n"))
        val days = mutableListOf<Day>()
        var eventBuilder: EventBuilder? = null
        var inCalendar = false
        for ((i, line) in lines.withIndex()) {
            if (line.isEmpty()) continue
            class CalendarError(message: String) : Error("line ${i + 1}: $message")

            if (!inCalendar) {
                if (line == "BEGIN:VCALENDAR") {
                    inCalendar = true
                    continue
                } else {
                    throw CalendarError("$line is out of the calendar")
                }
            }

            val (function, data) = line.split(":", limit = 2)
            when (function) {
                "BEGIN" -> {
                    when (data) {
                        "VCALENDAR" -> throw CalendarError("Multiple calendar")
                        "VEVENT" -> {
                            if (eventBuilder != null) {
                                throw CalendarError("Event in event ??")
                            }
                            eventBuilder = EventBuilder()
                        }

                        else -> throw CalendarError("Component $data not implemented")
                    }
                }

                "END" -> {
                    when (data) {
                        "VCALENDAR" -> {
                            if (eventBuilder != null) {
                                throw CalendarError("Event not closed")
                            }
                            inCalendar = false
                        }

                        "VEVENT" -> {
                            if (eventBuilder == null) {
                                throw CalendarError("No event to close")
                            }
                            val event = eventBuilder.build()
                            val date = calendarDateFormat.format(Date(event.dtStart))
                            val index = days.indexOfFirst { it.date == date }
                            if (index == -1) {
                                val day = Day(date, listOf(event))
                                days.add(day)
                            } else {
                                days[index] = days[index].copy(events = days[index].events + event)
                            }
                            eventBuilder = null
                        }

                        else -> throw CalendarError("WTF no begin before $line ??")
                    }
                }

                "DTSTART" -> {
                    if (eventBuilder == null) throw CalendarError("Must be in event")
                    val date = toTimestamp(data) ?: throw CalendarError("Date parsing error")
                    eventBuilder.dtStart = date
                }

                "DTEND" -> {
                    if (eventBuilder == null) throw CalendarError("Must be in event")
                    val date = toTimestamp(data) ?: throw CalendarError("Date parsing error")
                    eventBuilder.dtEnd = date
                }

                "SUMMARY" -> {
                    if (eventBuilder == null) throw CalendarError("Must be in event")
                    eventBuilder.summary = data
                }

                "LOCATION" -> {
                    if (eventBuilder == null) throw CalendarError("Must be in event")
                    eventBuilder.location = data
                }

                "DESCRIPTION" -> {
                    if (eventBuilder == null) throw CalendarError("Must be in event")
                    eventBuilder.description = data.replace("\\n", "\n")
                }

                "DTSTAMP", "UID", "CREATED", "LAST-MODIFIED", "SEQUENCE" -> {
                    if (eventBuilder == null) throw CalendarError("Must be in event")
                }

                "VERSION" -> if (data != "2.0") println("WARNING Unsupported calendar version $data")
                "CALSCALE" -> if (data != "GREGORIAN") println("WARNING Unsupported calendar scale $data")
                "METHOD", "PRODID" -> {}
                else -> {
                    if (strict) {
                        throw Error("Property $function not implemented")
                    } else {
                        println("Property $function not implemented")
                    }
                }
            }
        }
        return MyCalendar(days)
    }

    private val dateTimeFormat = SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'", Locale.getDefault())

    private fun toTimestamp(date: String): Long? {
        return dateTimeFormat.parse(date)?.time
    }
}