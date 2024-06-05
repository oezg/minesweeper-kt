package minesweeper


sealed class Action(open val position: Position) {
    data class Mark(override val position: Position) : Action(position)
    data class Explore(override val position: Position) : Action(position)
}