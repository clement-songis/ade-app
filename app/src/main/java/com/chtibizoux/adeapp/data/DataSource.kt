package com.chtibizoux.adeapp.data

import android.icu.text.SimpleDateFormat
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
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Date
import java.util.Locale

class DataSource {
//    $ADE_BASE/jsp/webapi?function=${getResources}&projectId=$PROJECT_ID&detail=${3}&data=$data

//    $ADE_BASE/jsp/webapi?function=imageET&displayConfId=2&detail=2&weeks=20&days=0,1,2,3,4,5,6&width=1920&height=1080&resources=67&projectId=3&data=$data
//    $ADE_BASE/jsp/webapi?function=imageET&displayConfId=2&weeks=20&days=0&width=540&height=960&resources=67&projectId=3&data=$data

//    private fun fetchIcs(resourceId: Int): String {
//        val url = URL("$INTRANET_BASE/ICS_ADE/$resourceId.ics")
////        $ADE_BASE/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=$resourceId&projectId=$PROJECT_ID&calType=ical&firstDate=2024-01-22&lastDate=2024-01-26
////        val url = URL("$ADE_BASE/jsp/custom/modules/plannings/anonymous_cal.jsp?data=$data&projectId=$PROJECT_ID&nbWeeks=4&resources=$resourceId")
//        val connection = url.openConnection() as HttpURLConnection
//        connection.run {
//            return BufferedReader(InputStreamReader(inputStream)).use(BufferedReader::readText)
//        }
//    }

    private fun fetchEvents(user: User): Calendar {
        val url =
            URL("${user.baseURL}/jsp/webapi?function=getEvents&detail=8&resources=${user.resourceId}&projectId=${user.projectId}&data=${user.data}")
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
        date: Date? = null/*, week: Int? = null, day: Int? = null*/
    ): SimpleCalendar {
        val dateQuery = if (date == null) "" else "&date=${dateFormat.format(date)}"
        val url =
            URL("${user.baseURL}/jsp/webapi?function=getEvents&detail=3$dateQuery&resources=${user.resourceId}&projectId=${user.projectId}&data=${user.data}")
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
            URL("${user.baseURL}/jsp/webapi?function=getResources&detail=2&tree=true&projectId=${user.projectId}&data=${user.data}")
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

//    suspend fun getCalendar(resourceId: Int): Result<MyCalendar> = withContext(Dispatchers.IO) {
//        try {
//            val ics = getIcs(resourceId)
//            val parser = Parser()
//            val calendar = parser.parse(ics)
//            return@withContext Result.Success(calendar)
//        } catch (e: Throwable) {
//            println(e)
//            return@withContext Result.Error(IOException("Error getting calendar", e))
//        }
//    }

    suspend fun getCalendar(user: User): Result<Calendar> = withContext(Dispatchers.IO) {
        try {
            val calendar = fetchEvents(user)
            return@withContext Result.Success(calendar)
        } catch (e: Throwable) {
            println(e)
            return@withContext Result.Error(IOException("Error getting calendar", e))
        }
    }

    suspend fun getResources(user: User): Result<ResourceTree> = withContext(Dispatchers.IO) {
        try {
            val resources = fetchResources(user)
            return@withContext Result.Success(resources)
        } catch (e: Throwable) {
            println(e)
            return@withContext Result.Error(IOException("Error getting resources", e))
        }
    }

    suspend fun getChildren(user: User, father: Int): Result<List<Resource>> = withContext(Dispatchers.IO) {
        try {
            val resource = fetchChildren(user, father)
            return@withContext Result.Success(resource)
        } catch (e: Throwable) {
            println(e)
            return@withContext Result.Error(IOException("Error getting children", e))
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

    suspend fun getStartingTime(user: User, date: Date): Result<Time> = withContext(Dispatchers.IO) {
        try {
            val (days) = fetchSimpleEvents(user, date)
            val hours = startingTimes(days)
            if (hours.size != 1) {
                throw BadHoursError("Bad hours number ${hours.size}")
            }
            return@withContext Result.Success(hours.first())
        } catch (e: Throwable) {
            println(e)
            return@withContext Result.Error(IOException("Error getting current starting time", e))
        }
    }

    suspend fun getStartingTimes(user: User): Result<List<Time>> = withContext(Dispatchers.IO) {
        try {
            val (days) = fetchSimpleEvents(user)
            val hours = startingTimes(days)
            return@withContext Result.Success(hours.filter { it.getHourNumber() < 12 })
        } catch (e: Throwable) {
            println(e)
            return@withContext Result.Error(IOException("Error getting calendar", e))
        }
    }
}