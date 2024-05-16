package minesweeper

import kotlin.random.Random

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
        Minesweeper.State.NotExplored, Minesweeper.State.NotFinished -> {
            val action = retrieveAction()
            if (minesweeper.isValid(action))
                minesweeper.execute(action)
            loop(minesweeper)
        }
        Minesweeper.State.Win -> println("Congratulations! You found all the mines!")
        Minesweeper.State.Loss -> println("You stepped on a mine and failed!")
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

class Minesweeper(private val numberOfMines: Int) {
    enum class State{
        NotExplored,
        NotFinished,
        Win,
        Loss
    }

    var state = State.NotExplored
    private var minedCoordinates : Set<Pair<Int,Int>> = emptySet()
    private var matrix: List<List<Cell>> = List(SIDE) { List(SIDE) { Cell.Empty() } }

    private val markedCoordinates
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isMarked) rowIndex to colIndex else null } }.toSet()

    private val unexploredCoordinates
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isExplored) null else rowIndex to colIndex } }.toSet()

    private fun neighbors(my: Pair<Int, Int>): List<Pair<Int, Int>> =
        OFFSETS
            .filter { my.first + it.first in 0 until SIDE && my.second + it.second in 0 until SIDE }
            .map { my.first + it.first to my.second + it.second }

    private fun countMinesAround(my: Pair<Int, Int>): Int = neighbors(my).count { it in minedCoordinates }

    fun isValid(action: Action): Boolean = !getCell(action.coordinate).isExplored

    fun execute(action: Action) =
        when (action) {
            is Action.Mark -> {
                getCell(action.coordinate).remark()
                state = if (markedCoordinates == minedCoordinates) State.Win else state
            }
            is Action.Explore -> {
                if (state == State.NotExplored) {
                    initializeMines(action.coordinate)
                    state = State.NotFinished
                }
                explore(action.coordinate)
                state = if (unexploredCoordinates == minedCoordinates) State.Win else state
            }
        }

    fun printMatrix() {
        println()
        println(" │123456789│")
        println("—│—————————│")
        for ((index, row) in matrix.withIndex()) {
            print("${index + 1}|")
            for (cell in row) {
                print(if (state == State.Loss && cell is Cell.Mine) 'X' else cell)
            }
            println("|")
        }
        println("—│—————————│")
    }

    private fun initializeMines(firstExploredCoordinate: Pair<Int, Int>) {
        minedCoordinates = generateDistinctCoordinates(numberOfMines, firstExploredCoordinate)
        matrix = List(SIDE) { row ->
            List(SIDE) { col ->
                val coordinate = row to col
                if (coordinate in minedCoordinates)
                    Cell.Mine(isMarked = getCell(coordinate).isMarked)
                else
                    Cell.Empty(countMinesAround(coordinate), isMarked = getCell(coordinate).isMarked)
            }
        }
    }

    private fun explore(coordinate: Pair<Int, Int>) {
        val cell = getCell(coordinate)
        if (cell is Cell.Mine) {
            state = State.Loss
            return
        }

        if ((cell as Cell.Empty).isExplored) return

        cell.explore()

        if (cell.minesAround > 0) return

        neighbors(coordinate).forEach { explore(it) }
    }

    private fun getCell(pair: Pair<Int, Int>): Cell = matrix[pair.first][pair.second]

    private fun generateDistinctCoordinates(n: Int, coordinate: Pair<Int, Int>): Set<Pair<Int, Int>> {
        val coordinates = mutableSetOf<Pair<Int, Int>>()
        while (coordinates.size < n) {
            val nextCoordinate = Random.nextInt(SIDE) to Random.nextInt(SIDE)
            if (nextCoordinate == coordinate) continue
            coordinates.add(nextCoordinate)
        }
        return coordinates.toSet()
    }
}

sealed class Cell {
    open var isMarked: Boolean = false
    open fun remark() {
        isMarked = !isMarked
    }
    open var isExplored: Boolean = false

    data class Empty(val minesAround: Int = 0,
                     override var isMarked: Boolean = false,
                     override var isExplored: Boolean = false) : Cell() {

        fun explore() {
            isExplored = true
            if (isMarked) remark()
        }

        override fun toString() = when {
            isMarked -> "*"
            !isExplored -> "."
            minesAround == 0 -> "/"
            else -> minesAround.toString()
        }
    }

    data class Mine(override var isMarked: Boolean = false) : Cell() {
        override fun toString() = if (isMarked) "*" else "."
    }
}

sealed class Action(open val coordinate: Pair<Int, Int>) {
    data class Mark(override val coordinate: Pair<Int, Int>) : Action(coordinate)
    data class Explore(override val coordinate: Pair<Int, Int>) : Action(coordinate)
}