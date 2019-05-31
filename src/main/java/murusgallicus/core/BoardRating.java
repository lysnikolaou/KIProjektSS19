package murusgallicus.core;

import java.util.HashMap;
import java.util.Map;
import murusgallicus.core.Board.Piece;
import murusgallicus.core.Board.Square;

/**
 * A class where the rating calculation of the board happens.
 */
class BoardRating {

  /**
   * The Transposition Table for quicker computation of the rating function.
   */
  private static Map<Board, Integer> transpositionTable = new HashMap<>();

  /**
   * A number that defines Checkmate in the rating function.
   */
  private static int MATE = 100000;

  /**
   * The piece square table for the roman walls.
   */
  private static int[] romanWallPieceSquareTable = {
      0, 20, 40, 60, 80, 100, MATE,
      0, 20, 40, 60, 80, 100, MATE,
      0, 20, 40, 60, 80, 100, MATE,
      0, 30, 60, 90, 120, 150, MATE,
      0, 30, 60, 90, 120, 150, MATE,
      0, 20, 40, 60, 80, 100, MATE,
      0, 20, 40, 60, 80, 100, MATE,
      0, 20, 40, 60, 80, 100, MATE
  };

  /**
   * The piece square table for the roman towers.
   */
  private static int[] romanTowerPieceSquareTable = {
      0, 10, 20, 30, 100, 0, 0,
      0, 30, 40, 60, 150, 0, 0,
      0, 30, 60, 90, 200, 0, 0,
      0, 40, 80, 100, 200, 0, 0,
      0, 40, 80, 100, 200, 0, 0,
      0, 30, 60, 90, 200, 0, 0,
      0, 30, 40, 60, 150, 0, 0,
      0, 10, 20, 30, 100, 0, 0
  };

  /**
   * The piece square table for the roman catapults.
   */
  private static int[] romanCatapultPieceSquareTable = {
      0, 15, 30, 100, 80, 0, 0,
      0, 15, 30, 100, 80, 0, 0,
      0, 25, 50, 120, 100, 0, 0,
      0, 40, 80, 150, 120, 0, 0,
      0, 40, 80, 150, 120, 0, 0,
      0, 25, 50, 120, 100, 0, 0,
      0, 15, 30, 100, 80, 0, 0,
      0, 25, 50, 120, 100, 0, 0
  };

  /**
   * The piece square table for the gaul walls.
   */
  private static int[] gaulWallPieceSquareTable = {
      MATE, 100, 80, 60, 40, 20, 0,
      MATE, 100, 80, 60, 40, 20, 0,
      MATE, 100, 80, 60, 40, 20, 0,
      MATE, 150, 120, 90, 60, 30, 0,
      MATE, 150, 120, 90, 60, 30, 0,
      MATE, 100, 80, 60, 40, 20, 0,
      MATE, 100, 80, 60, 40, 20, 0,
      MATE, 100, 80, 60, 40, 20, 0
  };

  /**
   * The piece square table for the gaul towers.
   */
  private static int[] gaulTowerPieceSquareTable = {
      0, 0, 100, 30, 20, 10, 0,
      0, 0, 150, 60, 40, 20, 0,
      0, 0, 200, 90, 60, 30, 0,
      0, 0, 200, 100, 80, 40, 0,
      0, 0, 200, 100, 80, 40, 0,
      0, 0, 200, 90, 60, 30, 0,
      0, 0, 150, 60, 40, 20, 0,
      0, 0, 100, 30, 20, 10, 0

  };

  /**
   * The piece square table for the gaul catapults.
   */
  private static int[] gaulCatapultPieceSquareTable = {
      0, 0, 80, 100, 30, 15, 0,
      0, 0, 80, 100, 30, 15, 0,
      0, 0, 100, 120, 50, 25, 0,
      0, 0, 120, 150, 80, 40, 0,
      0, 0, 120, 150, 80, 40, 0,
      0, 0, 100, 120, 50, 25, 0,
      0, 0, 80, 100, 30, 15, 0,
      0, 0, 80, 100, 30, 15, 0

  };

