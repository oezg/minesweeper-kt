package minesweeper

import kotlin.random.Random

typealias Matrix = List<List<Cell>>

class Grid(private val numberOfMines: Int, private val matrix: Matrix = List(SIDE) { List(SIDE) { Empty(0, false, false) } }) {

    private var minedPositions : Set<Action.Position> = emptySet()
    val isAllEmptyExplored: Boolean
        get() = unexploredPositions == minedPositions
    val isAllMinesMarked: Boolean
        get() = markedPositions == minedPositions

    val isNotYetExplored: Boolean =
        !matrix.flatten().any { it.explored }

    fun explore(position: Action.Position): Grid {
        if (getCell(position).explored) {
            return this
        }
        val cell = getCell(position).explore()

        var nextGrid = Grid(numberOfMines, getNextMatrix(position, cell))

        if (cell !is Empty || cell.minesAround > 0) {
            return nextGrid
        }

        for (neighbor in neighbors(position)) {
            nextGrid = nextGrid.explore(neighbor)
        }

        return nextGrid
    }

    fun getNextMatrix(position: Action.Position, cell: Cell): Matrix =
        List(SIDE) {row ->
            List(SIDE) {col ->
                if (position == Action.Position(row = row, col = col))
                    cell
                else
                    getCell(position)
            }
        }

    fun setupMines(position: Action.Position): Grid {
        minedPositions = generateMinePositions(position)
        return Grid(numberOfMines, initializeMines)
    }

    fun mark(position: Action.Position) =
        getCell(position).remark()

    val isMineExplored: Boolean
        get() = minedPositions.any { isExplored(it) }

    fun isExplored(position: Action.Position): Boolean = getCell(position).explored

    private val initializeMines: List<List<Cell>> =
        List(SIDE) { row ->
            List(SIDE) { col ->
                val position = Action.Position(row, col)
                val cell = getCell(position)
                if (position in minedPositions) {
                    Mine(marked = cell.marked, explored = cell.explored)
                } else {
                    val minesAround = neighbors(position).count { it in minedPositions }
                    Empty(minesAround = minesAround, marked = cell.marked, explored = cell.explored)
                }
            }
        }

    private val markedPositions: Set<Action.Position> =
        matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.marked) Action.Position(rowIndex, colIndex) else null } }.toSet()

    private val unexploredPositions: Set<Action.Position> =
        matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.explored) null else Action.Position(rowIndex, colIndex) } }.toSet()

    private fun getCell(position: Action.Position): Cell = matrix[position.row][position.col]

    private fun neighbors(my: Action.Position): List<Action.Position> =
        OFFSETS
            .filter { my.row + it.row in 0 until SIDE && my.col + it.col in 0 until SIDE }
            .map { Action.Position(row = my.row + it.row, col = my.col + it.col) }

    private fun generateMinePositions(position: Action.Position): Set<Action.Position> {
        val positions = mutableSetOf<Action.Position>()
        while (positions.size < numberOfMines) {
            val nextPosition = Action.Position(Random.nextInt(SIDE), Random.nextInt(SIDE))
            if (nextPosition == position) continue
            positions.add(nextPosition)
        }
        return positions.toSet()
    }

    override fun toString(): String {
        return matrix.withIndex().joinToString (
            separator = "",
            prefix = "\n │123456789│\n—│—————————│\n",
            postfix = "—│—————————│"
        ) {(index, row) ->
            row.joinToString(
                separator = "",
                prefix = "$index|",
                postfix = "|\n"
            )
        }
    }

    fun AsLost(): String {
        return matrix.withIndex().joinToString (
            separator = "",
            prefix = "\n │123456789│\n—│—————————│\n",
            postfix = "—│—————————│"
        ) {(index, row) ->
            row.joinToString(
                separator = "",
                prefix = "$index|",
                postfix = "|\n"
            ) { it.loser() }
        }
    }
}