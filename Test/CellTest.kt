package minesweeper.Test

import minesweeper.Empty
import minesweeper.Mine
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CellTest {
    @Test
    fun remarkEmpty() {
        val expected = Empty(minesAround = 0, isMarked = true, isExplored = false)
        val sut = Empty(minesAround = 0, isMarked = false, isExplored = false)
        assertEquals(expected, sut.remark())
    }

    @Test
    fun remarkMine() {
        val expected = Mine(isMarked = true, isExplored = false)
        val sut = Mine(isMarked = false, isExplored = false)
        assertEquals(expected, sut.remark())
    }
}