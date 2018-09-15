package orangy.snek

import kotlin.test.*

class ArenaTest {

    @Test
    fun placementDuel() {
        val snek1 = Snek("1", SnekBrain(7, 7, listOf()))
        val snek2 = Snek("2", SnekBrain(7, 7, listOf()))
        val arena = Arena(17, 17).also {
            it.startDuelPosition(snek1, 0, 2)
            it.startDuelPosition(snek2, 1, 2)
        }
        assertEquals("""
█████████████████
█               █
█ ○             █
█ 1             █
█ 1             █
█ 1           @ █
█ 1           2 █
█ 1           2 █
█ 1           2 █
█ 1           2 █
█ 1           2 █
█ @           2 █
█             2 █
█             2 █
█             ○ █
█               █
█████████████████
""".trimIndent(), arena.text())
    }

    @Test
    fun moves() {
        val snek1 = Snek("1", SnekBrain(7, 7, listOf()))
        val snek2 = Snek("2", SnekBrain(7, 7, listOf()))
        val arena = Arena(17, 17)
        val pos1 = arena.startDuelPosition(snek1, 0, 2)
        val pos2 = arena.startDuelPosition(snek2, 1, 2)

        arena.move(pos1, SnekDirection.Right)
        arena.move(pos2, SnekDirection.Left)
        arena.move(pos1, SnekDirection.Right)
        arena.move(pos2, SnekDirection.Left)
        arena.move(pos1, SnekDirection.Up)
        arena.move(pos2, SnekDirection.Up)
        assertEquals("""
█████████████████
█               █
█               █
█               █
█           @   █
█ ○         222 █
█ 1           2 █
█ 1           2 █
█ 1           2 █
█ 1           2 █
█ 1 @         2 █
█ 111         ○ █
█               █
█               █
█               █
█               █
█████████████████
""".trimIndent(), arena.text())
    }

    @Test
    fun placementSkirmish() {
        val sneks = (0..3).map { Snek("$it", SnekBrain(7, 7, listOf())) }
        val arena = Arena(27, 27).also { arena ->
            sneks.forEachIndexed { index, snek ->
                arena.startSkirmishPosition(snek, index, sneks.size)
            }
        }
        assertEquals("""
███████████████████████████
█                         █
█            ○            █
█            0            █
█            0            █
█            0            █
█            0            █
█            0            █
█            0            █
█            0            █
█            0            █
█            @            █
█                         █
█ ○33333333@   @11111111○ █
█                         █
█            @            █
█            2            █
█            2            █
█            2            █
█            2            █
█            2            █
█            2            █
█            2            █
█            2            █
█            ○            █
█                         █
███████████████████████████
""".trimIndent(), arena.text())
    }

}