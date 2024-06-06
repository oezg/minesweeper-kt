package minesweeper

class Minesweeper(numberOfMines: Int) {

    private var grid = Grid(numberOfMines)

    val state get() = grid.state

    fun execute(action: Action) {
        when (action) {
            is Action.Mark -> grid.mark(action.position)
            is Action.Explore -> grid.explore(action.position)
        }
    }

    fun isValid(action: Action): Boolean = !grid.isExplored(action.position)

    fun printMatrix() {
        println()
        println(" │123456789│")
        println("—│—————————│")
        grid.asString.forEachIndexed { index, row -> println("${index + 1}|$row|") }
        println("—│—————————│")
    }
}