package minesweeper

sealed class Cell(marked: Boolean = false, explored: Boolean = false) {
    protected var cellMarked: Boolean = marked
    protected var cellExplored: Boolean = explored

    fun remark() {
        cellMarked = !cellMarked
    }

    val isMarked: Boolean get() = this.cellMarked
    val isExplored: Boolean get() = this.cellMarked

    class Empty(val minesAround: Int = 0,
                isMarked: Boolean = false,
                isExplored: Boolean = false) : Cell(marked = isMarked, explored = isExplored) {

        fun explore() {
            cellExplored = true
            if (cellMarked) remark()
        }

        override fun toString() = when {
            cellMarked -> "*"
            !cellExplored -> "."
            minesAround == 0 -> "/"
            else -> minesAround.toString()
        }
    }

    class Mine(isMarked: Boolean = false) : Cell(marked = isMarked) {
        override fun toString() = if (cellMarked) "*" else "."
    }
}