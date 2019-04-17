package murusgallicus.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import murusgallicus.core.Board.Piece;
import org.junit.jupiter.api.Test;

class BoardTest {

  @Test
  void testBoard() {
    String initialFen = "tttttttt/8/8/8/8/8/TTTTTTTT";
    Board board = new Board(initialFen);
    for (int i = 0; i < 8; i++) {
      assertEquals(Piece.GaulTower, board.board[0][i],
          "Gaul Pieces didn't get initialized correctly.");
      assertEquals(Piece.RomanTower, board.board[6][i],
          "Roman Pieces didn't get initialized correctly.");
    }
    assertEquals(initialFen, board.toString(), "Board to FEN convertion is not correct");

  }

}
