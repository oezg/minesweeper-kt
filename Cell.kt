package minesweeper

sealed class Cell(open val marked: Boolean, open val explored: Boolean) {
    abstract fun remark(): Cell
    abstract fun explore(): Cell
    abstract fun asString(): String

    open fun loser() = "."

    data class Empty(val minesAround: Int = 0,
                     override val marked: Boolean = false,
                     override val explored: Boolean = false) : Cell(marked = marked, explored = explored) {
        override fun remark(): Cell {
            require (!explored) { "Explored empty cell can not be marked" }
            return Empty(minesAround = minesAround, marked = !marked)
        }

        override fun explore(): Cell {
            return Empty(minesAround = minesAround, marked = false, explored = true)
        }

        override fun asString(): String = when {
            marked -> "*"
            !explored -> "."
            minesAround == 0 -> "/"
            else -> minesAround.toString()
        }
    }

    data class Mine(override val marked: Boolean, override val explored: Boolean = false) : Cell(marked = marked, explored = explored) {

        override fun loser(): String {
            return "X"
        }

        override fun remark(): Cell {
            require (!explored) { "Mine cannot be explored" }
            return Mine(marked = !marked, explored = false)
        }

        override fun explore(): Cell {
            return Mine(marked = false, explored = true)
        }


        override fun asString() = when {
                marked -> "*"
                else -> "."
            }
    }
}