@file:JvmName("JvmUtils")
package orangy.snek

import kotlin.jvm.*
import kotlin.math.*

val dictionary = "qwertyuiopasdfghjklzxcvbnm"
fun randomName() = (0..5).joinToString("") { dictionary[random.nextInt(dictionary.length)].toString() }

fun Long.padZero(pad : Int = 2) = this.toString().padStart(pad, '0')
fun Int.padZero(pad : Int = 2) = this.toString().padStart(pad, '0')

expect fun nanoTime() : Long 
expect fun measureNanoTime(block: () -> Unit): Long
expect fun pause()

private val histogram = " \u2581\u2582\u2583\u2584\u2585\u2586\u2587\u2588"
fun Snek.dumpStatistics(sneks: List<Snek>, sneksResults: Map<Snek, List<SnekStatus>>) {
    if (this in sneks)
        print("* ")
    else
        print("  ")
    val lengths = sneksResults[this]!!.map { it.length }
    val avgLength = lengths.average().toInt().toString().padStart(2, ' ')
    val minLength = lengths.min().toString().padStart(2, ' ')
    val maxLength = lengths.max().toString().padStart(2, ' ')
    val median = lengths.median().toString().padStart(2, ' ')
    print("$name: $avgLength [$minLength..$maxLength] ~$median")
    val lengthsCount = lengths.groupingBy { it }.eachCount()
    print(" [")
    (1..(participants * 9 + 1)).forEach {
        val frequency = (lengthsCount[it]?.toDouble() ?: 0.0) / lengths.size
        val bar = (frequency * 8).roundToInt()
        print(histogram[bar])
    }
    println("]")
}
