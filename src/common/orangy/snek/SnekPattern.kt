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
        if (value == CellType.OwnHead) {
            data[headY * width + headX] = CellType.None
            headX = x
            headY = y
        }
        data[y * width + x] = value
    }

    fun match(arena: Arena, x: Int, y: Int, direction: Int, self: Snek, mirror: Boolean): Boolean {
        var hadMatch = false
        for (patternIndex in (0 until data.lastIndex)) {
            val patternCell = data[patternIndex]
            if (patternCell == CellType.None || patternCell == CellType.OwnHead)
                continue // cell without any match or matching own head, ignore it

            val patternX = patternIndex % width
            val patternY = patternIndex / width
            val dx = if (mirror) headX - patternX else patternX - headX
            val dy = patternY - headY
            val arenaX = x + rotateX(dx, dy, direction)
            val arenaY = y + rotateY(dx, dy, direction)
            if (arenaX < 0 || arenaY < 0 || arenaX >= arena.width || arenaY >= arena.height)
                return false // cell out of bounds can't match

            val arenaCell = arena[arenaX, arenaY]
            when (patternCell) {
                CellType.Empty -> if (arenaCell != ArenaCell.Empty) return false
                CellType.OwnHead -> if (arenaCell !is ArenaCell.Head || arenaCell.snek != self) return false
                CellType.OwnTail -> if (arenaCell !is ArenaCell.Tail || arenaCell.snek != self) return false
                CellType.OwnBody -> if (arenaCell !is ArenaCell.Body || arenaCell.snek != self) return false
                CellType.EnemyHead -> if (arenaCell !is ArenaCell.Head || arenaCell.snek == self) return false
                CellType.EnemyTail -> if (arenaCell !is ArenaCell.Tail || arenaCell.snek == self) return false
                CellType.EnemyBody -> if (arenaCell !is ArenaCell.Body || arenaCell.snek == self) return false
                CellType.Wall -> if (arenaCell != ArenaCell.Wall) return false
                else -> throw IllegalStateException("Unknown cell pattern $patternCell")
            }
            hadMatch = true
        }
        return hadMatch
    }

    private fun rotateX(patternX: Int, patternY: Int, direction: Int): Int = when (direction) {
        0 -> patternX // up
        1 -> -patternY // right
        2 -> -patternX // down
        3 -> patternY // left
        else -> throw IllegalStateException("Invalid rotation direction")
    }

    private fun rotateY(patternX: Int, patternY: Int, direction: Int): Int = when (direction) {
        0 -> patternY // up
        1 -> patternX // right
        2 -> -patternY // down
        3 -> -patternX // left
        else -> throw IllegalStateException("Invalid rotation direction")
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
            if (cell == CellType.OwnHead) {
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
        data.forEach { hash = hash * 13 + it.hashCode() }
        return hash
    }

    fun dumpRow(rowIndex: Int) {
        print("|")
        val shift = rowIndex * width
        data.sliceArray(shift until shift + width).forEach { cellType ->
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
                else -> throw UnsupportedOperationException("Unknown CellType $cellType")
            }
            print(char)
        }
        print("|")
        print("  ")
    }

    fun copy(): SnekPattern = SnekPattern(width, height, data.copyOf())

    companion object {
        private fun defaultData(width: Int, height: Int): IntArray {
            val data = IntArray(width * height) { CellType.None }
            data[data.size / 2] = CellType.OwnHead
            return data
        }
    }
}

object CellType {
    val None = 0
    val Empty = 1
    val OwnHead = 2
    val OwnTail = 3
    val OwnBody = 4
    val EnemyHead = 5
    val EnemyTail = 6
    val EnemyBody = 7
    val Wall = 8

    fun values() = intArrayOf(None, Empty, OwnHead, OwnTail, OwnBody, EnemyHead, EnemyTail, EnemyBody, Wall)
}