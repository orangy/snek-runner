package orangy.snek

val numPatterns = 9

class SnekBrain(val width: Int, val height: Int, val patterns: List<SnekPattern>) {
    fun dump() {
        repeat(7) { rowIndex ->
            patterns.forEachIndexed { p, pattern ->
                pattern.dumpRow(rowIndex)
            }
            println()
        }
    }

    fun hash(): Int {
        var hash = 0
        patterns.forEach {
            hash = hash * 13 + it.hash()
        }
        return hash
    }
}

fun snekBrain(width: Int, height: Int, body: SnekBrainBuilder.() -> Unit) = SnekBrainBuilder(width, height).apply(body).build()

class SnekBrainBuilder(val width: Int, val height: Int) {
    private val patterns = mutableListOf<SnekPattern>()
    fun build(): SnekBrain {
        repeat(numPatterns - patterns.size) {
            patterns.add(SnekPattern(width, height))
        }
        return SnekBrain(width, height, patterns)
    }

    fun pattern(other: SnekPattern) {
        patterns.add(other.copy())
    }

    fun pattern(text: String) {
        val pattern = SnekPattern(width, height)
        text.lines().forEachIndexed { y, line ->
            line.forEachIndexed { x, cell ->
                val value = when (cell) {
                    ' ' -> SnekPattern.None
                    '.' -> SnekPattern.Empty
                    'H' -> SnekPattern.OwnHead
                    'T' -> SnekPattern.OwnTail
                    'B' -> SnekPattern.OwnBody
                    'h' -> SnekPattern.EnemyHead
                    't' -> SnekPattern.EnemyTail
                    'b' -> SnekPattern.EnemyBody
                    'W' -> SnekPattern.Wall
                    else -> throw UnsupportedOperationException("Cell type '$cell' is not recognized")
                }
                pattern[x, y] = value
            }
        }
        patterns.add(pattern)
    }
}

