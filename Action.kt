package minesweeper


sealed class Action(open val coordinate: Pair<Int, Int>) {
    data class Mark(override val coordinate: Pair<Int, Int>) : Action(coordinate)
    data class Explore(override val coordinate: Pair<Int, Int>) : Action(coordinate)
}