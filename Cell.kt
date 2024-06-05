package minesweeper

sealed class Cell(marked: Boolean = false, explored: Boolean = false) {
    protected var cellMarked: Boolean = marked
    protected var cellExplored: Boolean = explored
    val isMarked: Boolean get() = cellMarked
    val isExplored: Boolean get() = cellExplored
    fun remark() {
        cellMarked = !cellMarked
    }

    class Empty(val minesAround: Int = 0,
                     isMarked: Boolean = false,
                     isExplored: Boolean = false) : Cell(marked = isMarked, explored = isExplored) {

        fun explore() {
            cellExplored = true
            if (isMarked) remark()
        }

        override fun toString() = when {
            isMarked -> "*"
            !isExplored -> "."
            minesAround == 0 -> "/"
            else -> minesAround.toString()
        }
    }

    class Mine(isMarked: Boolean = false) : Cell(marked = isMarked) {
        override fun toString() = if (isMarked) "*" else "."
    }
}