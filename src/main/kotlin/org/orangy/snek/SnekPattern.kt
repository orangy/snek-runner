package org.orangy.snek

class SnekPattern(val width: Int, val height: Int, private val data: Array<CellType> = defaultData(width, height)) {
    val headX: Int
    val headY: Int

    init {
        val head = findHead()
        headX = head.first
        headY = head.second
    }

    operator fun get(x: Int, y: Int): CellType = data[y * width + x]
    operator fun set(x: Int, y: Int, value: CellType) {
        data[y * width + x] = value
    }

    fun match(arena: Arena, x: Int, y: Int, direction: Int, self: Snek, mirror: Boolean): Boolean {
        var hadMatch = false
        data.forEachIndexed cell@{ patternIndex, patternCell ->
            if (patternCell == CellType.None || patternCell == CellType.OwnHead)
                return@cell // cell without a match or matching own head, ignore it
            val patternX = patternIndex % width
            val dx = if (mirror) headX - patternX else patternX - headX
            val dy = patternIndex / width - headY
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
            }
            print(char)
        }
        print("|")
        print("  ")
    }

    fun copy(): SnekPattern = SnekPattern(width, height, data.copyOf())

    companion object {
        private fun defaultData(width: Int, height: Int): Array<CellType> {
            val data = Array(width * height) { CellType.None }
            data[data.size / 2] = CellType.OwnHead
            return data
        }
    }
}


enum class CellType {
    None, Empty, OwnHead, OwnTail, OwnBody, EnemyHead, EnemyTail, EnemyBody, Wall
}