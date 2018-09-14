package orangy.snek

class SnekPosition(val snek: Snek, private var length: Int, private var x: IntArray, private var y: IntArray) {
    private var headIndex = 0
    private var tailIndex = length - 1
    private var dead = false

    fun x(index: Int): Int = x[(headIndex + index) % x.size]
    fun y(index: Int): Int = y[(headIndex + index) % x.size]

    fun headX() = x[headIndex]
    fun headY() = y[headIndex]
    fun tailX() = x[tailIndex]
    fun tailY() = y[tailIndex]

    fun length() = length
    fun isDead() = dead

    fun shrink() {
        length--
        tailIndex--
        if (tailIndex < 0)
            tailIndex = x.size - 1
    }

    fun grow(dx: Int, dy: Int) {
        val headX = headX() + dx
        val headY = headY() + dy
        length++
        headIndex--
        if (headIndex < 0)
            headIndex = x.size - 1
        x[headIndex] = headX
        y[headIndex] = headY
    }

    fun move(dx: Int, dy: Int) {
        val headX = headX() + dx
        val headY = headY() + dy
        headIndex = (headIndex + x.size - 1) % x.size
        x[headIndex] = headX
        y[headIndex] = headY
        tailIndex = (tailIndex + x.size - 1) % x.size
    }

    fun die() {
        dead = true
    }
}