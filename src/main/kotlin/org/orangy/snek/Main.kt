package org.orangy.snek

import kotlin.system.*

val xDirections = intArrayOf(0, 1, 0, -1)
val yDirections = intArrayOf(-1, 0, 1, 0)

fun main(args: Array<String>) {
    val brain1 = snekBrain {
        pattern("""|
            |    
            |    
            |   t
            |   H
            |    
            |    
            """.trimMargin("|"))
        pattern("""|
            |    
            |    
            |   Th
            |   H
            |    
            |    
            """.trimMargin("|"))
        pattern("""|
            |    
            |   h
            |   T
            |   H 
            |    
            |    
            """.trimMargin("|"))
        pattern("""|
            |    
            |  
            | ...
            |  H    
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

    val totalGames = 10000
    val roundsPerGame = 1000
    val start = System.nanoTime()

    val timings = (0..totalGames).map {
        val arena = Arena(28, 28)
        sneks.mapIndexed { index, snek -> arena.appendStartPosition(snek, index, sneks.size) }

        var rounds = 0
        val time = measureNanoTime {
            rounds = simulate(sneks, arena, roundsPerGame)
        }

        //println("Played $rounds rounds in $time ns.")
        time / rounds
    }
    
    val totalTime = System.nanoTime() - start

    println("Simulated $totalGames games of $roundsPerGame rounds each in ${totalTime/1000/1000/1000}sec")
    println("Average ${timings.average().toLong()}ns per round")

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
    return rounds - 1
}

fun Arena.appendStartPosition(snek: Snek, index: Int, numberOfSneks: Int): SnekPosition {
    val dx = xDirections[index]
    val dy = yDirections[index]
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


