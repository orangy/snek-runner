package orangy.snek

import orangy.snek.SnekDirection.Down
import orangy.snek.SnekDirection.Left
import orangy.snek.SnekDirection.Right
import orangy.snek.SnekDirection.Up
import orangy.snek.SnekDirection.clockwise
import orangy.snek.SnekDirection.counterClockwise
import orangy.snek.SnekDirection.dx
import orangy.snek.SnekDirection.dy
import orangy.snek.SnekDirection.opposite
import kotlin.test.*

class DirectionsTest {
    @Test
    fun rotation() {
        assertEquals(Up, clockwise(Left))
        assertEquals(Right, clockwise(Up))
        assertEquals(Down, clockwise(Right))
        assertEquals(Left, clockwise(Down))

        assertEquals(Up, counterClockwise(Right))
        assertEquals(Right, counterClockwise(Down))
        assertEquals(Down, counterClockwise(Left))
        assertEquals(Left, counterClockwise(Up))

        assertEquals(Up, opposite(Down))
        assertEquals(Right, opposite(Left))
        assertEquals(Down, opposite(Up))
        assertEquals(Left, opposite(Right))
    }

    @Test
    fun increment() {
        val x = 10
        val y = 10

        assertEquals(10, x + dx(Up))
        assertEquals(9, y + dy(Up))
        
        assertEquals(10, x + dx(Down))
        assertEquals(11, y + dy(Down))

        assertEquals(9, x + dx(Left))
        assertEquals(10, y + dy(Left))

        assertEquals(11, x + dx(Right))
        assertEquals(10, y + dy(Right))
    }
}