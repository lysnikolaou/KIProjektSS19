package murusgallicus.util;

import java.util.ArrayList;
import murusgallicus.core.Move;

public class MovesToStringConverter {

  /**
   * A method to convert a list of Moves to their string representations
   * @param moves A list containing all the moves that need to be converted
   * @return An array with all the string representation of the moves
   */
  public static String[] movesToString(ArrayList<Move> moves) {
    String[] strMoves = new String[moves.size()];

    int i = 0;
    for (Move move: moves) {
      if (move.getNumberOfPiecesMoved() == -1)
        strMoves[i++] = move.getSourceSquare() + "-" + move.getDestinationSquare();
      else
        strMoves[i++] = move.getSourceSquare() + "-" + move.getDestinationSquare() + "-" +
            move.getNumberOfPiecesMoved();
    }

    return strMoves;
  }
}
