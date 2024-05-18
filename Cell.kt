package minesweeper

sealed class Cell {
    open var isMarked: Boolean = false
    open fun remark() {
        isMarked = !isMarked
    }
    open var isExplored: Boolean = false

    data class Empty(val minesAround: Int = 0,
                     override var isMarked: Boolean = false,
                     override var isExplored: Boolean = false) : Cell() {

        fun explore() {
            isExplored = true
            if (isMarked) remark()
        }

        override fun toString() = when {
            isMarked -> "*"
            !isExplored -> "."
            minesAround == 0 -> "/"
            else -> minesAround.toString()
        }
    }

    data class Mine(override var isMarked: Boolean = false) : Cell() {
        override fun toString() = if (isMarked) "*" else "."
    }
}