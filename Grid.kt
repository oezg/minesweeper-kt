package minesweeper

class Grid(val matrix: List<List<Cell>> = List(SIDE) { List(SIDE) { Cell.Empty() } } ) {

    fun initializeMines(minedCoordinates: Set<Pair<Int, Int>>): Grid {
        val newMatrix = List(SIDE) { row ->
            List(SIDE) { col ->
                val coordinate = row to col
                if (coordinate in minedCoordinates)
                    Cell.Mine(marked = getCell(coordinate).isMarked)
                else
                    Cell.Empty(countMinesAround(coordinate), marked = getCell(coordinate).isMarked)
            }
        }
        return Grid(newMatrix)
    }

    fun mark(coordinate: Pair<Int, Int>) {
        getCell(coordinate).remark()
    }

    fun isExplored(coordinate: Pair<Int, Int>): Boolean {
        return getCell(coordinate).isExplored
    }

    fun getCell(pair: Pair<Int, Int>): Cell = matrix[pair.first][pair.second]

    private fun countMinesAround(my: Pair<Int, Int>): Int = neighbors(my).count { it in minedCoordinates }

    val markedCoordinates
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isMarked) rowIndex to colIndex else null } }.toSet()

    val unexploredCoordinates
        get() = matrix.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, cell ->
                if (cell.isExplored) null else rowIndex to colIndex } }.toSet()
}