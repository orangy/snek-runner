package org.orangy.snek


fun main(args: Array<String>) {
    val brain1 = snekBrain {
        pattern("""
            |    
            |    
            |       t
            |       H    
            |    
            |    
            |    
            """.trimMargin("|"))
    }

    val snek1 = Snek(0, "First", brain1)
    val snek2 = Snek(1, "Second", brain1)
    val arena = Arena(28, 28)
    val pos1 = arena.appendStartPosition(snek1, 0)
    arena.print()

/*
    repeat(3) {
        repeat(3) { pos1.move(0, 1) }
        repeat(3) { pos1.move(1, 0) }
        repeat(3) { pos1.move(0, -1) }
        repeat(3) { pos1.move(-1, 0) }
    }
*/
    Arena(28, 28).apply {
        append(snek1, pos1)
        print()
    }

    //val result = fight(snek1, snek2)
}

fun Arena.appendStartPosition(snek: Snek, index: Int): SnekPosition {
    val (dx, dy) = directions[index]
    val headX = width / 2 + dx * 2
    val headY = height / 2 + dy * 2
    val xs = (0..10).map { headX + it * dx }.toIntArray()
    val ys = (0..10).map { headY + it * dy }.toIntArray()
    val position = SnekPosition(snek, xs, ys)
    append(snek, position)
    return position
}

val directions = listOf(0 to -1, 1 to 0, 0 to 1, -1 to 0)

class SnekPosition(val snek: Snek, private var x: IntArray, private var y: IntArray) {
    private var length = x.size
    private var headIndex = 0
    private var tailIndex = length - 1

    fun x(index: Int): Int = x[(headIndex + index) % length]
    fun y(index: Int): Int = y[(headIndex + index) % length]

    fun headX() = x[headIndex]
    fun headY() = y[headIndex]
    fun tailX() = x[tailIndex]
    fun tailY() = y[tailIndex]

    fun length() = length

    fun grow(dx: Int, dy: Int) {


    }

    fun move(dx: Int, dy: Int) {
        val headX = x(0) + dx
        val headY = y(0) + dy
        headIndex = (headIndex + length - 1) % length
        x[headIndex] = headX
        y[headIndex] = headY
        tailIndex = (tailIndex + length - 1) % length
    }
}

fun fight(vararg sneks: Snek): Any {
    TODO()
}


class Snek(val id: Int, val name: String, val pattern: SnekBrain) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Snek) return false
        return id == other.id
    }

    override fun hashCode(): Int = id
}