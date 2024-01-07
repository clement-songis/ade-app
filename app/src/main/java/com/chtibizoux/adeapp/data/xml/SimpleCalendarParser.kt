package com.chtibizoux.adeapp.data.xml

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class SimpleCalendarParser {
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): SimpleCalendar {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readCalendar(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCalendar(parser: XmlPullParser): SimpleCalendar {
        val days = mutableListOf<Day<SimpleEvent>>()
        parser.require(XmlPullParser.START_TAG, ns, "events")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "event") {
                val event = readEvent(parser)
                val index = days.indexOfFirst { it.date == event.date }
                if (index == -1) {
                    days.add(Day(event.date, listOf(event)))
                } else {
                    days[index] = days[index].copy(events = days[index].events + event)
                }
            } else {
                skip(parser)
            }
        }
        return SimpleCalendar(days)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEvent(parser: XmlPullParser): SimpleEvent {
        parser.require(XmlPullParser.START_TAG, ns, "event")
        val name = parser.getAttributeValue(ns, "name")
        val date = parser.getAttributeValue(ns, "date")
        val startHour = parser.getAttributeValue(ns, "startHour")
        val endHour = parser.getAttributeValue(ns, "endHour")
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, ns, "event")
        return SimpleEvent(name, date, startHour, endHour)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}