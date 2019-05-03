package murusgallicus.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardTest {

  private Board board;
  private BufferedReader reader;

  @BeforeEach
  void initializeBoard() {
    String initialFen = "tttttttt/8/8/8/8/8/TTTTTTTT r";
    board = new Board(initialFen);

    String fileName = "testGenerateMoves.csv";
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

}
