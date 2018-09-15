package orangy.snek

class SnekPattern(val width: Int, val height: Int, private val data: IntArray = defaultData(width, height)) {
    private var headX: Int
    private var headY: Int

    init {
        val head = findHead()
        headX = head.first
        headY = head.second
    }

    operator fun get(x: Int, y: Int): Int = data[y * width + x]
    operator fun set(x: Int, y: Int, value: Int) {
        if (value == OwnHead) {
            data[headY * width + headX] = None
            headX = x
            headY = y
        }
        data[y * width + x] = value
    }

    fun match(arena: Arena, x: Int, y: Int, direction: Int, self: Snek, mirror: Boolean): Boolean {
        var hadMatch = false
        var optionals: IntArray? = null
        for (patternIndex in 0..data.lastIndex) {
            val patternCell = data[patternIndex]
            val matchCell = patternCell and 15
            if (matchCell == None || matchCell == OwnHead)
                continue // cell without any match or matching own head, ignore it

            val patternX = patternIndex % width
            val dx = if (mirror) headX - patternX else patternX - headX
            val patternY = patternIndex / width
            val dy = patternY - headY

            val arenaX = x + rotateX(dx, dy, direction)
            val arenaY = y + rotateY(dx, dy, direction)
            if (arenaX < 0 || arenaY < 0 || arenaX >= arena.width || arenaY >= arena.height)
                return false // cell out of bounds can't match

            val arenaCell = arena[arenaX, arenaY]
            val matched = when (matchCell) {
                Empty -> arenaCell is ArenaCell.Empty
                EnemyHead -> arenaCell is ArenaCell.Head && arenaCell.snek != self
                EnemyTail -> arenaCell is ArenaCell.Tail && arenaCell.snek != self
                EnemyBody -> arenaCell is ArenaCell.Body && arenaCell.snek != self
                OwnTail -> arenaCell is ArenaCell.Tail && arenaCell.snek == self
                OwnBody -> arenaCell is ArenaCell.Body && arenaCell.snek == self
                Wall -> arenaCell is ArenaCell.Wall
                else -> throw IllegalStateException("Unknown cell pattern $matchCell")
            }
            val matchMode = patternCell shr 4
            when (matchMode) {
                Exact -> if (!matched) return false
                Not -> if (matched) return false
                Optional -> {
                    if (optionals == null) {
                        optionals = IntArray(values.size) { -1 }
                        optionals[matchCell] = 0
                    }
                    if (matched)
                        optionals[matchCell]++
                }
                else -> throw IllegalStateException("Unknown cell mode $matchMode")
            }
            hadMatch = true
        }
        if (optionals != null && optionals.any { it == 0 })
            return false // we had optional groups, but some group didn't match any item
        return hadMatch
    }

    private fun rotateX(patternX: Int, patternY: Int, direction: Int): Int = when (direction) {
        0 -> patternX // up
        1 -> -patternY // right
        2 -> -patternX // down
        3 -> patternY // left
        else -> throw IllegalStateException("Invalid rotation direction $direction")
    }

    private fun rotateY(patternX: Int, patternY: Int, direction: Int): Int = when (direction) {
        0 -> patternY // up
        1 -> patternX // right
        2 -> -patternY // down
        3 -> -patternX // left
        else -> throw IllegalStateException("Invalid rotation direction $direction")
    }

    private fun shift_rotateX(patternX: Int, patternY: Int, direction: Int): Int {
        val index = (5 - direction) and 3
        return xDirections[index] * patternX + yDirections[index] * patternY
    }

    private fun shift_rotateY(patternX: Int, patternY: Int, direction: Int): Int {
        val index = (6 - direction) and 3
        return xDirections[index] * patternX + yDirections[index] * patternY
    }

    private fun findHead(): Pair<Int, Int> {
        var head: Pair<Int, Int>? = null
        repeat(height * width) { index ->
            val cell = data[index]
            if (cell == OwnHead) {
                val x = index % width
                val y = index / width
                if (head == null) {
                    head = x to y
                } else {
                    throw UnsupportedOperationException("Pattern can't have more than one own heads")
                }
            }
        }
        if (head == null) {
            throw UnsupportedOperationException("Pattern should have own head")
        } else {
            return head!!
        }
    }

    fun hash(): Int {
        var hash = 0
        data.forEach { hash = hash * 13 + it }
        return hash
    }

    fun dumpRow(rowIndex: Int) {
        print("|")
        val shift = rowIndex * width
        data.sliceArray(shift until shift + width).forEach { cellType ->
            val char = when (cellType and 15) {
                None -> " "
                Empty -> "."
                OwnHead -> "H"
                OwnTail -> "T"
                OwnBody -> "B"
                EnemyHead -> "h"
                EnemyTail -> "t"
                EnemyBody -> "b"
                Wall -> "W"
                else -> throw UnsupportedOperationException("Unknown CellType $cellType")
            }
            val withMode = when (cellType shr 4) {
                Exact -> " $char"
                Optional -> "?$char"
                Not -> "!$char"
                else -> throw UnsupportedOperationException("Unknown CellType $cellType")
            }
            print(withMode)
        }
        print("| ")
    }

    fun copy(): SnekPattern = SnekPattern(width, height, data.copyOf())

    companion object {
        const val None = 0
        const val Empty = 1
        const val OwnHead = 2
        const val OwnTail = 3
        const val OwnBody = 4
        const val EnemyHead = 5
        const val EnemyTail = 6
        const val EnemyBody = 7
        const val Wall = 8

        const val Exact = 0
        const val Optional = 1
        const val Not = 2

        val values = intArrayOf(None, Empty, OwnHead, OwnTail, OwnBody, EnemyHead, EnemyTail, EnemyBody, Wall)
        private fun defaultData(width: Int, height: Int): IntArray {
            val data = IntArray(width * height) { None }
            data[data.size / 2] = OwnHead
            return data
        }
    }
}

