package org.orangy.snek

import java.util.*

private val random = Random(1)

class Arena(val width: Int, val height: Int) {
    private val cells = Array(height) { y -> Array<ArenaCell>(width) { x -> ArenaCell.Empty } }
    private val sneks = mutableMapOf<Snek, SnekPosition>()

    init {
        repeat(width) {
            cells[it][0] = ArenaCell.Wall
            cells[it][height - 1] = ArenaCell.Wall
        }
        repeat(height) {
            cells[0][it] = ArenaCell.Wall
            cells[width - 1][it] = ArenaCell.Wall
        }
    }

    fun print() {
        cells.forEach { println(it.joinToString("")) }
    }

    operator fun get(x: Int, y: Int): ArenaCell = cells[y][x]
    operator fun set(x: Int, y: Int, value: ArenaCell) {
        cells[y][x] = value
    }

    fun selectRandomDirection(snek: Snek): Int {
        val position = sneks[snek]!!
        val headX = position.headX()
        val headY = position.headY()
        val possibleDirections = (0..3).mapNotNull { direction ->
            val dx = xdirections[direction]
            val dy = ydirections[direction]
            val cellValue = this[headX + dx, headY + dy]
            when (cellValue) {
                ArenaCell.Empty -> direction
                is ArenaCell.Tail -> direction
                else -> null
            }
        }
        if (possibleDirections.isEmpty())
            return -1
        return possibleDirections[random.nextInt(possibleDirections.size)]
    }

    fun append(snek: Snek, position: SnekPosition) {
        this[position.headX(), position.headY()] = snek.HeadCell
        this[position.tailX(), position.tailY()] = snek.TailCell
        (1..(position.length() - 2)).forEach { this[position.x(it), position.y(it)] = snek.BodyCell }
        sneks[snek] = position
    }

    fun move(snek: Snek, direction: Int) {
        val dx = xdirections[direction]
        val dy = ydirections[direction]
        val position = sneks[snek]!!
        val target = this[position.headX() + dx, position.headY() + dy]
        if (target is ArenaCell.Tail && target.snek != snek) {
            // eat
            val targetPosition = sneks[target.snek]!!
            targetPosition.shrink()
            this[targetPosition.tailX(), targetPosition.tailY()] = target.snek.TailCell

            this[position.headX(), position.headY()] = snek.BodyCell
            position.grow(dx, dy)
            this[position.headX(), position.headY()] = snek.HeadCell
        } else {
            // move
            this[position.headX(), position.headY()] = snek.BodyCell
            this[position.tailX(), position.tailY()] = ArenaCell.Empty
            position.move(dx, dy)
            this[position.headX(), position.headY()] = snek.HeadCell
            this[position.tailX(), position.tailY()] = snek.TailCell
        }
    }
}

sealed class ArenaCell {
    object Empty : ArenaCell() {
        override fun toString() = " "
    }

    object Wall : ArenaCell() {
        override fun toString() = "█"
    }

    class Head(val snek: Snek) : ArenaCell() {
        override fun toString() = "@"
    }

    class Body(val snek: Snek) : ArenaCell() {
        override fun toString() = ('A'.toInt() + snek.id).toChar().toString()
    }

    class Tail(val snek: Snek) : ArenaCell() {
        override fun toString() = "○"
    }
}