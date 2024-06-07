package minesweeper

import kotlin.random.Random

class Grid(private val numberOfMines: Int, private val matrix: List<List<Cell>> = List(SIDE) { List(SIDE) { Cell.Empty() } }) {

    private var minedPositions : Set<Position> = emptySet()
    val isAllEmptyExplored: Boolean
        get() = unexploredPositions == minedPositions
    val isAllMinesMarked: Boolean
        get() = markedPositions == minedPositions

    val isNotYetExplored: Boolean =
        !matrix.flatten().any { it.isExplored }

    fun explore(position: Position) {
        val cell = getCell(position)
        cell.explore()
        if (cell is Cell.Empty && cell.minesAround == 0)
            neighbors(position).forEach { explore(it) }
    }

    fun setupMines(position: Position): Grid {
        minedPositions = generateMinePositions(position)
        return Grid(numberOfMines, initializeMines)
    }

    fun mark(position: Position) =
        getCell(position).remark()

    val isMineExplored: Boolean
        get() = minedPositions.any { isExplored(it) }

    fun isExplored(position: Position): Boolean = getCell(position).isExplored

    override fun toString(): String =
        matrix.withIndex().joinToString (
            separator = "",
            prefix = "\n │123456789│\n—│—————————│\n",
            postfix = "—│—————————│"
        ) {(index, row) ->
            row.joinToString(
                separator = "",
                prefix = "$index|",
                postfix = "|\n"
            ) { it.asString(state) }
        }

    private val initializeMines: List<List<Cell>> =
        List(SIDE) { row ->
            List(SIDE) { col ->
                val position = Position(row, col)
                if (position in minedPositions) {
                    Cell.Mine(marked = getCell(position).isMarked)
                } else {
                    val minesAround = neighbors(position).count { it in minedPositions }
                    Cell.Empty(minesAround = minesAround, marked = getCell(position).isMarked)
                }
            }
        }

    private val markedPositions: Set<Position> =
        matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isMarked) Position(rowIndex, colIndex) else null } }.toSet()

    private val unexploredPositions: Set<Position> =
        matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isExplored) null else Position(rowIndex, colIndex) } }.toSet()

    private fun getCell(position: Position): Cell = matrix[position.row][position.col]

    private fun neighbors(my: Position): List<Position> =
        OFFSETS
            .filter { my.row + it.row in 0 until SIDE && my.col + it.col in 0 until SIDE }
            .map { Position(row = my.row + it.row, col = my.col + it.col) }

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