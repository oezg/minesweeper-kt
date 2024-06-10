package minesweeper.Test

import minesweeper.Action
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ActionTest {
    @Test
    fun getPosition() {
        val expected = Action.Position(row = 7, col = 0)
        assertEquals(expected, Action.Position(row = 7, col = 0))
    }

    @Test
    fun neighbors() {
        val expected = listOf(
            Action.Position(row = 6, col = 0),
            Action.Position(row = 6, col = 1),
            Action.Position(row = 7, col = 1),
            Action.Position(row = 8, col = 0),
            Action.Position(row = 8, col = 1),
            )
        assertEquals(expected, Action.Position(row = 7, col = 0).neighbors)
    }

}