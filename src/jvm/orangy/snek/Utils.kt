package orangy.snek

actual fun nanoTime() : Long = System.nanoTime()
actual fun measureNanoTime(block: () -> Unit): Long = kotlin.system.measureNanoTime(block)