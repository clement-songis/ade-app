package com.chtibizoux.adeapp.data

import android.icu.text.SimpleDateFormat
import com.chtibizoux.adeapp.data.xml.Calendar
import com.chtibizoux.adeapp.data.xml.CalendarParser
import com.chtibizoux.adeapp.data.xml.Day
import com.chtibizoux.adeapp.data.xml.SimpleCalendar
import com.chtibizoux.adeapp.data.xml.SimpleCalendarParser
import com.chtibizoux.adeapp.data.xml.SimpleEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.util.Date
import java.util.Locale

const val INTRANET_BASE = "https://intranet.iut-valence.fr"
const val ADE_BASE = "https://ade-iut-valence-ro.grenet.fr"
const val LOGIN_URL = "https://cas-uga.grenet.fr/login"
const val PROJECT_ID = 3

const val LOGIN_LT = "LT-831095-LtvSj67p9nJxcaIahJtGlGfBwHogPi-cas-uga.grenet.fr"
const val LOGIN_EXECUTION =
    "a7873355-1e09-4311-ba69-ce9e7edbfc92_AAAAIgAAABB3Qa20iIY9rw2CMcL9WoGnAAAABmFlczEyOBSKP6FPEJk9vjaZdhlNUTg6rmU1bqDfAs6IlPxH3M%2BjSxoy0OK6ywxaqGYofwg9EP%2BxUwCHSckbbxXmIdj8Z451woNi3XGW8pbghxkfG4j9t83IRLPixJvnSZ5Vc%2BIMH%2Bt7h9u%2BB69bnZdXt3we4FQqbnIXMJaR4YIsgQb7p94J9yY3cDDkER2GGoK4nZYmctHi1Nb6X9yTfkCydEfEZzF30ohN8Ojg9A2TxJH163oAoV9ssRU3Ob5S%2FeMAsv%2BENvR%2FIr7MS2vDT%2FJskCN0%2BhovAi06sDj%2BPqgdeL%2Fw1ghSTcDDTf6NYfs5f1MuqrDK7TXzinfpHfHc3nEmrcoMYiLeCf6my5xamqqbtE6CulzwCbwmGuQVf8tEOM7Iq3M2S%2Bjbh1eRzzHGcPvT3bw8XO2TtsIbQTxgP9MSyEi3kTnXmzBYDxrtb3UEULVwQCvSfDkbsqhgW47q7xD6fIAHaZMSEpQq8uN4Pogif0rP437XopJ5%2BL8vMq4LYDZn8qigbAUmMQyXb%2BF0GE%2FR9JdlkdTh1acP8JrpSLWOGjVU%2Bt6l5VpWN4aeM09wNSsEBLSSHojF7VIxLEl3Z%2FlYYBNEde1Z3icrobIQ9lsQ8RV87GE%2FG0jR1wEa4oGn902HZl%2FvlERs7xKGh21sE5xIM303c7eJ%2FDWUPmsKcX94pYy%2BMoxgoVLvSiPJbOozHma8XeXjePSiAyhwW1PQwXDN6ICcqh5Hu2mOmxZWp4j2hz%2FIZt5g6E82NQvJyCe5xbF2%2BvNPelV4Ne0F2XRCBSv8fBpKmF8hU69MkUzZehWk5D%2FJtuj9xM49swpcXaEwv8VWX7oBeQWYKKqCwc34pej11ULLCJtj2VQAVRg%2BDwJAjgavJem7NYYvgMQ6Tm8t%2BPCytzuPi11yud2PFYdSPxBCwkK4vllDxU50b39bIAG80s4Y813aDY4MAjgE8nDQ2S7mxm7K7ZHcu0mxhmwJQ3ENrHbhucypnaAW%2FTqOudadDO3RWBFSqvU4cgH7%2BmPA%2FAsomsdFCbkP9AkMwrPMCIFOZPMfqdtk%2B%2BS7JZyJXW%2Bf8tdDYoRdcFGd0hgGhUEqvVSlG3G3OgxuukcqX5mG%2FViEGw5W5tlq5Z5TTjVW%2BLZ7aIkpUDwGOxhlYL2Rioh%2B%2FA%2FErn2pzJ0fmr8CkfdYGTLQdlF0n646eKrcz6IL6W4YbCblr8SXyY%2BWH7SrV1beRwKQlGE%2FgJWP1tvo0Z5yzrTiV%2BamuAzYRFjcj3jCjXP0AKJpTxYCJomd6iAUa1x1qtmWGYGs%2BZNQ4xtsBOX5eciEVvxd8%2F%2B2u7Ex2bHhmx3jxWKP%2BQmd5Foc9EoJKnIh9YguE0cJNgz%2FxJsBJxc7tm9UBWt82jyt84fr%2FqPLLOCpZdrrCoxm9A%2BCQjc9JLzEICHLuL6DM1Th2bVikkkD%2FL2g5r1d0Tk%2BoUah%2FDFAYqAoMwsg4LSIOftjlZUDSS0BjvT%2Fn42YprTKO4k3zxmBX6EBloLUprVP8Wo6WclxKEi23%2FB%2F51SZ%2BoPDZpdWtnLnSlB5fYckwx9WTYyk3qRCTzt00VJaJ1rlrAELB9Xr0tcgmj3LQGOwIJ%2FFLdMlYj3t4RoO8xsiQBQrxUoFPTlePylY7did5q28hh9O1tVP64r9kHLCJyiz5zqOV7IIA%2BKIP8p4cth2w97Pxoj2nvPmeYPRP%2BBIKfTxrBQmt8b6TNWVacLOPYlcxnA%2FH%2Fx7eOs9dI4dCdfdLg%2BTxFGXUFLInJPiXlOxVHYqZ0E%2FRePtukCEALqjXELS7aXnjJkQxrVkXHMbtRGisFW4bbnCWPYWeFveb3y4rFxKnhnGR7N5jaCiGXKAYp3h26yqN2h7Wh0NQ99d0EnnovH%2BPIgM%2BEWWpOhz12cSag0uDOeH%2FPiI9cTkNcYyIoiHhW7Gfn9CtOQyGpXu3ZVTwh0yn0SRwmXdKokJtibc%2BIfeZkSWLVQmAg6rORO1J4STpQ2wi%2BWGzGfP4QDd0iE5nXX59nR3FcXl%2Bzs8wGjX3bo4Yu4m0DvrTM%2BnK6y8C169kBMZWcRx8MS6SJba%2FzlOYT1H0u%2Bw9eT5egUkEWbhAHU5dbaE2Nr%2BVLX6bpr8pTqQhCokbw%2FAICEaQUL1me1Axj3YEDHG%2FdKynJ7WEXKiVsRUSn2a2S7YhYi4peS4e1AFvpWY5i0ASmKynCBcC2eiqg1IO61mn0%2FoRolqd0%2BCsIDjpQST9J0ANTZwMwT9WJC6K9fjZreag%2F4SU%2FFjS3t2hXoQI5Zbk8FCnTEMpZ6RsYUesTEM%2BVncA7w4VzzJv5ORexWlU06gV%2BBmtCXzo8V4%2FbiPuF7nyj%2BpdTklp9D9n5TLh%2FD%2Bx21UcoXGxfFJroK0KeAxG4saRcUTh5%2FR8mihaA%3D%3D"
