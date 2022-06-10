import com.google.gson.Gson
import java.io.File
import java.net.URL
import java.time.LocalDate
import kotlin.math.roundToInt

fun main(args: Array<String>) {
    val startDate = "2021-09-10"
    val endDate = LocalDate.now().toString()
    val json = Gson().fromJson(
        getJSON(
            false,
            File("/Users/shmuel/IdeaProjects/ParseWakaTime/src/wakatime-software.sternbachgmail.com-1dc3ae8aa0b040d39cc8d00842150ee5.json"),
            startDate,
            endDate
        ),
        JSON::class.java
    )
    println(
        json
            .days
            .flatMap { it.projects }
            .filter { it.name == "TorahDownloads" }
            .sumOf { it.grand_total.total_seconds!! }
            .also { println("Time in double: $it") }
            .roundToInt()
            .toHrMinSec()
    )
}

private fun getJSON(getFromWeb: Boolean, file: File?, startDate: String?, endDate: String?) = if (getFromWeb)
    URL("https://wakatime.com/api/v1/users/current/summaries?start=${startDate}&end=$endDate?access_token=66142ee1-2ce3-4bb6-a8a7-7d685219f78e").readText()
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