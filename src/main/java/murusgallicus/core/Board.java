package murusgallicus.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The board class, represented as a bitboard.
 */
public class Board {

  /**
   * Bitboard for the gaul walls.
   */
  long gaulWalls;
  /**
   * Bitboard for the gaul towers.
   */
  long gaulTowers;
  /**
   * Bitboard for the gaul catapults.
   */
  long gaulCatapults;
  /**
   * Bitboard for all the gaul pieces.
   */
  long gauls;

  /**
   * Bitboard for the roman walls.
   */
  long romanWalls;
  /**
   * Bitboard for the roman towers.
   */
  long romanTowers;
  /**
   * Bitboard for the roman catapults.
   */
  long romanCatapults;
  /**
   * Bitboard for all the roman pieces.
   */
  long romans;

  /**
   * Bitboard for all the walls.
   */
  long walls;
  /**
   * Bitboard for all the towers.
   */
  long towers;
  /**
   * Bitboard for all the catapults.
   */
  long catapults;

  /**
   * Bitboard for all the pieces on the board.
   */
  long occupied;

  /**
   * The player to move ('r' for Romans, 'g' for Gauls)
   */
  char playerToMove;

  /**
   * An enum to represent all the pieces.
   */
  enum Piece {
    GaulWall(120, 'w', 3),
    GaulTower(300, 't', 4),
    GaulCatapult(500, 'c', 5),
    RomanWall(120, 'W', 0),
    RomanTower(300, 'T', 1),
    RomanCatapult(500, 'C', 2);

    final int pieceValue;
    final char fenChar;
    final int zobristIndex;
    Piece(int pieceValue, char fenChar, int zobristIndex) {
      this.pieceValue = pieceValue;
      this.fenChar = fenChar;
      this.zobristIndex = zobristIndex;
    }
  }

  /**
   * An enum to represent all the squares of the board.
   */
  enum Square {
    a7(6), b7(13), c7(20), d7(27), e7(34), f7(41), g7(48), h7(55),
    a6(5), b6(12), c6(19), d6(26), e6(33), f6(40), g6(47), h6(54),
    a5(4), b5(11), c5(18), d5(25), e5(32), f5(39), g5(46), h5(53),
    a4(3), b4(10), c4(17), d4(24), e4(31), f4(38), g4(45), h4(52),
    a3(2), b3(9), c3(16), d3(23), e3(30), f3(37), g3(44), h3(51),
    a2(1), b2(8), c2(15), d2(22), e2(29), f2(36), g2(43), h2(50),
    a1(0), b1(7), c1(14), d1(21), e1(28), f1(35), g1(42), h1(49);

    final long shiftWidth;
    long squareFile() { return shiftWidth / 7; }
    long squareRank() { return shiftWidth % 7; }
    Square(int shiftWidth) { this.shiftWidth = shiftWidth; }
    long bitboardMask() { return 1L << shiftWidth; }
    int distanceTo(Square other) {
      return (int) Math.max(Math.abs(squareFile() - other.squareFile()),
          Math.abs(squareRank() - other.squareRank()));
    }
    Square getInterveningSquare(Square other) {
      long newShiftWidth = (shiftWidth + other.shiftWidth) / 2;
      return findSquareByShiftWidth(newShiftWidth);
    }
    static Square findSquareByShiftWidth(long shiftWidth) {
      for (Square square: Square.values()) {
        if (square.shiftWidth == shiftWidth) return square;
      }
      throw new IllegalArgumentException("There isn't a square with this shiftwidth");
    }
  }

  /**
   * An enum to represent all the files of the board.
   */
  enum File {
    A(0), B(1), C(2), D(3), E(4), F(5), G(6), H(7);

    final int index;
    File(int index) { this.index = index; }
    long bitboardMask() { return 0x7FL << 7*index; }
  }

  /**
   * An enum to represent all the ranks of the board.
   */
  enum Rank {
    FIRST(0), SECOND(1), THIRD(2), FOURTH(3), FIFTH(4), SIXTH(5), SEVENTH(6);
    final int index;
    Rank(int index) { this.index = index; }
    long bitboardMask() { return 0x2040810204081L << index; }
  }

