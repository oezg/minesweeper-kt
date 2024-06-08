package minesweeper


sealed class Action(open val position: Position) {

    data class Position(val row: Int, val col: Int) {
        val neighbors: List<Position> get() =
            OFFSETS
                .filter { row + it.row in 0 until SIDE && col + it.col in 0 until SIDE }
                .map { Position(row = row + it.row, col = col + it.col) }
    }
    enum class Result {
        PlayerWins,
        PlayerLoses,
        GameNotOver
    }

    data class Mark(override val position: Position) : Action(position)
    data class Explore(override val position: Position) : Action(position)
}