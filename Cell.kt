package minesweeper

sealed class Cell(private var _marked: Boolean, protected var _explored: Boolean = false) {
    val isMarked: Boolean get() = _marked
    val isExplored: Boolean get() = _explored
    fun remark() {
        _marked = !_marked
    }
    open fun asString(state: Grid.State): String = "."

    data class Empty(val minesAround: Int = 0,
                     val marked: Boolean = false,
                     val explored: Boolean = false) : Cell(_marked = marked, _explored = explored) {

        fun explore() {
            _explored = true
            if (isMarked) remark()
        }

        override fun asString(state: Grid.State): String = when {
            isMarked -> "*"
            !isExplored -> "."
            minesAround == 0 -> "/"
            else -> minesAround.toString()
        }
    }

    data class Mine(val marked: Boolean = false) : Cell(_marked = marked) {
        override fun asString(state: Grid.State) =
            when {
                state == Grid.State.Loss -> "X"
                isMarked -> "*"
                else -> "."
            }
    }
}