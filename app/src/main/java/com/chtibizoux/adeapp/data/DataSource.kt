package com.chtibizoux.adeapp.data

import android.icu.text.SimpleDateFormat
import android.util.Xml
import com.chtibizoux.adeapp.data.xml.Calendar
import com.chtibizoux.adeapp.data.xml.CalendarParser
import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.Resource
import com.chtibizoux.adeapp.data.xml.ResourceTree
import com.chtibizoux.adeapp.data.xml.ResourcesParser
import com.chtibizoux.adeapp.data.xml.SimpleCalendar
import com.chtibizoux.adeapp.data.xml.SimpleCalendarParser
import com.chtibizoux.adeapp.data.xml.SimpleEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.xmlpull.v1.XmlPullParser
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import java.util.Locale

class DataSource {
    private fun fetchEvents(user: User): Calendar {
        val url =
            URL("${user.baseURL}/jsp/webapi?function=getEvents&detail=8&resources=${user.resourceId}&projectId=${user.projectId}&data=${user.data}&days=0")
        val connection = url.openConnection() as HttpURLConnection
        connection.run {
            readTimeout = 10000
            connectTimeout = 15000
            val parser = CalendarParser()
            return parser.parse(inputStream)
        }
    }

    private val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    private fun fetchSimpleEvents(
        user: User,
        date: Date? = null
    ): SimpleCalendar {
        val dateQuery = if (date == null) "" else "&date=${dateFormat.format(date)}"
        val url =
            URL("${user.baseURL}/jsp/webapi?function=getEvents&detail=3$dateQuery&resources=${user.resourceId}&projectId=${user.projectId}&data=${user.data}&days=0")
        val connection = url.openConnection() as HttpURLConnection
        connection.run {
            readTimeout = 10000
            connectTimeout = 15000

            val parser = SimpleCalendarParser()
            return parser.parse(inputStream)
        }
    }

    private fun fetchResources(user: User): ResourceTree {
        val url =
            URL("${user.baseURL}/jsp/webapi?function=getResources&detail=2&tree=true&projectId=${user.projectId}&data=${user.data}&days=0")
        val connection = url.openConnection() as HttpURLConnection
        connection.run {
            readTimeout = 10000
            connectTimeout = 15000
            val parser = ResourcesParser()
            return parser.parse(inputStream)
        }
    }

    private fun fetchChildren(user: User, father: Int): List<Resource> {
        val url =
            URL("${user.baseURL}/jsp/webapi?function=getResources&detail=2&tree=true&fatherIds=${father}&projectId=${user.projectId}&data=${user.data}")
        val connection = url.openConnection() as HttpURLConnection
        connection.run {
            readTimeout = 10000
            connectTimeout = 15000
            val parser = ResourcesParser()
            val resourceTree = parser.parse(inputStream)

            // Find the father
            val category = resourceTree.categories.single { it.resources.isNotEmpty() }
            var resource = category.resources.single()
            while (resource.id != father) {
                resource = resource.children.single()
            }

            return resource.children
        }
    }

    suspend fun getCalendar(user: User): Result<Calendar> = withContext(Dispatchers.IO) {
        try {
            val calendar = fetchEvents(user)
            Result.Success(calendar)
        } catch (e: Throwable) {
            System.err.println(e)
            Result.Error(IOException("Error getting calendar", e))
        }
    }

    suspend fun getResources(user: User): Result<ResourceTree> = withContext(Dispatchers.IO) {
        try {
            val resources = fetchResources(user)
            Result.Success(resources)
        } catch (e: Throwable) {
            System.err.println(e)
            Result.Error(IOException("Error getting resources", e))
        }
    }

    suspend fun getChildren(user: User, father: Int): Result<List<Resource>> =
        withContext(Dispatchers.IO) {
            try {
                val resource = fetchChildren(user, father)
                Result.Success(resource)
            } catch (e: Throwable) {
                System.err.println(e)
                Result.Error(IOException("Error getting children", e))
            }
        }

    private fun startingTimes(days: List<Day<SimpleEvent>>): List<Time> {
        val hours = mutableListOf<Time>()
        for (day in days) {
            val events = day.events.map { it.startHour }.sortedBy { it.getMinutesNumber() }
            if (events.isNotEmpty()) {
                hours.add(events.first())
            }
        }
        return hours.toSet().sortedBy { it.getMinutesNumber() }
    }

    suspend fun getStartingTime(user: User, date: Date): Result<Time> =
        withContext(Dispatchers.IO) {
            try {
                val (days) = fetchSimpleEvents(user, date)
                val hours = startingTimes(days)
                if (hours.size != 1) {
                    throw BadHoursError("Bad hours number ${hours.size}")
                }
                Result.Success(hours.first())
            } catch (e: Throwable) {
                System.err.println(e)
                Result.Error(
                    IOException(
                        "Error getting current starting time",
                        e
                    )
                )
            }
        }

    suspend fun getStartingTimes(user: User): Result<List<Time>> = withContext(Dispatchers.IO) {
        try {
            val (days) = fetchSimpleEvents(user)
            val hours = startingTimes(days)
            Result.Success(hours.filter { it.getHourNumber() < 12 })
        } catch (e: Throwable) {
            System.err.println(e)
            Result.Error(IOException("Error getting calendar", e))
        }
    }

    suspend fun checkConnection(user: User): Boolean = withContext(Dispatchers.IO) {
        try {
            val url =
                URL("${user.baseURL}/jsp/webapi?function=getResources&id=${user.resourceId}&projectId=${user.projectId}&data=${user.data}")
            val connection = url.openConnection() as HttpURLConnection
            connection.run {
                readTimeout = 10000
                connectTimeout = 15000

                inputStream.use {
                    val parser: XmlPullParser = Xml.newPullParser()
                    parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                    parser.setInput(inputStream, null)
                    parser.nextTag()
                    parser.require(XmlPullParser.START_TAG, null, "resources")
                    parser.nextTag()
                    parser.require(XmlPullParser.START_TAG, null, "resource")
                    val id = parser.getAttributeValue(null, "id")
                    if (id.toInt() != user.resourceId) {
                        throw Error("Wrong resource id")
                    }
                    parser.nextTag()
                    parser.require(XmlPullParser.END_TAG, null, "resource")
                    parser.nextTag()
                    parser.require(XmlPullParser.END_TAG, null, "resources")
                    true
                }
            }
        } catch (e: Throwable) {
            System.err.println(e)
            false
        }
    }
}