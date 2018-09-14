package orangy.snek

class ArenaStatus(val sneks: List<SnekStatus>)
class SnekStatus(val snek: Snek, val length: Int, val dead: Boolean)
