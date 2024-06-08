package minesweeper

import kotlin.random.Random

class Grid(private val numberOfMines: Int, private val matrix: Matrix = emptyMatrix) {

    private var minedPositions: Set<Action.Position> =
        emptySet()

    val isAllEmptyExplored: Boolean get() =
        matrix.unexploredPositions() == minedPositions

    val isAllMinesMarked: Boolean get() =
        matrix.markedPositions() == minedPositions

    val isNotYetExplored: Boolean get() =
        matrix.isNotYetExplored()

    fun explore(position: Action.Position): Grid =
        Grid(numberOfMines, exploreRecursive(matrix, position))

    fun exploreRecursive(accumulator: Matrix, position: Action.Position): Matrix {
        var current = accumulator.reveal(position)

        if (!accumulator.isZero(position))
            return current


        for (neighbor in position.neighbors) {
            val next = current.reveal(neighbor)
            if (next.isZero(neighbor))
                current = exploreRecursive(next, neighbor)
            else
                current = next
        }

        return current
    }

    fun setupMines(position: Action.Position): Grid {
        minedPositions = generateMinePositions(position)
        return Grid(numberOfMines, matrix.set(minedPositions))
    }

    fun mark(position: Action.Position) =
        matrix.mark(position)

    val isMineExplored: Boolean get() =
        minedPositions.any { matrix.isCellExplored(it) }

    fun isExplored(position: Action.Position): Boolean =
        matrix.isCellExplored(position)

    private fun generateMinePositions(position: Action.Position): Set<Action.Position> {
        val positions = mutableSetOf<Action.Position>()
        while (positions.size < numberOfMines) {
            val nextPosition = Action.Position(Random.nextInt(SIDE), Random.nextInt(SIDE))
            if (nextPosition == position) continue
            positions.add(nextPosition)
        }
        return positions.toSet()
    }

    override fun toString(): String =
        matrix.str(lose = false)

    fun toStringLost(): String =
        matrix.str(lose = true)
}