// if const variables don't work, scrap the page
// "$LOGIN_URL?service=${URLEncoder.encode(INTRANET_BASE, "UTF-8")}"
// encodeURIComponent(document.querySelector("input[name='lt']").value)
// encodeURIComponent(document.querySelector("input[name='execution']").value)

class DataSource {
    private fun fetchTicket(username: String, password: String): String {

        val encodedUsername = URLEncoder.encode(username, "UTF-8")
        val encodedPassword = URLEncoder.encode(password, "UTF-8")
        val query =
            "username=$encodedUsername&password=$encodedPassword&lt=$LOGIN_LT&execution=$LOGIN_EXECUTION&_eventId=submit"

        val connection = URL(LOGIN_URL).openConnection() as HttpURLConnection
        try {
            connection.doOutput = true
            connection.instanceFollowRedirects = false
            connection.setFixedLengthStreamingMode(query.toByteArray().size)
            connection.outputStream.use { out ->
                out.write(query.toByteArray())
            }

            val location = connection.getHeaderField("Location")
            if (location.isNullOrEmpty()) {
                throw Error("No location")
            }
            return location
        } finally {
            connection.disconnect()
        }
    }

    private fun fetchCookies(link: String): String {
        val url = URL(link)
        val connection = url.openConnection() as HttpURLConnection
        connection.run {
            instanceFollowRedirects = false
            val cookie = getHeaderField("Set-Cookie")
            if (cookie.isNullOrEmpty()) {
                throw Error("No cookie")
            }
            return cookie
        }
    }

