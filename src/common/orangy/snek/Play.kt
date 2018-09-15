package orangy.snek

val roundsPerGame = 1000

fun main(args: Array<String>) {
    val sneks = listOf(
            Snek("A", brain1),
            Snek("D", brain4)
    )

    val arena = Arena(17, 17)
    sneks.mapIndexed { index, snek -> arena.startDuelPosition(snek, index, sneks.size) }
    arena.print()
    val result = simulate(sneks, arena, 1000) {
        arena.print()
    }
    arena.print()
    println("Played ${result.rounds} rounds")

    result.status.sneks.forEach {
        println("Snek `${it.snek.name}` length: ${it.length} ${if (it.dead) "(dead)" else ""}")
    }
}

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

data class SimulationResult(val rounds: Int, val status: ArenaStatus)
