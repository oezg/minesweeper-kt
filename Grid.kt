package minesweeper

class Grid(val matrix: List<List<Cell>> = List(SIDE) { List(SIDE) { Cell.Empty() } } ) {

    fun initializeMines(minedPositions: Set<Position>): Grid {
        val newMatrix = List(SIDE) { row ->
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
        return Grid(newMatrix)
    }

    fun explore(position: Position): Minesweeper.State {
        val cell = getCell(position)
        return when {
            cell is Cell.Mine -> Minesweeper.State.Loss
            cell.isExplored -> Minesweeper.State.NotFinished
            else -> {
                (cell as Cell.Empty).explore()
                if (cell.minesAround == 0) {
                    neighbors(position).forEach { explore(it) }
                }
                Minesweeper.State.NotFinished
            }
        }
    }

    fun mark(position: Position) = getCell(position).remark()

    fun isExplored(position: Position) = getCell(position).isExplored

    private fun getCell(position: Position): Cell = matrix[position.row][position.col]

    private fun neighbors(my: Position): List<Position> =
        OFFSETS
            .filter { my.row + it.row in 0 until SIDE && my.col + it.col in 0 until SIDE }
            .map { Position(row = my.row + it.row, col = my.col + it.col) }

    fun asString(state: Minesweeper.State): List<String> =
        matrix.map { it.joinToString(separator = "") {
            if (state == Minesweeper.State.Loss && it is Cell.Mine)
                "X"
            else
                it.toString() } }

    val markedPositions
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isMarked) Position(rowIndex, colIndex) else null } }.toSet()

    val unexploredPositions
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isExplored) null else Position(rowIndex, colIndex) } }.toSet()
}