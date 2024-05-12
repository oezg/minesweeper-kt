package minesweeper

import kotlin.random.Random

const val SIDE = 9

fun main() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readln().toInt()
    val minesweeper = Minesweeper(numberOfMines)
    while (minesweeper.state == Minesweeper.State.NotFinished) {
        minesweeper.printMatrix()
        val position = retrieveCoordinates(minesweeper)
        minesweeper.mark(position)
    }
    minesweeper.printMatrix()
    println("Congratulations! You found all the mines!")
}

tailrec fun retrieveCoordinates(minesweeper: Minesweeper): Pair<Int, Int> {
    print("Set/delete mines marks (x and y coordinates): ")
    val (x, y) = readln().split(" ").map { it.toInt() }
    if (minesweeper.isThereNumber(x, y)) {
        println("There is a number here!")
    } else {
        return x to y
    }
    return retrieveCoordinates(minesweeper)
}

class Minesweeper(numberOfMines: Int) {
    enum class State{
        NotFinished,
        Finished
    }

    var state = State.NotFinished
    private val minePositions = generateDistinctNumbers(numberOfMines, SIDE * SIDE)
    private val markedPositions = mutableSetOf<Int>()

    private val matrix = List(SIDE) { row ->
        MutableList(SIDE) { col ->
            if ( row * SIDE + col in minePositions )
                Cell.Mine()
            else
                Cell.Empty()
        }
    }

    init {
        val offsets = listOf(-1 to -1, -1 to 0, -1 to 1, 0 to -1, 0 to 1, 1 to -1, 1 to 0, 1 to 1)
        for (i in matrix.indices) {
            for (j in matrix[i].indices) {
                if (matrix[i][j] is Cell.Mine) {
                    continue
                }
                val adjacentMines = offsets
                    .filter { i + it.first in matrix.indices && j + it.second in matrix[i].indices  }
                    .count { matrix[i + it.first][j + it.second] is Cell.Mine }
                matrix[i][j] = Cell.Empty(minesAround =  adjacentMines)
            }
        }
    }

    fun mark(coordinate: Pair<Int, Int>) {
        val position = coordinate.first - 1 + SIDE * (coordinate.second - 1)
        if (position in markedPositions) {
            markedPositions.remove(position)
        } else {
            markedPositions.add(position)
        }
        matrix[coordinate.second - 1][coordinate.first - 1].remark()
        if (markedPositions == minePositions) {
            state = State.Finished
        }
    }

    fun isThereNumber(x: Int, y: Int): Boolean {
        return (matrix[y-1][x-1] is Cell.Empty) && (matrix[y-1][x-1] as Cell.Empty).minesAround > 0
    }

    fun printMatrix() {
        println()
        println(" │123456789│")
        println("—│—————————│")
        for ((index, row) in matrix.withIndex()) {
            print("${index + 1}|")
            for (cell in row) {
                print(cell)
            }
            println("|")
        }
        println("—│—————————│")
    }

    private fun generateDistinctNumbers(n: Int, upper: Int): Set<Int> {
        val numbers = mutableSetOf<Int>()
        while (numbers.size < n) {
            numbers.add(Random.nextInt(upper))
        }
        return numbers.toSet()
    }
}

sealed class Cell {
    open var marked: Boolean = false
    open fun remark() {
        marked = !marked
    }
    data class Empty(override var marked: Boolean = false, val minesAround: Int = 0) : Cell() {
        override fun toString() = if (minesAround > 0) "$minesAround" else if (marked) "*" else "."
    }

    data class Mine(override var marked: Boolean = false) : Cell() {
        override fun toString() = if (marked) "*" else "."
    }

}