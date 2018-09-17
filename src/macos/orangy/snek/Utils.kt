package orangy.snek

import kotlin.system.*

actual fun nanoTime() : Long = getTimeNanos()
actual fun measureNanoTime(block: () -> Unit): Long = kotlin.system.measureNanoTime(block)
actual fun pause() {}