    private fun fetchResourceId(cookie: String): Int {
        val connection = URL(INTRANET_BASE).openConnection() as HttpURLConnection
        connection.run {
            setRequestProperty("Cookie", cookie)

            val allText =
                BufferedReader(InputStreamReader(inputStream)).use(BufferedReader::readText)

            val matchResult =
                Regex("href=\"/edt/([0-9]+)\"").find(allText) ?: throw Error("No resource ID")
            val (stringId) = matchResult.destructured

            return stringId.toIntOrNull() ?: throw Error("No resource ID")
        }
    }

    private fun fetchData(resourceId: Int, cookie: String): String {
        val url = URL("$INTRANET_BASE/edt/$resourceId")
        val connection = url.openConnection() as HttpURLConnection
        connection.run {
            instanceFollowRedirects = false
            setRequestProperty("Cookie", cookie)

            val location = getHeaderField("Location")
            if (location.isNullOrEmpty()) {
                throw Error("No edt location")
            }
            val dataURL = URL(location)
            val matchResult = Regex("data=([^&]+)&").find(dataURL.query) ?: throw Error("No data")
            val (data) = matchResult.destructured
            return data
        }
    }

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
            URL("$ADE_BASE/jsp/webapi?function=getEvents&detail=8&resources=${user.resourceId}&projectId=$PROJECT_ID&data=${user.data}")
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
        var query = ""
        if (date != null) {
            query += "&date=${dateFormat.format(date)}"
        }
        val url =
            URL("$ADE_BASE/jsp/webapi?function=getEvents&detail=3$query&resources=${user.resourceId}&projectId=$PROJECT_ID&data=${user.data}")
        val connection = url.openConnection() as HttpURLConnection
        connection.run {
            readTimeout = 10000
            connectTimeout = 15000

            val parser = SimpleCalendarParser()
            return parser.parse(inputStream)
        }
    }

//    private fun fetchResources(data: String): Resources {
//        val url =
//            URL("$ADE_BASE/jsp/webapi?function=getResources&detail=5&projectId=$PROJECT_ID&data=${data}")
//        val connection = url.openConnection() as HttpURLConnection
//        connection.run {
//            readTimeout = 10000
//            connectTimeout = 15000
//            val parser = ResourcesParser()
//            return parser.parse(inputStream)
//        }
//    }

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

//    suspend fun getResources(data: String): Result<Resources> = withContext(Dispatchers.IO) {
//        try {
//            val resources = fetchResources(data)
//            return@withContext Result.Success(resources)
//        } catch (e: Throwable) {
//            println(e)
//            return@withContext Result.Error(IOException("Error getting resources", e))
//        }
//    }

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

    suspend fun login(username: String, password: String): Result<User> =
        withContext(Dispatchers.IO) {
            try {
                val link = fetchTicket(username, password)
                val cookie = fetchCookies(link)
                val resourceId = fetchResourceId(cookie)
                val data = fetchData(resourceId, cookie)

                return@withContext Result.Success(User(resourceId, data))
            } catch (e: Throwable) {
                println(e)
                return@withContext Result.Error(IOException("Error logging in", e))
            }
        }
}