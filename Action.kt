package minesweeper


sealed class Action(open val position: Position) {

    data class Position(val row: Int, val col: Int)
    enum class Result {
        PlayerWins,
        PlayerLoses,
        GameNotOver
    }

    data class Mark(override val position: Position) : Action(position)
    data class Explore(override val position: Position) : Action(position)
}