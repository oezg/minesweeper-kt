package minesweeper

import kotlin.random.Random

fun main() {
    print("How many mines do you want on the field? ")
    val numberOfMines = readln().toInt()
    val minesweeper = Minesweeper(numberOfMines)
    minesweeper.print()
}

class Minesweeper(val numberOfMines: Int) {
    val minePositions = generateDistinctNumbers(numberOfMines, 9 * 9)

    fun print() {
        for (i in 0..8) {
            for (j in 0..8) {
                if (i * 9 + j in minePositions) {
                    print('X')
                } else {
                    print('.')
                }
            }
            println()
        }
    }

    fun generateDistinctNumbers(n: Int, upper: Int): Set<Int> {
        val numbers = mutableSetOf<Int>()
        while (numbers.size < n) {
            numbers.add(Random.nextInt(upper))
        }
        return numbers.toSet()
    }
}