  /**
   * A list containig all the squares of the board in the order that they appears in the FEN string.
   */
  Square[] squaresFenOrder = Square.values();

  /**
   * A list containing 6*56+1 random numbers for Zobrist hashing.
   * 6*56 for each piece(6) on each square(56=7*8)
   * 1 for the side to move
   */
  int[] zobristKeys = new int[6*56+1];

  /**
   * The Constructor of the Board class.
   * @param fen The fen string that the board needs to accord to
   */
  public Board(String fen) {
    setBoard(fen);
    fillZobristKeys();
  }

  /**
   * Sets all the bitboard to their values according to the fen string given.
   * @param fen The fen string that the board needs to accord to
   */
  public void setBoard(String fen) {
    String[] boardAndPlayer = fen.split(" ");
    String board = boardAndPlayer[0];
    playerToMove = boardAndPlayer[1].charAt(0);

    int squareCounter = 0;
    for (Character c: board.toCharArray()) {
      if (Character.isDigit(c)) {
        for (int i = squareCounter; i < squareCounter + Character.getNumericValue(c); i++) {
          removePieceAt(squaresFenOrder[i]);
        }
        squareCounter += Character.getNumericValue(c);
      } else if (c != '/') {
        removePieceAt(squaresFenOrder[squareCounter]);
        setPieceAtSquare(c, squaresFenOrder[squareCounter++]);
      }
    }
  }

  /**
   * Fill zobrist keys array with random number for zobrist hashing.
   */
  private void fillZobristKeys() {
    Random random = new Random();
    for (int i = 0; i < zobristKeys.length; i++) {
      zobristKeys[i] = random.nextInt();
    }
  }

  /**
   * Getter for the player
   */
  public char getPlayerToMove() {
    return playerToMove;
  }

  public int[] bitify() {
    int[] result = new int[337];
    for (int i = 55; i <= 0; i--) {
      result[i] = ((gaulWalls & (1 << i)) != 0) ? 1 : 0;
      result[56+i] = ((gaulTowers & (1 << i)) != 0) ? 1 : 0;
      result[2*56+i] = ((gaulCatapults & (1 << i)) != 0) ? 1 : 0;
      result[3*56+i] = ((romanWalls & (1 << i)) != 0) ? 1 : 0;
      result[4*56+i] = ((romanTowers & (1 << i)) != 0) ? 1 : 0;
      result[5*56+i] = ((romanCatapults & (1 << i)) != 0) ? 1 : 0;
    }


    result[336] = (getPlayerToMove() == 'r') ? 0 : 1;
    return result;
  }

  /**
   * Update the bitboard to remove a piece at a certain square.
   * @param square The square, whose piece gets removed
   */
  private void removePieceAt(Square square) {
    Piece piece = getPieceAt(square);
    if (piece == null) return;

    switch (piece) {
      case GaulWall:
        gaulWalls &= ~square.bitboardMask();
        walls &= ~square.bitboardMask();
        gauls &= ~square.bitboardMask();
        break;
      case GaulTower:
        gaulTowers &= ~square.bitboardMask();
        towers &= ~square.bitboardMask();
        gauls &= ~square.bitboardMask();
        break;
      case GaulCatapult:
        gaulCatapults &= ~square.bitboardMask();
        catapults &= ~square.bitboardMask();
        gauls &= ~square.bitboardMask();
        break;
      case RomanWall:
        romanWalls &= ~square.bitboardMask();
        walls &= ~square.bitboardMask();
        romans &= ~square.bitboardMask();
        break;
      case RomanTower:
        romanTowers &= ~square.bitboardMask();
        towers &= ~square.bitboardMask();
        romans &= ~square.bitboardMask();
        break;
      case RomanCatapult:
        romanCatapults &= ~square.bitboardMask();
        catapults &= ~square.bitboardMask();
        romans &= ~square.bitboardMask();
        break;
    }

    occupied &= ~square.bitboardMask();
  }

