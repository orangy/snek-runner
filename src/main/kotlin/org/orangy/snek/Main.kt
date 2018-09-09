package org.orangy.snek

import kotlin.system.*

val xdirections = intArrayOf(0, 1, 0, -1)
val ydirections = intArrayOf(-1, 0, 1, 0)

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
        pattern("""
            |    
            |    
            |       Th
            |       H    
            |    
            |    
            |    
            """.trimMargin("|"))
        pattern("""
            |    
            |       h
            |       T
            |       H    
            |    
            |    
            |    
            """.trimMargin("|"))
        pattern("""
            |    
            |       
            |      ...
            |       H    
            |    
            |    
            |    
            """.trimMargin("|"))
    }

    val sneks = listOf(
            Snek(0, "First", brain1),
            Snek(1, "Second", brain1),
            Snek(2, "Third", brain1),
            Snek(3, "Forth", brain1)
    )
    repeat(100) {
        val arena = Arena(28, 28)
        val positions = sneks.mapIndexed { index, snek -> arena.appendStartPosition(snek, index, sneks.size) }
        //arena.print()

        var rounds = 0
        val time = measureNanoTime {
            rounds = simulate(sneks, arena, 1000)
        }

        println("Played $rounds rounds in $time ns.")
    }
}

fun simulate(sneks: List<Snek>, arena: Arena, rounds: Int): Int {
    repeat(rounds) { round ->
        var allStuck = true
        sneks.forEach { snek ->
            val direction = arena.selectDirection(snek)
            if (direction != -1) {
                arena.move(snek, direction)
                allStuck = false
            } 
        }
        if (allStuck) {
            return round    
        }
    }
    return rounds
}

fun Arena.appendStartPosition(snek: Snek, index: Int, numberOfSneks: Int): SnekPosition {
    val dx = xdirections[index]
    val dy = ydirections[index]
    val headX = width / 2 + dx * 2
    val headY = height / 2 + dy * 2
    val length = 10
    // We allocate arrays for maximum length to save on array reallocations
    val xs = IntArray(length * numberOfSneks) { headX + it * dx }
    val ys = IntArray(length * numberOfSneks) { headY + it * dy }
    val position = SnekPosition(snek, length, xs, ys)
    append(snek, position)
    return position
}


