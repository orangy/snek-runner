@file:JvmName("JvmUtils")
package orangy.snek

import kotlin.jvm.*

val dictionary = "qwertyuiopasdfghjklzxcvbnm"
fun randomName() = (0..5).joinToString("") { dictionary[random.nextInt(dictionary.length)].toString() }

fun Long.padZero(pad : Int = 2) = this.toString().padStart(pad, '0')

expect fun nanoTime() : Long 
expect fun measureNanoTime(block: () -> Unit): Long