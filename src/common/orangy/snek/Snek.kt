package orangy.snek

class Snek(val name: String, val brain: SnekBrain) {
    var age = 1
    val BodyCell: ArenaCell = ArenaCell.Body(this)
    val TailCell: ArenaCell = ArenaCell.Tail(this)
    val HeadCell: ArenaCell = ArenaCell.Head(this)
}