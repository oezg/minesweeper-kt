package minesweeper

import kotlin.random.Random

class Grid(private val numberOfMines: Int, private val matrix: List<List<Cell>> = List(SIDE) { List(SIDE) { Cell.Empty() } }) {

    private var minedPositions : Set<Action.Position> = emptySet()
    val isAllEmptyExplored: Boolean
        get() = unexploredPositions == minedPositions
    val isAllMinesMarked: Boolean
        get() = markedPositions == minedPositions

    val isNotYetExplored: Boolean =
        !matrix.flatten().any { it.explored }

    fun explore(position: Action.Position) {
        val cell = getCell(position)
        cell.explore()
        if (cell is Cell.Empty && cell.minesAround == 0)
            neighbors(position).forEach { explore(it) }
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

    override fun toString(): String = toStringGeneral { it.asString() }


    private val initializeMines: List<List<Cell>> =
        List(SIDE) { row ->
            List(SIDE) { col ->
                val position = Action.Position(row, col)
                if (position in minedPositions) {
                    Cell.Mine(marked = getCell(position).marked)
                } else {
                    val minesAround = neighbors(position).count { it in minedPositions }
                    Cell.Empty(minesAround = minesAround, marked = getCell(position).marked)
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

    fun toStringGeneral(funky: (Cell) -> String): String {
        return matrix.withIndex().joinToString (
            separator = "",
            prefix = "\n │123456789│\n—│—————————│\n",
            postfix = "—│—————————│"
        ) {(index, row) ->
            row.joinToString(
                separator = "",
                prefix = "$index|",
                postfix = "|\n"
            ) { funky(it) }
        }
    }

    fun AsLost(): String {
        return toStringGeneral { it.loser() }
    }
}