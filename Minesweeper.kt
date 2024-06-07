package minesweeper

data class Minesweeper(val numberOfMines: Int) {

    enum class GameState {
        NotOver,
        Win,
        Loss
    }

    private var grid = Grid(numberOfMines)

    fun execute(action: Action): GameState =
        when (action) {
            is Action.Mark -> {
                grid.mark(action.position)
                if (grid.isAllMinesMarked)
                    GameState.Win
                else
                    GameState.NotOver
            }
            is Action.Explore -> {
                grid.explore(action.position)
                if (grid.isMineExplored)
                    GameState.Loss
                else if (grid.isAllEmptyExplored)
                    GameState.Win
                else
                    GameState.NotOver
            }
        }

    fun isValid(action: Action): Boolean =
        !grid.isExplored(action.position)

    fun printMatrix() = println(grid)
}