package minesweeper

typealias Matrix = List<List<Cell>>

val emptyMatrix: List<List<Empty>> = List(SIDE) { List(SIDE) { Empty(0, false, false) } }

fun Matrix.isNotYetExplored(): Boolean =
    !this.flatten().any { it.isExplored }

fun Matrix.markedPositions(): Set<Action.Position> =
    this.flatMapIndexed { rowIndex, row ->
        row.mapIndexedNotNull { colIndex, cell ->
            if (cell.isMarked) Action.Position(rowIndex, colIndex) else null } }.toSet()

fun Matrix.unexploredPositions(): Set<Action.Position> =
    this.flatMapIndexed { rowIndex, row ->
        row.mapIndexedNotNull { colIndex, cell ->
            if (cell.isExplored) null else Action.Position(rowIndex, colIndex) } }.toSet()

fun Matrix.cellAt(position: Action.Position): Cell = this[position.row][position.col]

fun Matrix.isCellExplored(position: Action.Position): Boolean = this.cellAt(position).isExplored

fun Matrix.set(minedPositions: Set<Action.Position>): List<List<Cell>> =
    List(SIDE) { row ->
        List(SIDE) { col ->
            val position = Action.Position(row, col)
            val cell = this.cellAt(position)
            if (position in minedPositions) {
                Mine(isMarked = cell.isMarked, isExplored = false)
            } else {
                val minesAround = position.neighbors.count { it in minedPositions }
                Empty(minesAround = minesAround, isMarked = cell.isMarked, isExplored = false)
            }
        }
    }

fun Matrix.mark(position: Action.Position): Cell =
    this.cellAt(position).remark()

fun Matrix.str(lose: Boolean): String =
    this.withIndex().joinToString(
        separator = "\n",
        prefix = "\n │123456789│\n—│—————————│\n",
        postfix = "\n—│—————————│"
    ) { (index, row) ->
        row.joinToString(
            separator = "",
            prefix = "${index+1}|",
            postfix = "|"
        ) { if (lose) it.loser() else it.toString() }
    }

fun Matrix.reveal(position: Action.Position): Matrix =
    this.getNextMatrix(position, this.cellAt(position = position).explore())

fun Matrix.isZero(position: Action.Position): Boolean {
    val cell = this.cellAt(position = position)
    return (cell is Empty && cell.minesAround == 0)
}

fun Matrix.getNextMatrix(position: Action.Position, cell: Cell): Matrix =
    List(SIDE) {row ->
        List(SIDE) {col ->
            if (position.row == row && position.col == col)
                cell
            else
                this.cellAt(Action.Position(row = row, col = col))
        }
    }