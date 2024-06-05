package minesweeper

const val SIDE = 9
val OFFSETS: List<Position> = listOf(
    -1 to -1,   -1 to 0,    -1 to 1,
     0 to -1,                0 to 1,
     1 to -1,    1 to 0,     1 to 1
).map { Position(row = it.first, col = it.second) }

fun main() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readln().toInt()
    val minesweeper = Minesweeper(numberOfMines)
    loop(minesweeper)
}

tailrec fun loop(minesweeper: Minesweeper) {
    minesweeper.printMatrix()
    when (minesweeper.state) {
        Grid.State.Win -> println("Congratulations! You found all the mines!")
        Grid.State.Loss -> println("You stepped on a mine and failed!")
        Grid.State.NotExplored, Grid.State.NotFinished -> {
            retrieveAction().takeIf { minesweeper.isValid(it) }?.let { minesweeper.execute(it) }
            loop(minesweeper)
        }
    }
}

fun retrieveAction(): Action {
    print("Set/unset mine marks or claim a cell as free: ")
    val (col, row, action) = readln().split(" ")
    val position = Position(row = row.toInt() - 1, col = col.toInt() - 1)
    return when (action) {
        "free" -> Action.Explore(position)
        "mine" -> Action.Mark(position)
        else -> throw IllegalArgumentException(action)
    }
}