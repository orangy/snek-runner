package org.orangy.snek

class SnekBrain(val patterns: List<SnekPattern>) {}

fun snekBrain(body: SnekBrainBuilder.() -> Unit) = SnekBrainBuilder().apply(body).build()

class SnekBrainBuilder {
    private val patterns = mutableListOf<SnekPattern>()
    fun build(): SnekBrain = SnekBrain(patterns)

    fun pattern(body: SnekPatternBuilder.() -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    fun pattern(text: String) {
        val patternData = text.lines().map { line ->
            line.map { cell ->
                when (cell) {
                    ' ' -> CellType.None
                    '.' -> CellType.Empty
                    'H' -> CellType.OwnHead
                    'T' -> CellType.OwnTail
                    'B' -> CellType.OwnBody
                    'h' -> CellType.EnemyHead
                    't' -> CellType.EnemyTail
                    'b' -> CellType.EnemyBody
                    else -> throw UnsupportedOperationException("Cell type '$cell' is not recognized")
                }
            }.toTypedArray()
        }.toTypedArray()
        patterns.add(SnekPattern(patternData))
    }
}