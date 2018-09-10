package org.orangy.snek

val numPatterns = 9

class SnekBrain(val patterns: List<SnekPattern>) {
    fun dump() {
        repeat(7) { rowIndex ->
            patterns.forEachIndexed { p, pattern ->
                val data = pattern.data
                printRow(data[rowIndex])
                print("  ")
            }
            println()
        }
    }

    fun printRow(row: Array<CellType>) {
        print("|")
        row.forEach { cellType ->
            val char = when (cellType) {
                CellType.None -> " "
                CellType.Empty -> "."
                CellType.OwnHead -> "H"
                CellType.OwnTail -> "T"
                CellType.OwnBody -> "B"
                CellType.EnemyHead -> "h"
                CellType.EnemyTail -> "t"
                CellType.EnemyBody -> "b"
                CellType.Wall -> "W"
            }
            print(char)
        }
        print("|")
    }
}

fun snekBrain(body: SnekBrainBuilder.() -> Unit) = SnekBrainBuilder().apply(body).build()

class SnekBrainBuilder {
    private val patterns = mutableListOf<SnekPattern>()
    fun build(): SnekBrain {
        repeat(numPatterns - patterns.size) {
            val data = SnekPattern.emptyData()
            data[3][3] = CellType.OwnHead
            patterns.add(SnekPattern(data))
        }
        return SnekBrain(patterns)
    }

    fun pattern(other: SnekPattern) {
        val data = SnekPattern.emptyData()
        other.data.forEachIndexed { y, row ->
            row.forEachIndexed { x, cellType ->
                data[y][x] = cellType
            }
        }
        patterns.add(SnekPattern(data))
    }

    fun pattern(text: String) {
        val data = SnekPattern.emptyData()
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

fun cross(brain1: SnekBrain, brain2: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder()
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
    val builder = SnekBrainBuilder()
    (0..(numPatterns - 1)).forEach { index ->
        builder.pattern(brain.patterns[index])
    }
    return builder.build()
}

fun swap(brain: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder()
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
    val y = random.nextInt(pattern.data.size)
    val row = pattern.data[y]
    val x = random.nextInt(row.size)
    val type = types[random.nextInt(types.size)]
    if (type == CellType.OwnHead || row[x] == CellType.OwnHead) {
        // TODO: need to move head, ignore for now
    } else {
        row[x] = type
    }
    return newbrain
}

fun copyOrEmpty(brain: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder()
    (0..(numPatterns - 1)).forEach { index ->
        if (random.nextInt(200) == 1) {
            val data = SnekPattern.emptyData()
            data[3][3] = CellType.OwnHead
            builder.pattern(SnekPattern(data))
        } else {
            builder.pattern(brain.patterns[index])
        }
    }
    return builder.build()
}