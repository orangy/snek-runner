package org.orangy.snek

class SnekBrain(val patterns: List<SnekPattern>) {}

fun snekBrain(body: SnekBrainBuilder.() -> Unit) = SnekBrainBuilder().apply(body).build()

class SnekBrainBuilder {
    private val patterns = mutableListOf<SnekPattern>()
    fun build(): SnekBrain = SnekBrain(patterns)
    
    fun pattern(text: String) {
        val data = Array(7) { y -> Array(7) { x -> CellType.None } } 
        text.lines().forEachIndexed { y, line ->
            line.forEachIndexed { x, cell ->
                val value = when (cell) {
                    ' ' -> CellType.None
                    '.' -> CellType.Empty
                    'H' -> CellType.OwnHead
                    'T' -> CellType.OwnTail
                    'B' -> CellType.OwnBody
                    'h' -> CellType.EnemyHead
                    't' -> CellType.EnemyTail
                    'b' -> CellType.EnemyBody
                    'W' -> CellType.Wall
                    else -> throw UnsupportedOperationException("Cell type '$cell' is not recognized")
                }
                data[y][x] = value
            }
        }
        patterns.add(SnekPattern(data))
    }
}