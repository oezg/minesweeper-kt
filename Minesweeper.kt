package minesweeper

data class Minesweeper(val numberOfMines: Int) {

    private var grid = Grid(numberOfMines)

    fun execute(action: Action): Action.Result =
        when (action) {
            is Action.Mark -> {
                grid.mark(action.position)
                if (grid.isAllMinesMarked)
                    Action.Result.PlayerWins
                else
                    Action.Result.GameNotOver
            }
            is Action.Explore -> {
                if (grid.isNotYetExplored) {
                    grid = grid.setupMines(action.position)
                }
                grid.explore(action.position)
                if (grid.isMineExplored)
                    Action.Result.PlayerLoses
                else if (grid.isAllEmptyExplored)
                    Action.Result.PlayerWins
                else
                    Action.Result.GameNotOver
            }
        }

    fun isValid(action: Action): Boolean =
        !grid.isExplored(action.position)

    fun printMatrix() = println(grid)
    fun printWin() {
        printMatrix()
        println("Congratulations! You found all the mines!")
    }

    fun printLose() {
        println(grid.AsLost())
        println("You stepped on a mine and failed!")
    }
}