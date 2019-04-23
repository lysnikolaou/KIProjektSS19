package murusgallicus.util;

import java.util.HashMap;
import java.util.Map;

public class SquareToStringConverter {


  /**
   * A method to convert to a string representation of a square, given its coordinates.
   * @param row The row index of the square
   * @param column The column index of the square
   * @return The string representation of the square
   */
  public static String squareToString(int row, int column) {
    Map<Integer, Character> columns = new HashMap<>();
    columns.put(0, 'a');
    columns.put(1, 'b');
    columns.put(2, 'c');
    columns.put(3, 'd');
    columns.put(4, 'e');
    columns.put(5, 'f');
    columns.put(6, 'g');
    columns.put(7, 'h');

    return Character.toString(columns.get(column)) + (7 - row);

  }
}
