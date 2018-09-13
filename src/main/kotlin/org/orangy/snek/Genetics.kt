package org.orangy.snek

import kotlin.system.*

val brainWidth = 7
val brainHeight = 7
val gamesPerSnek = 100
val populationSize = 100
val generations = 1000
val participants = 2

fun main(args: Array<String>) {
    val original = listOf(snekBrain(brainWidth, brainHeight) {}).map { Snek(randomName(), it) }
    var sneks = original
    repeat(generations) {
        println("Generation #$it of $generations")
        sneks = population(sneks)
    }

    sneks.take(10).forEach {
        println("========")
        println(it.name)
        it.brain.dump()
        println()
    }
}

fun population(sneks: List<Snek>): List<Snek> {
    val candidates = buildCandidates(sneks)
    val games = candidates.size * gamesPerSnek / 4
    println("Simulating skirmish of ${candidates.size} sneks by playing $games games")
    val start = System.nanoTime()

    val timings = (0..games).map {
        val arenaSneks = mutableListOf<Snek>()
        repeat(participants) {
            arenaSneks.add(candidates[random.nextInt(candidates.size)])
        }
        
        val arena = when (participants) {
            in 1..2 -> Arena(17, 17).also {
                arenaSneks.mapIndexed { index, snek -> it.appendDuelPosition(snek, index, arenaSneks.size) }
            }
            in 3..4 -> Arena(28, 28).also {
                arenaSneks.mapIndexed { index, snek -> it.appendSkirmishPosition(snek, index, arenaSneks.size) }
            }
            else -> throw UnsupportedOperationException()
        }
        
        var result: SimulationResult? = null
        val time = measureNanoTime {
            result = simulate(arenaSneks, arena, roundsPerGame, null)
        }
        time to result!!
    }

    val totalTime = System.nanoTime() - start

    println("Simulated $games games of $roundsPerGame rounds each in ${totalTime / 1000 / 1000 / 1000}sec")
    println("Average ${timings.map { it.first / it.second.rounds }.average().toLong()}ns per round")
    println("Average ${timings.map { it.first }.average().toLong() / 1000000}ms per game")
    println("Average ${timings.map { it.second.rounds }.average().toLong()} rounds per game")

    val sneksResults = timings.flatMap { it.second.status.sneks }.groupBy { it.snek }
    val sneksRounds = sneksResults.map { it.value.size }.average().toInt()
    println("Average $sneksRounds games per snek")

    val snekAverages = sneksResults.mapValues { it.value.map { it.length }.average() }
    val notplayed = sneks.filter { it !in snekAverages }.count()
    println("Sneks didn't play: $notplayed")

    val topSneks = snekAverages.toList().sortedByDescending { it.second }.take(populationSize)

    val survived = topSneks.count { it.first in sneks }
    println("Sneks survived: $survived of ${sneks.size}")

    println()
    topSneks.take(10).forEach { (snek, length) ->
        if (snek in sneks)
            print("* ")
        else
            print("  ")
        val minLength = sneksResults[snek]!!.minBy { it.length }!!.length
        val maxLength = sneksResults[snek]!!.maxBy { it.length }!!.length
        println("Average length of `${snek.name}`: ${length.toInt()} [$minLength..$maxLength]")
    }

    topSneks.take(3).forEach {
        println(it.first.name)
        it.first.brain.dump()
        println()
    }
    return topSneks.map { it.first }
}

fun buildCandidates(sneks: List<Snek>): List<Snek> {
    val candidates = mutableListOf<Snek>()
    candidates.addAll(sneks)

    val brains = sneks.map { it.brain }
    brains.forEach { brain ->
        candidates.add(Snek(randomName(), mutate(mutate(brain))))
    }

    brains.forEach { brain ->
        candidates.add(Snek(randomName(), swap(brain)))
    }

    repeat(brains.size) {
        val ia = random.nextInt(brains.size)
        val ib = random.nextInt(brains.size)
        candidates.add(Snek(randomName(), cross(brains[ia], brains[ib])))
    }

    return candidates.distinctBy { it.brain.hash() }
}
