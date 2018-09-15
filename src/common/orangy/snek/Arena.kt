package orangy.snek

import kotlin.random.*

val random = Random(1/*nanoTime()*/)

class Arena(val width: Int, val height: Int) {
    private val cells = Array(height) { Array<ArenaCell>(width) { ArenaCell.Empty } }
    private val positions = mutableListOf<SnekPosition>()

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

    fun simulate(rounds: Int, callback: (Arena.(Int) -> Unit)?): SimulationResult {
        repeat(rounds) { round ->
            var allStuck = true
            positions.forEach { position ->
                //print("Move: ${position.snek.name}")
                val direction = position.snek.brain.selectDirection(this, position)
                if (direction != -1) {
                    move(position, direction)
                    allStuck = false
                }
                //println()
            }
            if (allStuck) {
                return SimulationResult(round, status())
            }
            if (callback != null)
                callback(round)
        }
        return SimulationResult(rounds - 1, status())
    }

    fun dump() {
        cells.forEach { println(it.joinToString("")) }
    }

    operator fun get(x: Int, y: Int): ArenaCell = cells[y][x]
    operator fun set(x: Int, y: Int, value: ArenaCell) {
        cells[y][x] = value
    }

    fun append(snek: Snek, position: SnekPosition) {
        this[position.headX(), position.headY()] = snek.HeadCell
        this[position.tailX(), position.tailY()] = snek.TailCell
        (1..(position.length() - 2)).forEach { this[position.x(it), position.y(it)] = snek.BodyCell }
        positions.add(position)
    }

    fun move(position: SnekPosition, direction: Int) {
        val dx = SnekDirection.dx(direction)
        val dy = SnekDirection.dy(direction)
        val target = this[position.headX() + dx, position.headY() + dy]
        val snek = position.snek
        if (target is ArenaCell.Tail && target.snek != snek) {
            // eat
            val targetPosition = positions.first { it.snek == target.snek }
            targetPosition.shrink()
            if (targetPosition.length() < 2) {
                // it's dead!
                this[targetPosition.headX(), targetPosition.headY()] = ArenaCell.Empty
                targetPosition.die()
            } else {
                this[targetPosition.tailX(), targetPosition.tailY()] = target.snek.TailCell
            }

            this[position.headX(), position.headY()] = snek.BodyCell
            position.grow(dx, dy, direction)
            this[position.headX(), position.headY()] = snek.HeadCell
        } else {
            // move
            this[position.headX(), position.headY()] = snek.BodyCell
            this[position.tailX(), position.tailY()] = ArenaCell.Empty
            position.move(dx, dy, direction)
            this[position.headX(), position.headY()] = snek.HeadCell
            this[position.tailX(), position.tailY()] = snek.TailCell
        }
    }

    fun status(): ArenaStatus = ArenaStatus(positions.map {
        SnekStatus(it.snek, it.length(), it.isDead())
    })
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
        override fun toString() = snek.name[0].toString()
    }

    class Tail(val snek: Snek) : ArenaCell() {
        override fun toString() = "○"
    }
}

fun Arena.startSkirmishPosition(snek: Snek, index: Int, numberOfSneks: Int): SnekPosition {
    val dx = SnekDirection.dx(index)
    val dy = SnekDirection.dy(index)
    val headX = width / 2 + dx * 2
    val headY = height / 2 + dy * 2
    val length = 10
    // We allocate arrays for maximum length to save on array reallocations
    val xs = IntArray(length * numberOfSneks) { headX + it * dx }
    val ys = IntArray(length * numberOfSneks) { headY + it * dy }
    val position = SnekPosition(snek, length, xs, ys, SnekDirection.opposite(index))
    append(snek, position)
    return position
}

fun Arena.startDuelPosition(snek: Snek, index: Int, numberOfSneks: Int): SnekPosition {
    val dx = SnekDirection.dx(index * 2)
    val dy = SnekDirection.dy(index * 2)
    val headX = width / 2 + dy * 6
    val headY = height / 2 - dy * 3
    val length = 10
    // We allocate arrays for maximum length to save on array reallocations
    val xs = IntArray(length * numberOfSneks) { headX + it * dx }
    val ys = IntArray(length * numberOfSneks) { headY + it * dy }
    val position = SnekPosition(snek, length, xs, ys, SnekDirection.opposite(index * 2))
    append(snek, position)
    return position
}
