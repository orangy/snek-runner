package orangy.snek

import orangy.snek.SnekDirection.clockwise
import orangy.snek.SnekDirection.counterClockwise
import orangy.snek.SnekDirection.opposite
import kotlin.test.*

class DirectionsTest {
    @Test
    fun rotation() {
        assertEquals(SnekDirection.Up, clockwise(SnekDirection.Left))
        assertEquals(SnekDirection.Right, clockwise(SnekDirection.Up))
        assertEquals(SnekDirection.Down, clockwise(SnekDirection.Right))
        assertEquals(SnekDirection.Left, clockwise(SnekDirection.Down))

        assertEquals(SnekDirection.Up, counterClockwise(SnekDirection.Right))
        assertEquals(SnekDirection.Right, counterClockwise(SnekDirection.Down))
        assertEquals(SnekDirection.Down, counterClockwise(SnekDirection.Left))
        assertEquals(SnekDirection.Left, counterClockwise(SnekDirection.Up))

        assertEquals(SnekDirection.Up, opposite(SnekDirection.Down))
        assertEquals(SnekDirection.Right, opposite(SnekDirection.Left))
        assertEquals(SnekDirection.Down, opposite(SnekDirection.Up))
        assertEquals(SnekDirection.Left, opposite(SnekDirection.Right))
    }
}