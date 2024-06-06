package minesweeper

import kotlin.random.Random

class Grid(private val numberOfMines: Int) {

    enum class State{
        NotOver,
        Win,
        Loss
    }

    private var minedPositions : Set<Position> = emptySet()
    var matrix: List<List<Cell>> = List(SIDE) { List(SIDE) { Cell.Empty() } }
        private set
    var state = State.NotOver
        private set
    fun explore(position: Position) {
        if (minedPositions.isEmpty()) {
            minedPositions = generateMinePositions(position)
            matrix = initializeMines
        }
        val cell = getCell(position)
        state = when {
            cell is Cell.Mine -> State.Loss
            cell.isExplored -> State.NotOver
            else -> {
                (cell as Cell.Empty).explore()
                if (cell.minesAround == 0) {
                    neighbors(position).forEach { explore(it) }
                }
                if (unexploredPositions == minedPositions)
                    State.Win
                else
                    State.NotOver
            }
        }
    }

    fun mark(position: Position) {
        getCell(position).remark()
        if (markedPositions == minedPositions)
            state = State.Win
    }

    fun isExplored(position: Position): Boolean = getCell(position).isExplored

    val asString: List<String>
        get() = matrix.map { it.joinToString(separator = "") { it.asString(state) } }

    private val initializeMines: List<List<Cell>>
        get() = List(SIDE) { row ->
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

    private val markedPositions: Set<Position>
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isMarked) Position(rowIndex, colIndex) else null } }.toSet()

    private val unexploredPositions: Set<Position>
        get() = matrix.flatMapIndexed { rowIndex, row ->
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