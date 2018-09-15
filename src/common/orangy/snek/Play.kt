package orangy.snek

val roundsPerGame = 1000

fun main(args: Array<String>) {
    val sneks = listOf(
            Snek("A", brain1),
            Snek("D", brain4)
    )

    val arena = Arena(17, 17)
    sneks.mapIndexed { index, snek -> arena.startDuelPosition(snek, index, sneks.size) }
    arena.dump()
    val result = arena.simulate(1000) {
        println("Round #$it")
        arena.dump() 
        println()
        pause()
    }
    arena.dump()
    println("Played ${result.rounds} rounds")

    result.status.sneks.forEach {
        println("Snek `${it.snek.name}` length: ${it.length} ${if (it.dead) "(dead)" else ""}")
    }
}

data class SimulationResult(val rounds: Int, val status: ArenaStatus)
