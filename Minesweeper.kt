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
        private set
    private var minedCoordinates : Set<Pair<Int,Int>> = emptySet()
    private var grid = Grid()

    fun execute(action: Action) =
        when (action) {
            is Action.Mark -> {
                grid.mark(action.coordinate)
                state = if (grid.markedCoordinates == minedCoordinates) State.Win else state
            }
            is Action.Explore -> {
                if (state == State.NotExplored) {
                    minedCoordinates = generateDistinctCoordinates(action.coordinate)
                    grid = grid.initializeMines(minedCoordinates)
                    state = State.NotFinished
                }
                explore(action.coordinate)
                state = if (grid.unexploredCoordinates == minedCoordinates) State.Win else state
            }
        }

    private fun neighbors(my: Pair<Int, Int>): List<Pair<Int, Int>> =
        OFFSETS
            .filter { my.first + it.first in 0 until SIDE && my.second + it.second in 0 until SIDE }
            .map { my.first + it.first to my.second + it.second }

    fun isValid(action: Action): Boolean = !grid.isExplored(action.coordinate)

    fun printMatrix() {
        println()
        println(" │123456789│")
        println("—│—————————│")
        for ((index, row) in grid.matrix.withIndex()) {
            print("${index + 1}|")
            for (cell in row) {
                print(if (state == State.Loss && cell is Cell.Mine) 'X' else cell)
            }
            println("|")
        }
        println("—│—————————│")
    }



    private fun explore(coordinate: Pair<Int, Int>) {
        val cell = grid.getCell(coordinate)
        if (cell is Cell.Mine) {
            state = State.Loss
            return
        }

        if ((cell as Cell.Empty).isExplored) return

        cell.explore()

        if (cell.minesAround > 0) return

        neighbors(coordinate).forEach { explore(it) }
    }


    fun generateDistinctCoordinates(coordinate: Pair<Int, Int>): Set<Pair<Int, Int>> {
        val coordinates = mutableSetOf<Pair<Int, Int>>()
        while (coordinates.size < numberOfMines) {
            val nextCoordinate = Random.nextInt(SIDE) to Random.nextInt(SIDE)
            if (nextCoordinate == coordinate) continue
            coordinates.add(nextCoordinate)
        }
        return coordinates.toSet()
    }
}