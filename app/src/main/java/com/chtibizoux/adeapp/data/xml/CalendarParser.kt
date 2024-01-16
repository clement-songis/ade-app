package com.chtibizoux.adeapp.data.xml

import android.util.Xml
import com.chtibizoux.adeapp.data.Time
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

class CalendarParser {
    private val ns: String? = null

    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): Calendar {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readCalendar(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readCalendar(parser: XmlPullParser): Calendar {
        val days = mutableListOf<Day<Event>>()
        parser.require(XmlPullParser.START_TAG, ns, "events")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            // Starts by looking for the entry tag.
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
        return Calendar(days.sortedBy { it.getDate() })
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readEvent(parser: XmlPullParser): Event {
        parser.require(XmlPullParser.START_TAG, ns, "event")
        val name = parser.getAttributeValue(ns, "name")
        val date = parser.getAttributeValue(ns, "date")
        val duration = parser.getAttributeValue(ns, "duration").toInt()
        val startHour = Time.fromString(parser.getAttributeValue(ns, "startHour"))!!
        val endHour = Time.fromString(parser.getAttributeValue(ns, "endHour"))!!
        val color = parser.getAttributeValue(ns, "color")
//        absoluteSlot slot day week
        var resources = listOf<Resource>()
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "resources") {
                resources = readResources(parser)
            } else {
                skip(parser)
            }
        }
        return Event(name, date, duration, startHour, endHour, color, resources)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readResources(parser: XmlPullParser): List<Resource> {
        val resources = mutableListOf<Resource>()
        parser.require(XmlPullParser.START_TAG, ns, "resources")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "resource") {
                val resource = readResource(parser)
                resources.add(resource)
            } else {
                skip(parser)
            }
        }
        return resources
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readResource(parser: XmlPullParser): Resource {
        parser.require(XmlPullParser.START_TAG, ns, "resource")
        val name = parser.getAttributeValue(ns, "name")
        val category = parser.getAttributeValue(ns, "category")
        val id = parser.getAttributeValue(ns, "id").toInt()
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, ns, "resource")
        return Resource(name, category, id)
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