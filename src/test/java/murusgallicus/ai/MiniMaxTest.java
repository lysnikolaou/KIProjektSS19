package murusgallicus.ai;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import murusgallicus.core.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MiniMaxTest {

  Board board;
  BufferedReader reader;

  @BeforeEach
  void initBoard() {
    board = new Board("tttttttt/8/8/8/8/8/TTTTTTTT r");
    String fileName = "testGetOptimalMove.csv";
    File file;
    try {
      file = new File(
          Objects.requireNonNull(getClass().getClassLoader().getResource(fileName)).getFile());
      reader = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    assert reader != null;
  }

  @Test
  void testGetOptimalMove() {
    try {
      String line;
      while ((line = reader.readLine()) != null) {
        board.setBoard(line);
        System.out.println(line);
        long before = System.currentTimeMillis();
        System.out.println("Move: " + MiniMax.getOptimalMove(board,
            (board.getPlayerToMove() == 'r') ? 0 : 1, 0));
        long after = System.currentTimeMillis();
        System.out.println("Time elapsed: " + (after-before));
        System.out.println("Depth: " + MiniMax.depth);
        System.out.println("Nodes: " + MiniMax.nodes);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}
