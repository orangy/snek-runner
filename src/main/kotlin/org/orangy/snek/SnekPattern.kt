package org.orangy.snek

class SnekPattern(val data: Array<Array<CellType>>) {
    val headX: Int
    val headY: Int

    init {
        val head = findHead(data)
        headX = head.first
        headY = head.second
    }

    fun match(arena: Arena, x: Int, y: Int, direction: Int, self: Snek): Boolean {
        data.forEachIndexed { patternY, patternRow ->
            patternRow.forEachIndexed cells@ { patternX, patternCell ->
                if (patternCell == CellType.None)
                    return@cells // cell without a match, ignore it
                val arenaX = x + rotateX(patternX - headX, patternY - headY, direction)
                val arenaY = y + rotateY(patternX - headX, patternY - headY, direction)
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
            }
        }
        return true
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

    private fun findHead(data: Array<Array<CellType>>): Pair<Int, Int> {
        var head: Pair<Int, Int>? = null
        data.forEachIndexed { y, row ->
            row.forEachIndexed { x, cell ->
                if (cell == CellType.OwnHead) {
                    if (head == null) {
                        head = x to y
                    } else {
                        throw UnsupportedOperationException("Pattern can't have more than one own heads")
                    }
                }
            }
        }
        if (head == null) {
            throw UnsupportedOperationException("Pattern should have own head")
        } else {
            return head!!
        }
    }
}

enum class CellType {
    None, Empty, OwnHead, OwnTail, OwnBody, EnemyHead, EnemyTail, EnemyBody, Wall
}