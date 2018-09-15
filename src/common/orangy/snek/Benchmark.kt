package orangy.snek

private val totalGames = 10000

fun main(args: Array<String>) {
    val sneks = listOf(
            Snek("A", brain1),
            Snek("B", brain2),
            Snek("C", brain2),
            Snek("D", brain3)
    )

    val start = nanoTime()

    val timings = (0..totalGames).map {
        val arena = Arena(28, 28)
        sneks.mapIndexed { index, snek -> arena.startSkirmishPosition(snek, index, sneks.size) }

        var result: SimulationResult? = null
        val time = measureNanoTime {
            result = simulate(sneks, arena, roundsPerGame, null)
        }
        time to result!!
    }

    val totalTime = nanoTime() - start

    val sneksResults = dumpStatistics(totalGames, totalTime, timings, sneks)
    sneks.forEach { it.dumpStatistics(sneks, sneksResults) }
}
