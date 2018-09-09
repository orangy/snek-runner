package org.orangy.snek

class Snek(val id: Int, val name: String, val pattern: SnekBrain) {
    val BodyCell: ArenaCell = ArenaCell.Body(this)
    val TailCell: ArenaCell = ArenaCell.Tail(this)
    val HeadCell: ArenaCell = ArenaCell.Head(this)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Snek) return false
        return id == other.id
    }

    override fun hashCode(): Int = id
}