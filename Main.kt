package minesweeper

const val SIDE = 9
val OFFSETS: List<Action.Position> = listOf(
    -1 to -1,   -1 to 0,    -1 to 1,
     0 to -1,                0 to 1,
     1 to -1,    1 to 0,     1 to 1
).map { Action.Position(row = it.first, col = it.second) }

fun main() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readln().toInt()
    val minesweeper = Minesweeper(numberOfMines)
    loop(minesweeper)
}

tailrec fun loop(minesweeper: Minesweeper) {
    minesweeper.printMatrix()
    val action = retrieveAction()
    if (!minesweeper.isValid(action))
        loop(minesweeper)
    else
        when (minesweeper.execute(action)) {
            Action.Result.PlayerWins -> minesweeper.printWin()
            Action.Result.PlayerLoses -> minesweeper.printLose()
            Action.Result.GameNotOver -> loop(minesweeper)
        }
}

fun retrieveAction(): Action {
    print("Set/unset mine marks or claim a cell as free: ")
    val (col, row, action) = readln().split(" ")
    val position = Action.Position(row = row.toInt() - 1, col = col.toInt() - 1)
    return when (action) {
        "free" -> Action.Explore(position)
        "mine" -> Action.Mark(position)
        else -> throw IllegalArgumentException(action)
    }
}