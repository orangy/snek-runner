package org.orangy.snek

import kotlin.system.*

val xDirections = intArrayOf(0, 1, 0, -1)
val yDirections = intArrayOf(-1, 0, 1, 0)

val totalGames = 3000
val roundsPerGame = 1000
val gamesPerSnek = 100
val populationSize = 100
val generations = 1000
val participants = 2

val sneks = listOf(
        Snek("A", brain1),
        Snek("B", brain2),
        Snek("C", brain2),
        Snek("D", brain3)
)

fun main(args: Array<String>) {
    //play(sneks)
    val original = listOf(snekBrain {}).map { Snek(randomName(), it) }
    var sneks = original
    repeat(generations) {
        println("Generation #$it of $generations")
        sneks = skirmish(sneks)
    }

    sneks.take(10).forEach {
        println("========")
        println(it.name)
        it.brain.dump()
        println()
    }

}

fun skirmish(sneks: List<Snek>): List<Snek> {
    val candidates = buildCandidates(sneks)
    val games = candidates.size * gamesPerSnek / 4
    println("Simulating skirmish of ${candidates.size} sneks by playing $games games")
    val start = System.nanoTime()

    val timings = (0..games).map { game ->
        val arena = Arena(28, 28)
        val arenaSneks = mutableListOf<Snek>()
        repeat(participants) {
            arenaSneks.add(candidates[random.nextInt(candidates.size)])
        }

        arenaSneks.mapIndexed { index, snek -> arena.appendStartPosition(snek, index, arenaSneks.size) }

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

/*
    sneks.forEach { snek ->
        print(" ")
        println("Average length of `${snek.name}`: ${snekStatuses[snek]?.toInt()}")
    }
*/

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

val dictionary = "qwertyuiopasdfghjklzxcvbnm"
fun randomName() = (0..5).joinToString("") { dictionary[random.nextInt(dictionary.length)].toString() }

fun buildCandidates(sneks: List<Snek>): List<Snek> {
    val candidates = mutableListOf<Snek>()
    candidates.addAll(sneks)

    val brains = sneks.map { it.brain }
    brains.forEachIndexed { index, brain ->
        candidates.add(Snek(randomName(), mutate(mutate(brain))))
    }

    brains.forEachIndexed { index, brain ->
        candidates.add(Snek(randomName(), swap(brain)))
    }

    repeat(brains.size) {
        val ia = random.nextInt(brains.size)
        val ib = random.nextInt(brains.size)
        candidates.add(Snek(randomName(), cross(brains[ia], brains[ib])))
    }

    return candidates.distinctBy { hash(it.brain) }
}

fun hash(brain: SnekBrain): Int {
    var hash = 0
    brain.patterns.forEach {
        it.data.forEach {
            it.forEach {
                hash = hash * 13 + it.hashCode()
            }
        }
    }
    return hash
}

fun play(sneks: List<Snek>) {
    val arena = Arena(28, 28)
    sneks.mapIndexed { index, snek -> arena.appendStartPosition(snek, index, sneks.size) }
    val result = simulate(sneks, arena, 1000) {
        //readLine()
    }
    arena.print()
    println("Played ${result.rounds} rounds")

    result.status.sneks.forEach {
        println("Snek `${it.snek.name}` length: ${it.length} ${if (it.dead) "(dead)" else ""}")
    }
}

fun benchmark(sneks: List<Snek>) {
    val start = System.nanoTime()

    val timings = (0..totalGames).map {
        val arena = Arena(28, 28)
        sneks.mapIndexed { index, snek -> arena.appendStartPosition(snek, index, sneks.size) }

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

/*
Simulated 10000 games of 1000 rounds each in 43sec
Average 4325ns per round
Average 4ms per game
Average 997 rounds per game
 */

data class SimulationResult(val rounds: Int, val status: ArenaStatus)

fun simulate(sneks: List<Snek>, arena: Arena, rounds: Int, callback: (Arena.() -> Unit)?): SimulationResult {
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
            return SimulationResult(round, arena.status())
        }
        if (callback != null)
            arena.callback()
    }
    return SimulationResult(rounds - 1, arena.status())
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


