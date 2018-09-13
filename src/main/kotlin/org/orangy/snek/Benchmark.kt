package org.orangy.snek

import kotlin.system.*

private val totalGames = 10000

fun main(args: Array<String>) {
    val sneks = listOf(
            Snek("A", brain1),
            Snek("B", brain2),
            Snek("C", brain2),
            Snek("D", brain3)
    )

    val start = System.nanoTime()

    val timings = (0..totalGames).map {
        val arena = Arena(28, 28)
        sneks.mapIndexed { index, snek -> arena.appendSkirmishPosition(snek, index, sneks.size) }

        var result: SimulationResult? = null
        val time = measureNanoTime {
            result = simulate(sneks, arena, roundsPerGame, null)
        }
        time to result!!
    }

    val totalTime = System.nanoTime() - start

    println("Simulated $totalGames games of $roundsPerGame rounds each in ${totalTime / 1000 / 1000 / 1000}sec")
    println("Average ${timings.map { it.first / it.second.rounds }.average().toLong()}ns per round")
    println("Average ${timings.map { it.first }.average().toLong() / 1000000}ms per game")
    println("Average ${timings.map { it.second.rounds }.average().toLong()} rounds per game")

    sneks.forEach { snek ->
        println("Average length of `${snek.name}`: ${timings.map { it.second.status.sneks.single { it.snek == snek }.length }.average().toInt()}")
    }
}
