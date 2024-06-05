package minesweeper

import kotlin.random.Random

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
                    Cell.Mine(marked = getCell(coordinate).isMarked)
                else
                    Cell.Empty(countMinesAround(coordinate), marked = getCell(coordinate).isMarked)
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