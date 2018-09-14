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
                pattern[x, y] = value
            }
        }
        patterns.add(pattern)
    }
}

fun cross(brain1: SnekBrain, brain2: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder(brain1.width, brain1.height)
    (0..(numPatterns - 1)).forEach { index ->
        val select = random.nextBoolean()
        if (select) {
            builder.pattern(brain1.patterns[index])
        } else {
            builder.pattern(brain2.patterns[index])
        }
    }
    return builder.build()
}

fun copy(brain: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder(brain.width, brain.height)
    (0..(numPatterns - 1)).forEach { index ->
        builder.pattern(brain.patterns[index])
    }
    return builder.build()
}

fun swap(brain: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder(brain.width, brain.height)
    val index1 = random.nextInt(numPatterns - 1)
    val index2 = random.nextInt(numPatterns - 1)
    (0..(numPatterns - 1)).forEach { index ->
        if (index == index1)
            builder.pattern(brain.patterns[index2])
        else if (index == index2)
            builder.pattern(brain.patterns[index1])
        else
            builder.pattern(brain.patterns[index])

    }
    return builder.build()
}

private val types = CellType.values()
fun mutate(brain: SnekBrain): SnekBrain {
    val newbrain = copyOrEmpty(brain)
    val patternIndex = random.nextInt(newbrain.patterns.size)
    val pattern = newbrain.patterns[patternIndex]
    val x = random.nextInt(pattern.width)
    val y = random.nextInt(pattern.height)
    val type = types[random.nextInt(types.size)]
    if (type == CellType.OwnHead || pattern[x, y] == CellType.OwnHead) {
        // TODO: need to move head, ignore for now
    } else {
        pattern[x, y] = type
    }
    return newbrain
}

fun copyOrEmpty(brain: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder(brain.width, brain.height)
    (0..(numPatterns - 1)).forEach { index ->
        if (random.nextInt(200) == 1) {
            builder.pattern(SnekPattern(brain.width, brain.height))
        } else {
            builder.pattern(brain.patterns[index])
        }
    }
    return builder.build()
}