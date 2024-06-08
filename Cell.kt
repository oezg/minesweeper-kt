package minesweeper

sealed class Cell(open val isMarked: Boolean, open val isExplored: Boolean) {
    abstract fun remark(): Cell
    abstract fun explore(): Cell
    open fun loser() = toString()
}

data class Empty(val minesAround: Int,
                 override val isMarked: Boolean,
                 override val isExplored: Boolean) : Cell(isMarked = isMarked, isExplored = isExplored) {
    override fun remark(): Cell {
        require (!isExplored) { "Explored empty cell can not be marked" }
        return Empty(minesAround = minesAround, isMarked = !isMarked, isExplored = false)
    }
    override fun explore() = Empty(minesAround = minesAround, isMarked = false, isExplored = true)
    override fun toString(): String = when {
        isMarked -> "*"
        !isExplored -> "."
        minesAround == 0 -> "/"
        else -> minesAround.toString()
    }
}

data class Mine(override val isMarked: Boolean, override val isExplored: Boolean) : Cell(isMarked = isMarked, isExplored = isExplored) {
    override fun remark(): Cell {
        require (!isExplored) { "Mine cannot be explored" }
        return Mine(isMarked = !isMarked, isExplored = false)
    }
    override fun loser() = "X"
    override fun explore() = Mine(isMarked = false, isExplored = true)
    override fun toString() = if (isMarked) "*" else "."
}