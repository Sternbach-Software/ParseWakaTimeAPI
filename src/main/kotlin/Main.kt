import com.google.gson.Gson
import java.io.File
import kotlin.math.roundToInt

fun main(args: Array<String>) {
val json = Gson().fromJson(
    File("/Users/shmuel/IdeaProjects/ParseWakaTime/src/wakatime-software.sternbachgmail.com-1dc3ae8aa0b040d39cc8d00842150ee5.json").readText(),
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