package minesweeper

class Grid(val matrix: List<List<Cell>> = List(SIDE) { List(SIDE) { Cell.Empty() } } ) {

    enum class State{
        NotExplored,
        NotFinished,
        Win,
        Loss
    }

    var state = State.NotExplored
        private set
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

    fun explore(position: Position): State {
        val cell = getCell(position)
        return when {
            cell is Cell.Mine -> State.Loss
            cell.isExplored -> State.NotFinished
            else -> {
                (cell as Cell.Empty).explore()
                if (cell.minesAround == 0) {
                    neighbors(position).forEach { explore(it) }
                }
                State.NotFinished
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

    fun asString(): List<String> =
        matrix.map { it.joinToString(separator = "") { it.asString(state) } }

    val markedPositions
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isMarked) Position(rowIndex, colIndex) else null } }.toSet()

    val unexploredPositions
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isExplored) null else Position(rowIndex, colIndex) } }.toSet()
}