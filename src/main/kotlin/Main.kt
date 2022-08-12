import com.google.gson.Gson
import java.io.File
import java.lang.Math.abs
import java.net.URL
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.absoluteValue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

fun main() { //
    val hours = getHoursFromHeartbeats(LocalDate.of(2022, 3, 15)) {
        it == "TorahDownloads" ||
                it == "uamp_for_td" ||
                it == "AutoTaggrSuite-Original" ||
                it == "AutoTaggerSuite" ||
                it == "okdownload-root" ||
                it == "uamp" ||
                it == "KotlinFunctionLibrary" ||
                it == "material-components-android" ||
                it == "ReadAPK" ||
                it == "vlc-android" ||
                it == "dex2jar" ||
                it == "TestingSQLQueryBuilders" ||
                it == "public-android"
    }
    //println("Projects: ${daysAfterLastPayment.flatMapTo(HashSet()) { it.projects.map { it.name } }}")

    print(
        hours
    )
    println(" hours")
}

@OptIn(ExperimentalTime::class)
fun getHoursFromHeartbeats(
    since: LocalDate,
    isValidProject: (String) -> Boolean
): Double {
    val json = heartbeatsJSON()
    val daysAfterLastPayment = json.days.filter {
        println("Got: ${it.date}")
        LocalDate.parse(it.date, DateTimeFormatter.ISO_LOCAL_DATE).isAfter(since)
    }
    println("Days after last payment: $daysAfterLastPayment")
    val numHeartbeatDays = daysAfterLastPayment.distinctBy { it.date }.map { it.date }.size//joinToString("\n")
    println("Days: $numHeartbeatDays")
    val offset = OffsetDateTime.now().offset
    return daysAfterLastPayment
        .asSequence()
        .flatMap {
//            println("Flat map: $it")
            it.heartbeats.map { it.time to (it.project ?: "null") }
        }
        .filter {
//            println("Filter: $it")
            isValidProject(it.second)
        }
        .map {
//            println("Sum of: $it")
            val seconds = it.first.toLong()
            val nanoSeconds = abs(it.first % 1.0).seconds.inWholeNanoseconds.toInt()
            println("Taking epoch   ${it.first} and turning it into     $seconds seconds and    $nanoSeconds nanoseconds")
            LocalDateTime.ofEpochSecond(seconds, nanoSeconds, offset)
        }
        .toList()
        .let {
            val durations = mutableListOf<Long>() //seconds
            var previous = it.first()
            val list = mutableListOf<LocalDateTime>(previous)
            for(heartbeat in it.subList(1, it.size)) {
                if(Duration.between(previous, heartbeat).toMinutes().absoluteValue <= 2L) { //if less than 2 minutes apart, they are part of the same duration/streak
                    list.add(heartbeat)
                    println("Adding heartbeat")
                } else { //more than 2 minutes is a new streak/session/duration, so add the previous duration and start new
                    val duration =
                        if (list.size > 1) Duration.between(list.last(), list.first()).toSeconds().absoluteValue.also { println("Duration: $it") }
                        else if (list.size == 1) (120L).also { println("Only one") }
                        else (0L).also { println("None") }
                    println("farther than 2 minutes, duration: $duration")
                    durations.add(duration) //if there is no clear start and end (but rather just one blip/heartbeat between the previous duration/streak and the next), assume I was coding for two minutes
                    list.clear()
                    list.add(heartbeat)
                }
                previous = heartbeat
            }
            durations.sum().also { println("Final sum: $it") } / 3600.0
        }
}

fun getHoursFromSummary(
    since: LocalDate,
    isValidProject: (String) -> Boolean
): Double {
    val json = summaryJSON()
    val daysAfterLastPayment = json.days.filter {
        val month = it.date.substring(5, 7)
        val day = it.date.substring(8, 10)
        val monthString = since.month.toString()
        it.date.substring(
            0,
            4
        ) == since.year.toString() /*"2022"*/ && (month > monthString /*"03"*/ || (month == monthString && day > since.dayOfMonth.toString()/*"15"*/))
    }
    return (daysAfterLastPayment
        .asSequence()
        .flatMap {
            println("Flat map: $it")
            it.projects
        }
        .filter {
            println("Filter: $it")
            it.name?.let { it1 -> isValidProject(it1) } ?: false
        }
        .sumOf {
            println("Sum of: $it")
            it.grand_total.total_seconds ?: 0.toDouble().also { println("Was null: $it") }
        }
            / 3600.0)
}

private fun summaryJSON() = Gson().fromJson(
    File("summary_wakatime-software.sternbachgmail.com-1dc3ae8aa0b040d39cc8d00842150ee5.json").readText(),
    SummaryJSON::class.java
)

private fun heartbeatsJSON() = Gson().fromJson(
    File("heartbeats_wakatime-software.sternbachgmail.com-1dc3ae8aa0b040d39cc8d00842150ee5.json").readText(),
    HeartbeatsJSON::class.java
)

private fun getJSON(getFromWeb: Boolean, file: File?, startDate: String?, endDate: String?) = if (getFromWeb)
    URL("https://wakatime.com/api/v1/users/sternbachsoftware/summaries?start=${startDate}&end=$endDate&access_token=66142ee1-2ce3-4bb6-a8a7-7d685219f78e").readText()
else file!!.readText()

fun Int.toHrMinSec(hour: Int = 0, minute: Int = 0, second: Int = this): Triple<Int, Int, Int> {
    var minute1 = minute
    var second1 = second
    var hour1 = hour
    minute1 += (second1 / 60)
    hour1 += (minute1 / 60)
    second1 %= 60
    minute1 %= 60
    return Triple(hour1, minute1, second1)
}