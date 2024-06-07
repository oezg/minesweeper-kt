package minesweeper

sealed class Cell(open val marked: Boolean, open val explored: Boolean) {
    abstract fun remark(): Cell
    abstract fun explore(): Cell
    open fun loser() = toString()
}

data class Empty(val minesAround: Int,
                 override val marked: Boolean,
                 override val explored: Boolean) : Cell(marked = marked, explored = explored) {
    override fun remark(): Cell {
        require (!explored) { "Explored empty cell can not be marked" }
        return Empty(minesAround = minesAround, marked = !marked, explored = false)
    }
    override fun explore() = Empty(minesAround = minesAround, marked = false, explored = true)
    override fun toString(): String = when {
        marked -> "*"
        !explored -> "."
        minesAround == 0 -> "/"
        else -> minesAround.toString()
    }
}

data class Mine(override val marked: Boolean, override val explored: Boolean) : Cell(marked = marked, explored = explored) {
    override fun remark(): Cell {
        require (!explored) { "Mine cannot be explored" }
        return Mine(marked = !marked, explored = false)
    }
    override fun loser() = "X"
    override fun explore() = Mine(marked = false, explored = true)
    override fun toString() = if (marked) "*" else "."
}