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
          rating += getWallNeighbourhoodRating(board, square, piece);
          rating += romanWallPieceSquareTable[(int) square.shiftWidth];
          break;
        case RomanTower:
          rating += getTowerNeighbouthoodRating(board, square, piece);
          rating += romanTowerPieceSquareTable[(int) square.shiftWidth];
          break;
        case RomanCatapult:
          rating += romanCatapultPieceSquareTable[(int) square.shiftWidth];
          break;
        case GaulWall:
          rating -= getWallNeighbourhoodRating(board, square, piece);
          rating -= gaulWallPieceSquareTable[(int) square.shiftWidth];
          break;
        case GaulTower:
          rating -= getTowerNeighbouthoodRating(board, square, piece);
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
  private static int getTowerNeighbouthoodRating(Board board, Square square, Piece piece) {
    int extraRating = 0;
    int[]cellOffsets = {1, 2};
    for (int offset: cellOffsets) {
      Piece front;
      try {
        int finalOffset = (piece == Piece.RomanTower) ? offset : -offset;
        Square frontSquare = Square.findSquareByShiftWidth(square.shiftWidth + finalOffset);
        if (square.distanceTo(frontSquare) > offset) continue;
        front = board.getPieceAt(frontSquare);
      } catch (IllegalArgumentException e) {
        continue;
      }
      if ((piece == Piece.RomanTower && front == Piece.RomanWall
          || piece == Piece.GaulTower && front == Piece.GaulWall)) {
        extraRating += 40 / offset;
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
  private static int getWallNeighbourhoodRating(Board board, Square square, Piece piece) {
    int extraRating = addExtraRatingFromAdjacentOccupiedCells(board, square, piece);
    extraRating -= subtractExtraRatingFromAdjacentEmptyCells(board, square);
    return extraRating;
  }

  private static int subtractExtraRatingFromAdjacentEmptyCells(Board board, Square square) {
    int[] cellOffsets = {-8, -7, -6, -1, 1, 6, 7, 8};
    for (int offset: cellOffsets) {
      Piece adjacent;
      try {
        Square adjacentSquare = Square.findSquareByShiftWidth(square.shiftWidth + offset);
        if (square.distanceTo(adjacentSquare) > 1) continue;
        adjacent = board.getPieceAt(adjacentSquare);
        if (adjacent != null) return 0;
      } catch (IllegalArgumentException e) {
        continue;
      }
    }

    return 40;
  }

  private static int addExtraRatingFromAdjacentOccupiedCells(Board board, Square square, Piece piece) {
    int extraRating = 0;
    int offset = 3;
    Piece behind;
    try {
      int finalOffset = (piece == Piece.RomanWall) ? -offset : offset;
      Square behindSquare = Square.findSquareByShiftWidth(square.shiftWidth + finalOffset);
      if (square.distanceTo(behindSquare) > offset) return 0;
      behind = board.getPieceAt(behindSquare);
    } catch (IllegalArgumentException e) {
      return 0;
    }

    if (piece == Piece.RomanWall && behind == Piece.RomanCatapult
        || piece == Piece.GaulWall && behind == Piece.GaulCatapult)
      extraRating += 30;
    return extraRating;
  }
}
