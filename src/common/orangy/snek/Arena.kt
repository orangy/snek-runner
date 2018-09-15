package orangy.snek

import kotlin.random.*

val random = Random(nanoTime())

class Arena(val width: Int, val height: Int) {
    private val cells = Array(height) { Array<ArenaCell>(width) { ArenaCell.Empty } }
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

    fun selectDirection(snek: Snek): Int {
        val position = sneks[snek]!!
        if (position.isDead()) // dead cannot dance
            return -1

        val headX = position.headX()
        val headY = position.headY()
        val possibleDirections = (position.direction()..position.direction() + 3).mapNotNull { shiftedDirection ->
            val direction = shiftedDirection % 4
            val dx = xDirections[direction]
            val dy = yDirections[direction]
            val cellValue = this[headX + dx, headY + dy]
            when (cellValue) {
                ArenaCell.Empty -> direction
                is ArenaCell.Tail -> {
                    if (cellValue.snek == snek) { // can go to own tail?
                        if (position.length() < 3)
                            null // if it's of length 2, then no, cause it would flip
                        else
                            direction // lengthy sneks can go into onw tail
                    } else {
                        direction // other's tail, go!
                    }
                }
                else -> null
            }
        }

        snek.brain.patterns.forEach { pattern ->
            val matchingDirections = possibleDirections.filter { direction ->
                pattern.match(this, headX, headY, direction, snek, false) ||
                        pattern.match(this, headX, headY, direction, snek, true)
            }
            if (matchingDirections.isEmpty())
                return@forEach // next pattern
            return matchingDirections[random.nextInt(matchingDirections.size)]
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
        val dx = xDirections[direction]
        val dy = yDirections[direction]
        val position = sneks[snek]!!
        val target = this[position.headX() + dx, position.headY() + dy]
        if (target is ArenaCell.Tail && target.snek != snek) {
            // eat
            val targetPosition = sneks[target.snek]!!
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

    fun status(): ArenaStatus = ArenaStatus(sneks.map {
        SnekStatus(it.key, it.value.length(), it.value.isDead())
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
    val dx = xDirections[index]
    val dy = yDirections[index]
    val headX = width / 2 + dx * 2
    val headY = height / 2 + dy * 2
    val length = 10
    // We allocate arrays for maximum length to save on array reallocations
    val xs = IntArray(length * numberOfSneks) { headX + it * dx }
    val ys = IntArray(length * numberOfSneks) { headY + it * dy }
    val position = SnekPosition(snek, length, xs, ys, (index + 2) % 4)
    append(snek, position)
    return position
}

fun Arena.startDuelPosition(snek: Snek, index: Int, numberOfSneks: Int): SnekPosition {
    val dx = xDirections[index * 2]
    val dy = yDirections[index * 2]
    val headX = width / 2 + dy * 6
    val headY = height / 2 - dy * 3
    val length = 10
    // We allocate arrays for maximum length to save on array reallocations
    val xs = IntArray(length * numberOfSneks) { headX + it * dx }
    val ys = IntArray(length * numberOfSneks) { headY + it * dy }
    val position = SnekPosition(snek, length, xs, ys, (1 - index) * 2)
    append(snek, position)
    return position
}