  /**
   * The bitboard-to-FEN converter for the board.
   * @return The FEN representation of the board
   */
  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    int empty = 0;
    for (Square square: squaresFenOrder) {
      Piece pieceAtSquare = getPieceAt(square);
      if (pieceAtSquare == null) {
        empty++;
      } else {
        if (empty > 0) {
          builder.append(empty);
          empty = 0;
        }
        builder.append(pieceAtSquare.fenChar);
      }

      if ((square.bitboardMask() & File.H.bitboardMask()) > 0) {
        if (empty > 0) {
          builder.append(empty);
          empty = 0;
        }

        if (square != Square.h1) {
          builder.append('/');
          empty = 0;
        }
      }
    }

    builder.append(' ');
    builder.append(playerToMove);
    return builder.toString();
  }

  /**
   * Hash code to be used in the transposition table
   */
  @Override
  public int hashCode() {
    int hashCode = 0;
    for (Square square: squaresFenOrder) {
      Piece piece = getPieceAt(square);
      if (piece != null)
        hashCode ^= zobristKeys[piece.zobristIndex * 56 + (int) square.shiftWidth];
    }

    if (playerToMove == 'g') hashCode ^= zobristKeys[336];
    return hashCode;
  }

  /**
   * Execute a move.
   * @param moveToBeExecuted The string representation of the move
   */
  public void executeMove(String moveToBeExecuted) {
    Move move = null;
    String[] parts = moveToBeExecuted.split("-");
    if (parts.length == 2)
      move = new Move(Square.valueOf(parts[0]), Square.valueOf(parts[1]), -1);
    else if (parts.length == 3)
      move = new Move(Square.valueOf(parts[0]), Square.valueOf(parts[1]), Integer.parseInt(parts[2]));
    assert move != null;

    Piece piece = getPieceAt(move.getSourceSquare());
    if (piece == Piece.GaulTower || piece == Piece.RomanTower) executeTowerMove(move);
    else if (piece == Piece.GaulCatapult || piece == Piece.RomanCatapult) executeCatapultMove(move);

    playerToMove = (playerToMove == 'r') ? 'g' : 'r';
  }

  /**
   * Helper method to execute a catapult move.
   * @param move The move to be executed
   */
  private void executeCatapultMove(Move move) {
    downgradeCatapult(move.getSourceSquare(), 1);
    Piece attackedPiece = getPieceAt(move.getDestinationSquare());
    if (attackedPiece == null) {
      setPieceAtSquare((playerToMove == 'r') ? 'C' : 'c', move.getDestinationSquare());
    } else if ((walls & move.getDestinationSquare().bitboardMask()) > 0) {
      removePieceAt(move.getDestinationSquare());
    } else if ((towers & move.getDestinationSquare().bitboardMask()) > 0) {
      downgradeTower(move.getDestinationSquare(), 1);
    } else if ((catapults & move.getDestinationSquare().bitboardMask()) > 0) {
      downgradeCatapult(move.getDestinationSquare(), 1);
    }
  }

  /**
   * Helper method to execute a tower move.
   * @param move The move to be executed
   */
  private void executeTowerMove(Move move) {
    int distance = move.getSourceSquare().distanceTo(move.getDestinationSquare());
    if (distance == 2) {
      removePieceAt(move.getSourceSquare());
      upgradePiece(move.getSourceSquare().getInterveningSquare(move.getDestinationSquare()));
      upgradePiece(move.getDestinationSquare());
    } else if (distance == 1) {
      if ((walls & move.getDestinationSquare().bitboardMask()) > 0) {
        downgradeTower(move.getSourceSquare(), 1);
        removePieceAt(move.getDestinationSquare());
      } else if ((catapults & move.getDestinationSquare().bitboardMask()) > 0) {
        downgradeTower(move.getSourceSquare(), move.getNumberOfPiecesMoved());
        downgradeCatapult(move.getDestinationSquare(), move.getNumberOfPiecesMoved());
      }
    }
  }

  /**
   * Downgrade a tower to another piece.
   * @param square The square of the tower
   * @param amount 1 downgrades the tower to a wall, 2 removes the tower
   */
  private void downgradeTower(Square square, int amount) {
    removePieceAt(square);
    if (amount == 1) setPieceAtSquare((playerToMove == 'r') ? 'W' : 'w', square);
  }

  /**
   * Downgrade a catapult to another piece.
   * @param square The square of the catapult
   * @param amount 1 downgrades the catapult to a tower, 2 downgrades it to a wall
   */
  private void downgradeCatapult(Square square, int amount) {
    removePieceAt(square);
    if (amount == 1) {
      setPieceAtSquare((playerToMove == 'r') ? 'T' : 't', square);
    } else if (amount == 2) {
      setPieceAtSquare((playerToMove == 'r') ? 'W' : 'w', square);
    }
  }

  /**
   * Upgrade a piece to the next strongest one.
   * @param square The square of the piece
   */
  private void upgradePiece(Square square) {
    Piece piece = getPieceAt(square);
    if (piece == null) {
      setPieceAtSquare((playerToMove == 'r') ? 'W' : 'w', square);
      return;
    }
    removePieceAt(square);
    switch (piece) {
      case GaulWall:
        setPieceAtSquare('t', square);
        break;
      case RomanWall:
        setPieceAtSquare('T', square);
        break;
      case GaulTower:
        setPieceAtSquare('c', square);
        break;
      case RomanTower:
        setPieceAtSquare('C', square);
        break;
    }
  }

  /**
   * Rating function of the board.
   * @return The rating of the board, negative if gauls win, positive otherwise
   */
  public int getRating() {
    return BoardRating.getRating(this);
  }

  /**
   * The move generator.
   * @return A string array that contatins all the moves, decoded according to the rules defined
   */
  public String[] generateMoves() {
    List<Move> moves = new ArrayList<>();
    long piecesToMove = (playerToMove == 'r') ? romans : gauls;

    // Catapult Moves
    List<Square> catapultToMoveSquares = getSquaresWithPieces(piecesToMove & catapults);
    for (Square square: catapultToMoveSquares) {
      generateCatapultMovesFromSquare(square, moves);
    }

    // Tower Moves
    List<Square> towerToMoveSquares = getSquaresWithPieces(piecesToMove & towers);
    for (Square square: towerToMoveSquares) {
      generateTowerMovesFromSquare(square, moves);
    }

    return moveListToStringArray(moves);
  }

  /**
   * Given a square that has a catapult, all its moves are generated.
   * @param srcSquare The square of the catapult
   * @param moves All of its moves
   */
  private void generateCatapultMovesFromSquare(Square srcSquare, List<Move> moves) {
    for (int adjacentCellIndex: getCatapultAdjacentCellsIndexes(srcSquare)) {
      Square destSquare = Square.findSquareByShiftWidth(srcSquare.shiftWidth + adjacentCellIndex);
      Piece pieceAtAdjacentCell = getPieceAt(destSquare);
      if (pieceAtAdjacentCell == null
          || (playerToMove == 'r' && (gauls & destSquare.bitboardMask()) > 0)
          || (playerToMove == 'g' && (romans & destSquare.bitboardMask()) > 0)) {
        moves.add(new Move(srcSquare, destSquare, -1));
      }
    }

  }

  /**
   * Get all the possible squares, where a catapult could land its stones.
   * @param srcSquare The square of the catapult
   * @return All the possible squares
   */
  private List<Integer> getCatapultAdjacentCellsIndexes(Square srcSquare) {
    List<Integer> cellsDistanceTwo = new ArrayList<>(Arrays.asList(-14, 14));
    if (playerToMove == 'r') {
      cellsDistanceTwo.addAll(Arrays.asList(-12, 2, 16));
    } else {
      cellsDistanceTwo.addAll(Arrays.asList(-16, -2, 12));
    }

    List<Integer> intermediateList = new ArrayList<>();
    for (int cell: cellsDistanceTwo) {
      try {
        if (srcSquare.distanceTo(Square.findSquareByShiftWidth(srcSquare.shiftWidth + cell)) <= 2)
          intermediateList.add(cell);
      } catch (IllegalArgumentException e) {
        continue;
      }
    }

    List<Integer> finalList = new ArrayList<>();
    for (int cell: intermediateList) {
      finalList.add(cell);
      try {
        Square cellDistanceTwo = Square.findSquareByShiftWidth(srcSquare.shiftWidth + cell);
        Square newCell = Square.findSquareByShiftWidth(cellDistanceTwo.shiftWidth + cell/2);
        if (cellDistanceTwo.distanceTo(newCell) == 1) {
          finalList.add(cell + cell/2);
        }
      } catch (IllegalArgumentException e) {
        continue;
      }
    }

    return finalList;
  }

  /**
   * Given a square that has a tower, all its moves are generated.
   * @param srcSquare The square of the tower
   * @param moves All of its moves
   */
  private void generateTowerMovesFromSquare(Square srcSquare, List<Move> moves) {
    for (int adjacentCellIndex: getTowerAdjacentCellsIndexes(srcSquare)) {
      Square destSquare = Square.findSquareByShiftWidth(srcSquare.shiftWidth + adjacentCellIndex);
      Piece pieceAtAdjacentCell = getPieceAt(destSquare);
      if (playerToMove == 'r' && pieceAtAdjacentCell == Piece.GaulCatapult
          || playerToMove == 'g' && pieceAtAdjacentCell == Piece.RomanCatapult) {
        moves.add(new Move(srcSquare, destSquare, 1));
        moves.add(new Move(srcSquare, destSquare, 2));
      } else if (playerToMove == 'r' && pieceAtAdjacentCell == Piece.GaulWall
          || playerToMove == 'g' && pieceAtAdjacentCell == Piece.RomanWall) {
        moves.add(new Move(srcSquare, destSquare, -1));
      } else if (pieceAtAdjacentCell == null
          || (playerToMove == 'r' && (pieceAtAdjacentCell == Piece.RomanWall || pieceAtAdjacentCell == Piece.RomanTower))
          || (playerToMove == 'g' && (pieceAtAdjacentCell == Piece.GaulWall || pieceAtAdjacentCell == Piece.GaulTower))) {
        checkAndGenerateTowerSilentMove(srcSquare, adjacentCellIndex, moves);
      }
    }

  }

  /**
   * Get all the possible squares, where a tower could land its stones.
   * @param square The square of the tower
   * @return All the possible squares
   */
  private List<Integer> getTowerAdjacentCellsIndexes(Square square) {
    List<Integer> adjacentCellsIndexes = new ArrayList<>();
    if (square == Square.a7) adjacentCellsIndexes.addAll(Arrays.asList(-1, 6, 7));
    else if (square == Square.a1) adjacentCellsIndexes.addAll(Arrays.asList(1, 7, 8));
    else if (square == Square.h7) adjacentCellsIndexes.addAll(Arrays.asList(-1, -7, -8));
    else if (square == Square.h1) adjacentCellsIndexes.addAll(Arrays.asList(1, -7, -6));
    else if ((square.bitboardMask() & File.A.bitboardMask()) > 0)
      adjacentCellsIndexes.addAll(Arrays.asList(1, 6, 8, 7, -1));
    else if ((square.bitboardMask() & File.H.bitboardMask()) > 0)
      adjacentCellsIndexes.addAll(Arrays.asList(1, -7, -8, -6, -1));
    else if ((square.bitboardMask() & Rank.FIRST.bitboardMask()) > 0)
     adjacentCellsIndexes.addAll(Arrays.asList(-6, -7, 1, 7, 8));
    else if ((square.bitboardMask() & Rank.SEVENTH.bitboardMask()) > 0)
      adjacentCellsIndexes.addAll(Arrays.asList(-8, -7, -1, 7, 6));
    else adjacentCellsIndexes.addAll(Arrays.asList(-7, -8, -6, -1, 1, 7, 8, 6));
    return adjacentCellsIndexes;
  }

  /**
   * Checks the second square needed for a silent tower move.
   * @param srcSquare The square of the tower
   * @param adjacentCellIndex The first square of the tower's move
   * @param moves The array containing all the moves
   */
  private void checkAndGenerateTowerSilentMove(Square srcSquare, int adjacentCellIndex,
      List<Move> moves) {
    try {
      Square destSquare = Square.findSquareByShiftWidth(srcSquare.shiftWidth + 2 * adjacentCellIndex);
      if (srcSquare.distanceTo(destSquare) > 2) return;
      Piece piece = getPieceAt(destSquare);
      if (piece == null
          || (playerToMove == 'r' && (piece == Piece.RomanWall || piece == Piece.RomanTower))
          || (playerToMove == 'g' && (piece == Piece.GaulWall || piece == Piece.GaulTower)))
        moves.add(new Move(srcSquare, destSquare, -1));
    } catch (IllegalArgumentException e) {
      return;
    }
  }

  /**
   * Get all the square on the board, on which a certain type of piece is located
   */
  private List<Square> getSquaresWithPieces(long pieces) {
    List<Square> squares = new ArrayList<>();

    long i = 0;
    while (pieces > 0) {
      if ((pieces & 1) == 1) squares.add(Square.findSquareByShiftWidth(i));
      pieces >>= 1;
      i++;
    }

    return squares;
  }

  /**
   * Set piece at the given square.
   * @param c The FEN character of the piece
   * @param square The square, onto which the piece has to be placed
   */
  private void setPieceAtSquare(char c, Square square) {
    switch (c) {
      case 'w':
        gaulWalls |= square.bitboardMask();
        walls |= square.bitboardMask();
        gauls |= square.bitboardMask();
        break;
      case 't':
        gaulTowers |= square.bitboardMask();
        towers |= square.bitboardMask();
        gauls |= square.bitboardMask();
        break;
      case 'c':
        gaulCatapults |= square.bitboardMask();
        catapults |= square.bitboardMask();
        gauls |= square.bitboardMask();
        break;
      case 'W':
        romanWalls |= square.bitboardMask();
        walls |= square.bitboardMask();
        romans |= square.bitboardMask();
        break;
      case 'T':
        romanTowers |= square.bitboardMask();
        towers |= square.bitboardMask();
        romans |= square.bitboardMask();
        break;
      case 'C':
        romanCatapults |= square.bitboardMask();
        catapults |= square.bitboardMask();
        romans |= square.bitboardMask();
        break;
    }
    occupied |= square.bitboardMask();
  }

  private String[] moveListToStringArray(List<Move> list) {
    String[] rv = new String[list.size()];
    int i = 0;
    for (Move m: list) rv[i++] = m.toString();
    return rv;
  }

  /**
   * Get the piece type of a square.
   * @param square The square, whose piece type has to be found
   * @return The piece type of the square's piece
   */
  Piece getPieceAt(Square square) {

    if ((occupied & square.bitboardMask()) == 0) {
      return null;
    } else if ((romanWalls & square.bitboardMask()) > 0) {
      return Piece.RomanWall;
    } else if ((romanTowers & square.bitboardMask()) > 0) {
      return Piece.RomanTower;
    } else if ((romanCatapults & square.bitboardMask()) > 0) {
      return Piece.RomanCatapult;
    } else if ((gaulWalls & square.bitboardMask()) > 0) {
      return Piece.GaulWall;
    } else if ((gaulTowers & square.bitboardMask()) > 0) {
      return Piece.GaulTower;
    } else if ((gaulCatapults & square.bitboardMask()) > 0) {
      return Piece.GaulCatapult;
    }

    throw new IllegalArgumentException("Square is invalid");

  }

  /**
   * Override the equals method to check equality of all the bitboards.
   * @param other The other board to check equality to
   * @return true, if equal Boards, false otherwise
   */
  @Override
  public boolean equals(Object other) {
    if (other instanceof Board) {
      return (gaulWalls == ((Board) other).gaulWalls
          && gaulTowers == ((Board) other).gaulTowers
          && gaulCatapults == ((Board) other).gaulCatapults
          && romanWalls == ((Board) other).romanWalls
          && romanTowers == ((Board) other).romanTowers
          && romanCatapults == ((Board) other).romanCatapults
          && playerToMove == ((Board) other).playerToMove);
    }
    return false;
  }
}
