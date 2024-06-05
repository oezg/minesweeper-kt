package minesweeper

const val SIDE = 9
val OFFSETS = listOf(
    -1 to -1,   -1 to 0,    -1 to 1,
     0 to -1,                0 to 1,
     1 to -1,    1 to 0,     1 to 1
)

fun main() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readln().toInt()
    val minesweeper = Minesweeper(numberOfMines)
    loop(minesweeper)
}

tailrec fun loop(minesweeper: Minesweeper) {
    minesweeper.printMatrix()
    when (minesweeper.state) {
        Minesweeper.State.Win -> println("Congratulations! You found all the mines!")
        Minesweeper.State.Loss -> println("You stepped on a mine and failed!")
        Minesweeper.State.NotExplored, Minesweeper.State.NotFinished -> {
            retrieveAction().takeIf { minesweeper.isValid(it) }?.let { minesweeper.execute(it) }
            loop(minesweeper)
        }
    }
}

fun retrieveAction(): Action {
    print("Set/unset mine marks or claim a cell as free: ")
    val (x, y, action) = readln().split(" ")
    val coordinate = y.toInt() - 1 to x.toInt() - 1
    return when (action) {
        "free" -> Action.Explore(coordinate)
        "mine" -> Action.Mark(coordinate)
        else -> throw IllegalArgumentException(action)
    }
}