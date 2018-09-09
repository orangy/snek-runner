package org.orangy.snek

class Arena(val width: Int, val height: Int) {
    private val data = Array(height) { y -> Array<ArenaCell>(width) { x -> ArenaCell.Empty } }
    private val sneks = mutableMapOf<Snek, SnekPosition>()

    init {
        repeat(width) {
            data[it][0] = ArenaCell.Wall
            data[it][height - 1] = ArenaCell.Wall
        }
        repeat(height) {
            data[0][it] = ArenaCell.Wall
            data[width - 1][it] = ArenaCell.Wall
        }
    }

    fun print() {
        data.forEach { println(it.joinToString("")) }
    }

    fun append(snek: Snek, position: SnekPosition) {
        val index = sneks.size
        data[position.y(0)][position.x(0)] = ArenaCell.Head(snek)
        data[position.y(9)][position.x(9)] = ArenaCell.Tail(snek)
        (1..8).forEach { data[position.y(it)][position.x(it)] = ArenaCell.Body(snek) }
        sneks[snek] = position
    }
    
    fun move(snek: Snek, direction: Int) {
        val (dx, dy) = directions[direction]
        val position = sneks[snek]!!
        position.move(dx, dy)
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