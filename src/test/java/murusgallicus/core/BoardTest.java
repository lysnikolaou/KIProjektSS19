package murusgallicus.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import murusgallicus.testutils.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BoardTest {

  private Board board;
  private BufferedReader reader;

  @BeforeEach
  void initializeBoard() {
    String initialFen = "tttttttt/8/8/8/8/8/TTTTTTTT r";
    board = new Board(initialFen);

    reader = TestUtils.loadTestData("testGenerateMoves.csv");
  }

  @Test
  void testBoard() {
    String line;
    try{
      while ((line = reader.readLine()) != null) {
        if (line.charAt(0) == '#') continue;
        String fen = line.split(";")[0];
        board.setBoard(fen);
        assertEquals(fen, board.toString(),
            "Board to FEN conversion is not correct for FEN="+fen);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @Test
  void testGenerateMoves() {
    String line;
    try {
      while ((line = reader.readLine()) != null) {
        if (line.charAt(0) == '#') continue;
        String[] fenAndMoves = line.split(";");
        String fen = fenAndMoves[0];

        String[] expectedMoves;
        if (fenAndMoves.length < 2) {
          expectedMoves = new String[0];
        } else {
          expectedMoves = fenAndMoves[1].split(",");
        }

        board.setBoard(fen);
        String[] actualMoves = board.generateMoves();

        List<String> expectedMovesList = Arrays.asList(expectedMoves);
        List<String> actualMovesList = Arrays.asList(actualMoves);

        Collections.sort(actualMovesList);
        Collections.sort(expectedMovesList);
        assertEquals(expectedMovesList, actualMovesList,
            "Move lists do not contain the same elements for FEN=" + fen);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testExecuteMove() {
    reader = TestUtils.loadTestData("testExecuteMove.csv");
    String line;
    try {
      while ((line = reader.readLine()) != null) {
        String[] cols = line.split(";");
        board.setBoard(cols[0]);
        System.out.println(cols[1]);
        board.executeMove(cols[1]);
        assertEquals(cols[2], board.toString(), "Execute move not correct for FEN=" + cols[0]);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @ParameterizedTest
  @ValueSource(ints = {10000, 100000, 1000000})
  void testGetRatingPerformance(int nrOfExecutions) {
    board.setBoard("tttttttt/8/8/8/8/8/TTTTTTTT r");
    String[] moves = board.generateMoves();
    long before = System.currentTimeMillis();
    for (int i = 0; i < nrOfExecutions; i++) {
      board.getRating();
    }
    long after = System.currentTimeMillis();
    System.out.println("Time elapsed: " + (after-before));
    System.out.println("Average time per iteration: " + (after-before)/(double)nrOfExecutions);
  }

  @ParameterizedTest
  @ValueSource(strings = {
          "tttttttt/8/8/8/8/8/TTTTTTTT r",
          "tttttttt/8/8/8/5W2/6W1/TTTTTTT1 g",
          "ttttttt1/6w1/5w2/8/5W2/6W1/TTTTTTT1 r",
          "1ww3w1/2wwwww1/3www2/4w3/3WW2W/2WWWWW1/T5WW g",
          "5ww1/2w1wwW1/w1w1wWT1/2wt3W/1wWwwW2/2Tw1WW1/T6T r",
          "ttt1tttt/3w4/3w4/8/2W5/1W6/1TTTTTTT r",
          "ttt2ttt/3ww3/3ww3/8/2T5/1W1W4/1TTT1TTT r",
          "1tt3tc/w2ww3/w1Tww3/2Ww4/8/1WWW4/1T1T1TTT r"

  })
  void testGetRatingResult(String fen) {
    board.setBoard(fen);
    System.out.print("Rating for FEN=" + fen + ": ");
    System.out.println(board.getRating());
  }

}
