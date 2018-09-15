package orangy.snek

import kotlin.test.*

class PatternTest {
    @Test
    fun pattern1() {
        val snek = Snek("1", SnekBrain(7, 7, listOf()))
        val arena = Arena(17, 17).apply {
            startCustomPosition(snek, 3, 3, "rrrddrddllu", 100)
            //dump()
        }

        with(SnekPattern.parse("""|  .
                                  | ..
                                  |  H    
                                  """.trimMargin("|"))) {
            assertTrue { match(arena, 3, 3, 0, snek, false) }
            assertTrue { match(arena, 3, 3, 0, snek, true) }
            assertFalse { match(arena, 3, 3, 1, snek, false) }
            assertFalse { match(arena, 3, 3, 1, snek, true) }
            assertTrue { match(arena, 3, 3, 2, snek, false) }
            assertTrue { match(arena, 3, 3, 2, snek, true) }
            assertTrue { match(arena, 3, 3, 3, snek, false) }
            assertTrue { match(arena, 3, 3, 3, snek, true) }
        }

        with(SnekPattern.parse("""|  
                                  |  B
                                  |  H    
                                  """.trimMargin("|"))) {
            assertFalse { match(arena, 3, 3, 0, snek, false) }
            assertFalse { match(arena, 3, 3, 0, snek, true) }
            assertTrue { match(arena, 3, 3, 1, snek, false) }
            assertTrue { match(arena, 3, 3, 1, snek, true) }
            assertFalse { match(arena, 3, 3, 2, snek, false) }
            assertFalse { match(arena, 3, 3, 2, snek, true) }
            assertFalse { match(arena, 3, 3, 3, snek, false) }
            assertFalse { match(arena, 3, 3, 3, snek, true) }
        }
        with(SnekPattern.parse("""|  
                                  |  BB
                                  |  B
                                  |  B
                                  |  H    
                                  """.trimMargin("|"))) {
            assertFalse { match(arena, 3, 3, 0, snek, false) }
            assertFalse { match(arena, 3, 3, 0, snek, true) }
            assertTrue { match(arena, 3, 3, 1, snek, false) }
            assertFalse { match(arena, 3, 3, 1, snek, true) }
            assertFalse { match(arena, 3, 3, 2, snek, false) }
            assertFalse { match(arena, 3, 3, 2, snek, true) }
            assertFalse { match(arena, 3, 3, 3, snek, false) }
            assertFalse { match(arena, 3, 3, 3, snek, true) }
        }
        with(SnekPattern.parse("""|  
                                  |   WW
                                  |   
                                  |   .
                                  |W  H    
                                  """.trimMargin("|"))) {
            assertTrue { match(arena, 3, 3, 0, snek, false) }
            assertFalse { match(arena, 3, 3, 0, snek, true) }
            assertFalse { match(arena, 3, 3, 1, snek, false) }
            assertFalse { match(arena, 3, 3, 1, snek, true) }
            assertFalse { match(arena, 3, 3, 2, snek, false) }
            assertFalse { match(arena, 3, 3, 2, snek, true) }
            assertFalse { match(arena, 3, 3, 3, snek, false) }
            assertTrue { match(arena, 3, 3, 3, snek, true) }
        }
    }
}