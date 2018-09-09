package org.orangy.snek

class SnekPattern(val data: Array<Array<CellType>>) {
    val headX: Int
    val headY: Int

    init {
        val head = findHead(data)
        headX = head.first
        headY = head.second
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

class SnekPatternBuilder

enum class CellType {
    None, Empty, OwnHead, OwnTail, OwnBody, EnemyHead, EnemyTail, EnemyBody
}