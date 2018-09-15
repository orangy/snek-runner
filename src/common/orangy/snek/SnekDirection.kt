package orangy.snek

object SnekDirection {
    fun opposite(direction: Int): Int = (direction + 2) and 3
    fun clockwise(direction: Int): Int = (direction + 1) and 3
    fun counterClockwise(direction: Int): Int = (direction + 3) and 3
    fun dx(direction: Int) = dxValues[direction]
    fun dy(direction: Int) = dyValues[direction]

    val dxValues = intArrayOf(0, 1, 0, -1)
    val dyValues = intArrayOf(-1, 0, 1, 0)

    const val Up = 0
    const val Right = 1
    const val Down = 2
    const val Left = 3

    const val None = -1
}
