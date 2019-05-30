package murusgallicus.core;

import murusgallicus.core.Board.Piece;
import murusgallicus.core.Board.Rank;
import murusgallicus.core.Board.Square;

import java.util.HashMap;
import java.util.Map;

class BoardRating {

  private static Map<Board, Integer> transpositionTable = new HashMap<>();

  static int getRating(Board board) {
    if (transpositionTable.containsKey(board)) return transpositionTable.get(board);

    int rating = 0;
    for (Square square: board.squaresFenOrder) {
      Piece piece = board.getPieceAt(square);
      if (piece == null)
        continue;

      if ((board.romans & square.bitboardMask()) > 0) {
        rating += piece.pieceValue;
      } else if ((board.gauls & square.bitboardMask()) > 0) {
        rating -= piece.pieceValue;
      }

      if (piece == Piece.RomanWall) rating += getWallNeighbourhoodRating(board, square);
      if (piece == Piece.GaulWall) rating -= getWallNeighbourhoodRating(board, square);
      if (piece == Piece.RomanTower) rating += getTowerNeighbouthoodRating(board, square);
      if (piece == Piece.GaulTower) rating -= getTowerNeighbouthoodRating(board, square);
    }

    for (Rank rank: Rank.values()) {
      if ((board.romans & rank.bitboardMask()) > 0) rating += rank.index * 50;
      if ((board.gauls & rank.bitboardMask()) > 0) rating -= (6 - rank.index) * 50;
    }
    if ((board.romans & Rank.SEVENTH.bitboardMask()) >0) rating += 100000;
    if ((board.gauls & Rank.FIRST.bitboardMask()) >0) rating -= 100000;

    transpositionTable.put(board, rating);
    return rating;
  }

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
        extraRating -= 40;
        break;
      }

    }
    return extraRating;
  }

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
