package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.game.TicTacToeEngine
import com.example.model.AIDifficulty
import com.example.model.GameMode
import com.example.model.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun read_string_from_context() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Tic Tac Toe", appName)
  }

  @Test
  fun test_mini_mode_win_detection() {
    val board = listOf(
      Player.O, Player.O, Player.O,
      Player.X, Player.X, null,
      null, null, null
    )
    val win = TicTacToeEngine.checkWin(board, GameMode.MINI)
    assertNotNull(win)
    assertEquals(0, win?.startRow)
    assertEquals(0, win?.startCol)
    assertEquals(0, win?.endRow)
    assertEquals(2, win?.endCol)
  }

  @Test
  fun test_big_mode_win_detection() {
    val board = MutableList<Player?>(36) { null }
    // 4 in a row diagonal
    board[0 * 6 + 0] = Player.O
    board[1 * 6 + 1] = Player.O
    board[2 * 6 + 2] = Player.O
    board[3 * 6 + 3] = Player.O

    val win = TicTacToeEngine.checkWin(board, GameMode.BIG)
    assertNotNull(win)
    assertEquals(0, win?.startRow)
    assertEquals(0, win?.startCol)
    assertEquals(3, win?.endRow)
    assertEquals(3, win?.endCol)
  }

  @Test
  fun test_mega_12x12_win_and_master_ai() {
    val board = MutableList<Player?>(144) { null }
    // 5 in a row horizontal
    for (c in 3..7) {
      board[5 * 12 + c] = Player.O
    }
    val win = TicTacToeEngine.checkWin(board, GameMode.MEGA)
    assertNotNull(win)
    assertEquals(5, win?.startRow)
    assertEquals(3, win?.startCol)
    assertEquals(5, win?.endRow)
    assertEquals(7, win?.endCol)

    // Master AI move test on 12x12:
    val gameBoard = MutableList<Player?>(144) { null }
    gameBoard[6 * 12 + 6] = Player.O
    val aiMove = TicTacToeEngine.getAiMove(gameBoard, GameMode.MEGA, AIDifficulty.MASTER, Player.X)
    assertTrue("AI move must be valid and empty", aiMove in 0 until 144 && gameBoard[aiMove] == null)
  }

  @Test
  fun test_master_ai_blocks_instant_win() {
    val board = MutableList<Player?>(144) { null }
    // Player O has 4 in a row from (4, 3) to (4, 6)
    board[4 * 12 + 3] = Player.O
    board[4 * 12 + 4] = Player.O
    board[4 * 12 + 5] = Player.O
    board[4 * 12 + 6] = Player.O

    val aiMove = TicTacToeEngine.getAiMove(board, GameMode.MEGA, AIDifficulty.MASTER, Player.X)
    // Master AI MUST block either (4, 2) or (4, 7)
    val blockingMoves = listOf(4 * 12 + 2, 4 * 12 + 7)
    assertTrue("Master AI must immediately block human winning threat", aiMove in blockingMoves)
  }
}
