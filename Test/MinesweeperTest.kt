package minesweeper.Test

import minesweeper.Action
import minesweeper.Minesweeper
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MinesweeperTest {

    @Test
    fun isValid() {
        val sut = Minesweeper(5)
        assertTrue(sut.isValid(Action.Explore(Action.Position(2, 3))))
    }

    @Test
    fun execute() {
        val expected = Action.Result.GameNotOver
        val sut = Minesweeper(5)
        assertEquals(expected, sut.execute(Action.Explore(Action.Position(2, 3))))
    }
}