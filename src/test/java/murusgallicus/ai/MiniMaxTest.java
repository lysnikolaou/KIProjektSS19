package murusgallicus.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import murusgallicus.core.Board;
import murusgallicus.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class MiniMaxTest {

  private Board board;
  private BufferedReader reader;

  @BeforeEach
  void initBoard() {
    board = new Board("tttttttt/8/8/8/8/8/TTTTTTTT r");
    reader = TestUtils.loadTestData("testGetOptimalMove.csv");
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 2, 3, 4, 5})
  void testMinimaxWithoutCutoffs(int depth) {

    MiniMax.maxDepth = depth;
    MiniMax.cutOffs = false;
    String line;
    try {
      while ((line = reader.readLine()) != null) {
        if (line.charAt(0) == '#') continue;
        String[] fenAndNumber = line.split(";");
        String[] numberOfNodes = fenAndNumber[1].split(",");
        board.setBoard(fenAndNumber[0]);
        MiniMax.getOptimalMove(board, (board.getPlayerToMove() == 'r') ? 0 : 1, 0);
        if (depth <= numberOfNodes.length)
          assertEquals(Integer.parseInt(numberOfNodes[depth - 1]), MiniMax.nodes,
            "The number of generated nodes is not correct.");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    MiniMax.maxDepth = -1;
    MiniMax.cutOffs = true;
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
