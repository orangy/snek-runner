package orangy.snek

val brainWidth = 7
val brainHeight = 7
val gamesPerSnek = 100
val populationSize = 100
val generations = 1000
val participants = 2

fun main(args: Array<String>) {
    genetics()
}

fun genetics() {
    val original = listOf(snekBrain(brainWidth, brainHeight) {}).map { Snek(randomName(), it) }
    var sneks = original

    val start = nanoTime()
    repeat(generations) {
        if (it > 0) {
            val elapsedSeconds = (nanoTime() - start) / 1000 / 1000 / 1000
            val hours = elapsedSeconds / 3600
            val minutes = (elapsedSeconds % 3600) / 60
            val estimate = (elapsedSeconds.toDouble() * generations / it).toLong()
            val estHours = estimate / 3600
            val estMinutes = (estimate % 3600) / 60
            println("Generation #$it of $generations (${hours.padZero()}:${minutes.padZero()} of ${estHours.padZero()}:${estMinutes.padZero()})")
        }
        sneks = population(sneks)
        sneks.forEach { it.age++ }
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
    val games = candidates.size * gamesPerSnek / participants
    println("Simulating skirmish of ${candidates.size} sneks by playing $games games")

    val start = nanoTime()

    val timings = (0..games).map {
        val arenaSneks = mutableListOf<Snek>()
        repeat(participants) {
            arenaSneks.add(candidates[random.nextInt(candidates.size)])
        }

        val arena = when (participants) {
            in 1..2 -> Arena(17, 17).also {
                arenaSneks.mapIndexed { index, snek -> it.startDuelPosition(snek, index) }
            }
            in 3..4 -> Arena(28, 28).also {
                arenaSneks.mapIndexed { index, snek -> it.startSkirmishPosition(snek, index) }
            }
            else -> throw UnsupportedOperationException()
        }

        var result: SimulationResult? = null
        val time = measureNanoTime {
            result = arena.simulate(roundsPerGame, null)
        }
        time to result!!
    }

    val totalTime = nanoTime() - start

    val sneksResults = dumpStatistics(games, totalTime, timings)

    val snekAverages = sneksResults.mapValues { it.value.map { it.length }.average() }
/*
    val notplayed = sneks.filter { it !in snekAverages }.count()
    println("Sneks didn't play: $notplayed")
*/
    println("Max age: ${sneks.maxBy { it.age }?.age}g")

    val rating = snekAverages.toList().sortedByDescending { it.second }
    val topSneks = rating.take(populationSize)

    println()
    rating.take(3).forEach { it.first.dumpStatistics(sneksResults) }
    println("~~~~~~~~")
    rating.takeLast(3).forEach { it.first.dumpStatistics(sneksResults) }
    println()

    rating.take(3).forEach {
        println(it.first.name)
        it.first.brain.dump()
        println()
    }

    return topSneks.map { it.first }
}

fun List<Int>.median() = sorted().let { (it[it.size / 2] + it[(it.size - 1) / 2]) / 2 }

fun buildCandidates(sneks: List<Snek>): List<Snek> {
    val candidates = mutableListOf<Snek>()
    candidates.addAll(sneks)

    val brains = sneks.map { it.brain }

    brains.shuffled().take(populationSize / 2).forEach { brain ->
        candidates.add(Snek(randomName(), mutate(brain)))
    }
    brains.shuffled().take(populationSize / 4).forEach { brain ->
        candidates.add(Snek(randomName(), mutate(mutate(brain))))
    }
    brains.shuffled().take(populationSize / 8).forEach { brain ->
        candidates.add(Snek(randomName(), mutate(mutate(mutate(brain)))))
    }

    brains.shuffled().take(populationSize / 2).forEach { brain ->
        candidates.add(Snek(randomName(), swap(brain)))
    }

    repeat(populationSize / 2) {
        val ia = random.nextInt(brains.size)
        val ib = random.nextInt(brains.size)
        candidates.add(Snek(randomName(), cross(brains[ia], brains[ib])))
    }

    return candidates
}

fun cross(brain1: SnekBrain, brain2: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder(brain1.width, brain1.height)
    (0..(numPatterns - 1)).forEach { index ->
        val select = random.nextBoolean()
        if (select) {
            builder.pattern(brain1.patterns[index])
        } else {
            builder.pattern(brain2.patterns[index])
        }
    }
    return builder.build()
}

fun copy(brain: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder(brain.width, brain.height)
    (0..(numPatterns - 1)).forEach { index ->
        builder.pattern(brain.patterns[index])
    }
    return builder.build()
}

fun swap(brain: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder(brain.width, brain.height)
    val index1 = random.nextInt(numPatterns - 1)
    val index2 = random.nextInt(numPatterns - 1)
    (0..(numPatterns - 1)).forEach { index ->
        if (index == index1)
            builder.pattern(brain.patterns[index2])
        else if (index == index2)
            builder.pattern(brain.patterns[index1])
        else
            builder.pattern(brain.patterns[index])

    }
    return builder.build()
}

fun mutate(brain: SnekBrain): SnekBrain {
    val newbrain = copyOrEmpty(brain)
    val patternIndex = random.nextInt(newbrain.patterns.size)
    val pattern = newbrain.patterns[patternIndex]
    val x = random.nextInt(pattern.width)
    val y = random.nextInt(pattern.height)
    val type = SnekPattern.variants[random.nextInt(SnekPattern.variants.size)]
    if (pattern[x, y] != SnekPattern.OwnHead) { // can't override head
        val mode = when (type) {
            SnekPattern.None, SnekPattern.OwnHead -> 0
            else -> random.nextInt(3)
        }
        pattern[x, y] = (mode shl 4) or type
    }
    return newbrain
}

fun copyOrEmpty(brain: SnekBrain): SnekBrain {
    val builder = SnekBrainBuilder(brain.width, brain.height)
    (0..(numPatterns - 1)).forEach { index ->
        if (random.nextInt(200) == 1) {
            builder.pattern(SnekPattern(brain.width, brain.height))
        } else {
            builder.pattern(brain.patterns[index])
        }
    }
    return builder.build()
}