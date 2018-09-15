package orangy.snek

val numPatterns = 9

class SnekBrain(val width: Int, val height: Int, val patterns: List<SnekPattern>) {
    
    fun selectDirection(arena: Arena, position: SnekPosition): Int {
        if (position.isDead()) // dead cannot dance
            return SnekDirection.None

        val snek = position.snek
        val headX = position.headX()
        val headY = position.headY()
        var currentDirection = position.direction()
        val possibleDirections = intArrayOf(-1, -1, -1, -1)
        var possibleIndex = 0
        repeat(4) {
            val dx = SnekDirection.dx(currentDirection)
            val dy = SnekDirection.dy(currentDirection)
            val cellInDirection = arena[headX + dx, headY + dy]
            when (cellInDirection) {
                ArenaCell.Empty -> possibleDirections[possibleIndex++] = currentDirection
                is ArenaCell.Tail -> {
                    if (cellInDirection.snek == snek) {
                        // lengthy sneks can go into own tail
                        // but if it's of length 2, then no, cause it would flip
                        if (position.length() > 2)
                            possibleDirections[possibleIndex++] = currentDirection
                    } else {
                        possibleDirections[possibleIndex++] = currentDirection // other's tail, go!
                    }
                }
            }
            currentDirection = SnekDirection.clockwise(currentDirection)
        }

        if (possibleIndex == 0)
            return SnekDirection.None // stuck

        patterns.forEachIndexed { p, pattern ->
            //print(" P:$p [")
            try {
                for (index in 0 until possibleIndex) {
                    val direction = possibleDirections[index]
                    //print(" D$direction:")
                    val matchNormal = pattern.match(arena, headX, headY, direction, snek, false)
                    //print(matchNormal.toString().first())
                    if (matchNormal)
                        return direction
                    val matchMirror = pattern.match(arena, headX, headY, direction, snek, true)
                    //print(matchMirror.toString().first())
                    if (matchMirror)
                        return direction
                }
            } finally {
                //print("] ")
            }
        }

        return possibleDirections[random.nextInt(possibleIndex)]
    }

    fun dump() {
        repeat(7) { rowIndex ->
            patterns.forEach { pattern ->
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

