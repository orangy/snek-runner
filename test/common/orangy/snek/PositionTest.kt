package orangy.snek

import kotlin.test.*

class PositionTest {
    private val snek = Snek("test", SnekBrain(7, 7, listOf()))

    @Test fun move() {
        val position = SnekPosition(snek, 3, intArrayOf(10, 10, 10, 0, 0), intArrayOf(9, 10, 11, 0, 0), SnekDirection.Up)
        
        position.move(SnekDirection.Left)
        assertEquals(9, position.headX())
        assertEquals(9, position.headY())
        assertEquals(10, position.tailX())
        assertEquals(10, position.tailY())

        position.move(SnekDirection.Up)
        assertEquals(9, position.headX())
        assertEquals(8, position.headY())
        assertEquals(10, position.tailX())
        assertEquals(9, position.tailY())
        
        repeat(6) {
            position.move(SnekDirection.Up)
        }
        assertEquals(9, position.headX())
        assertEquals(2, position.headY())
        assertEquals(9, position.tailX())
        assertEquals(4, position.tailY())
    }
    
    @Test fun grow() {
        val position = SnekPosition(snek, 3, intArrayOf(10, 10, 10, 0), intArrayOf(9, 10, 11, 0), SnekDirection.Up)
        position.grow(SnekDirection.Left)
        assertEquals(9, position.headX())
        assertEquals(9, position.headY())
        assertEquals(10, position.tailX())
        assertEquals(11, position.tailY())
    }
    
    @Test fun shrink() {
        val position = SnekPosition(snek, 3, intArrayOf(10, 10, 10, 0), intArrayOf(9, 10, 11, 0), SnekDirection.Up)
        position.shrink()
        assertEquals(10, position.headX())
        assertEquals(9, position.headY())
        assertEquals(10, position.tailX())
        assertEquals(10, position.tailY())
    }
    
    @Test fun mixed() {
        val position = SnekPosition(snek, 3, intArrayOf(10, 10, 10, 0), intArrayOf(9, 10, 11, 0), SnekDirection.Up)
        position.shrink()
        assertEquals(10, position.headX())
        assertEquals(9, position.headY())
        assertEquals(10, position.tailX())
        assertEquals(10, position.tailY())

        position.grow(SnekDirection.Left)
        assertEquals(9, position.headX())
        assertEquals(9, position.headY())
        assertEquals(10, position.tailX())
        assertEquals(10, position.tailY())

        repeat(6) {
            position.move(SnekDirection.Up)
        }
        assertEquals(9, position.headX())
        assertEquals(3, position.headY())
        assertEquals(9, position.tailX())
        assertEquals(5, position.tailY())
    }
}