  /**
   * The rating function, which given a board returns which side is winning.
   * @param board The board to evaluate
   * @return An integer, which is larger, when the romans are winning, and smaller, when the
   *         gauls are winning.
   */
  static int getRating(Board board) {
    if (transpositionTable.containsKey(board)) {
      int rating = transpositionTable.get(board);
      return (board.getPlayerToMove() == 'r') ? rating : -rating;
    }

    if (board.romanTowers == 0)
      return (board.getPlayerToMove() == 'r') ? -MATE : MATE;
    else if (board.gaulTowers == 0)
      return (board.getPlayerToMove() == 'r') ? MATE : -MATE;

    int rating = 0;
    for (Square square: board.squaresFenOrder) {
      Piece piece = board.getPieceAt(square);
      if (piece == null)
        continue;

//      if ((board.romans & square.bitboardMask()) > 0) {
//        rating += piece.pieceValue;
//      } else if ((board.gauls & square.bitboardMask()) > 0) {
//        rating -= piece.pieceValue;
//      }

      switch (piece) {
        case RomanWall:
          rating += getWallNeighbourhoodRating(board, square);
          rating += romanWallPieceSquareTable[(int) square.shiftWidth];
          break;
        case RomanTower:
          rating += getTowerNeighbouthoodRating(board, square);
          rating += romanTowerPieceSquareTable[(int) square.shiftWidth];
          break;
        case RomanCatapult:
          rating += romanCatapultPieceSquareTable[(int) square.shiftWidth];
          break;
        case GaulWall:
          rating -= getWallNeighbourhoodRating(board, square);
          rating -= gaulWallPieceSquareTable[(int) square.shiftWidth];
          break;
        case GaulTower:
          rating -= getTowerNeighbouthoodRating(board, square);
          rating -= gaulTowerPieceSquareTable[(int) square.shiftWidth];
          break;
        case GaulCatapult:
          rating -= gaulCatapultPieceSquareTable[(int) square.shiftWidth];
          break;
      }
    }

    transpositionTable.put(board, rating);
    return (board.getPlayerToMove() == 'r') ? rating : -rating;
  }

  /**
   * For each tower, the tower neighbourhood gets evaluated, in order to find out what value the
   * surrounding pieces add to the current tower.
   * @param board The present board
   * @param square The square, on which the tower sits
   * @return The extra rating the tower gets, due to its surroundings
   */
  private static int getTowerNeighbouthoodRating(Board board, Square square) {
    int extraRating = 0;
    int[]cellOffsets = {1, 2};
    for (int offset: cellOffsets) {
      Piece front;
      try {
        int finalOffset = (board.playerToMove == 'r') ? offset : -offset;
        Square fronSquare = Square.findSquareByShiftWidth(square.shiftWidth + finalOffset);
        if (square.distanceTo(fronSquare) > offset) continue;
        front = board.getPieceAt(Square.findSquareByShiftWidth(square.shiftWidth - 1));
      } catch (IllegalArgumentException e) {
        continue;
      }
      if ((board.playerToMove == 'r' && front == Piece.RomanWall
          || board.playerToMove == 'g' && front == Piece.GaulWall)) {
        extraRating -= 80;
        break;
      }

    }
    return extraRating;
  }

  /**
   * For each wall, the wall neighbourhood gets evaluated, in order to find out what value the
   * surrounding pieces add to the current wall.
   * @param board The present board
   * @param square The square, on which the wall sits
   * @return The extra rating the wall gets, due to its surroundings
   */
  private static int getWallNeighbourhoodRating(Board board, Square square) {
    int extraRating = 0;
    int[]cellOffsets = {1, 2, 3};
    for (int offset: cellOffsets) {
      Piece behind;
      try {
        int finalOffset = (board.playerToMove == 'r') ? -offset : offset;
        Square behindSquare = Square.findSquareByShiftWidth(square.shiftWidth + finalOffset);
        if (square.distanceTo(behindSquare) > offset) continue;
        behind = board.getPieceAt(Square.findSquareByShiftWidth(square.shiftWidth - 1));
      } catch (IllegalArgumentException e) {
        continue;
      }
      if (offset != 3 && (board.playerToMove == 'r' && behind == Piece.RomanWall
          || board.playerToMove == 'g' && behind == Piece.GaulWall))
        extraRating += 60 / offset;
      if (offset != 1 && (board.playerToMove == 'r' && behind == Piece.RomanCatapult
          || board.playerToMove == 'g' && behind == Piece.GaulCatapult))
        extraRating += 60;
    }
    return extraRating;
  }
}
