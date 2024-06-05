package minesweeper

class Grid(val matrix: List<List<Cell>> = List(SIDE) { List(SIDE) { Cell.Empty() } } ) {

    fun initializeMines(minedCoordinates: Set<Pair<Int, Int>>): Grid {
        val newMatrix = List(SIDE) { row ->
            List(SIDE) { col ->
                val coordinate = row to col
                if (coordinate in minedCoordinates) {
                    Cell.Mine(marked = getCell(coordinate).isMarked)
                } else {
                    val minesAround = neighbors(coordinate).count { it in minedCoordinates }
                    Cell.Empty(minesAround = minesAround, marked = getCell(coordinate).isMarked)
                }
            }
        }
        return Grid(newMatrix)
    }

    fun explore(coordinate: Pair<Int, Int>): Minesweeper.State {
        val cell = getCell(coordinate)
        return when {
            cell is Cell.Mine -> Minesweeper.State.Loss
            cell.isExplored -> Minesweeper.State.NotFinished
            else -> {
                (cell as Cell.Empty).explore()
                if (cell.minesAround == 0) {
                    neighbors(coordinate).forEach { explore(it) }
                }
                Minesweeper.State.NotFinished
            }
        }
    }

    fun mark(coordinate: Pair<Int, Int>) = getCell(coordinate).remark()

    fun isExplored(coordinate: Pair<Int, Int>) = getCell(coordinate).isExplored

    private fun getCell(pair: Pair<Int, Int>): Cell = matrix[pair.first][pair.second]

    private fun neighbors(my: Pair<Int, Int>): List<Pair<Int, Int>> =
        OFFSETS
            .filter { my.first + it.first in 0 until SIDE && my.second + it.second in 0 until SIDE }
            .map { my.first + it.first to my.second + it.second }

    fun asString(state: Minesweeper.State): List<String> =
        matrix.map { it.joinToString(separator = "") { if (state == Minesweeper.State.Loss && it is Cell.Mine) "X" else it.toString() } }

    val markedCoordinates
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isMarked) rowIndex to colIndex else null } }.toSet()

    val unexploredCoordinates
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isExplored) null else rowIndex to colIndex } }.toSet()
}