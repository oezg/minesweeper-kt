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
    private var minedCoordinates : Set<Position> = emptySet()
    private var grid = Grid()

    fun execute(action: Action) {
        state = when (action) {
            is Action.Mark -> {
                grid.mark(action.position)
                if (grid.markedPositions == minedCoordinates) State.Win else state
            }
            is Action.Explore -> {
                if (state == State.NotExplored) {
                    setUpBeforeFirstExploration(action)
                }
                explore(action.position)
                if (grid.unexploredPositions == minedCoordinates) State.Win else state
            }
        }
    }

    fun isValid(action: Action): Boolean = !grid.isExplored(action.position)

    fun printMatrix() {
        println()
        println(" │123456789│")
        println("—│—————————│")
        grid.asString(state).forEachIndexed { index, row -> println("${index + 1}|$row|") }
        println("—│—————————│")
    }

    private fun setUpBeforeFirstExploration(action: Action) {
        minedCoordinates = generateMinePositions(action.position)
        grid = grid.initializeMines(minedCoordinates)
        state = State.NotFinished
    }

    private fun explore(position: Position) {
        state = grid.explore(position)
    }

    private fun generateMinePositions(position: Position): Set<Position> {
        val positions = mutableSetOf<Position>()
        while (positions.size < numberOfMines) {
            val nextPosition = Position(Random.nextInt(SIDE), Random.nextInt(SIDE))
            if (nextPosition == position) continue
            positions.add(nextPosition)
        }
        return positions.toSet()
    }
}