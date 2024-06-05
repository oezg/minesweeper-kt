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

    fun execute(action: Action) {
        state = when (action) {
            is Action.Mark -> {
                grid.mark(action.coordinate)
                if (grid.markedCoordinates == minedCoordinates) State.Win else state
            }
            is Action.Explore -> {
                if (state == State.NotExplored) {
                    setUpBeforeFirstExploration(action)
                }
                explore(action.coordinate)
                if (grid.unexploredCoordinates == minedCoordinates) State.Win else state
            }
        }
    }

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

    private fun setUpBeforeFirstExploration(action: Action) {
        minedCoordinates = generateDistinctCoordinates(action.coordinate)
        grid = grid.initializeMines(minedCoordinates)
        state = State.NotFinished
    }

    private fun explore(coordinate: Pair<Int, Int>) {
        state = grid.explore(coordinate)
    }

    private fun generateDistinctCoordinates(coordinate: Pair<Int, Int>): Set<Pair<Int, Int>> {
        val coordinates = mutableSetOf<Pair<Int, Int>>()
        while (coordinates.size < numberOfMines) {
            val nextCoordinate = Random.nextInt(SIDE) to Random.nextInt(SIDE)
            if (nextCoordinate == coordinate) continue
            coordinates.add(nextCoordinate)
        }
        return coordinates.toSet()
    }
}