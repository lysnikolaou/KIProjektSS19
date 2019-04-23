package murusgallicus.core;

import murusgallicus.util.SquareToStringConverter;

import java.util.ArrayList;
import java.util.Collection;

import static murusgallicus.util.SquareToStringConverter.squareToString;


class Board {

  /**
   * An enum that contains all the piece types.
   */
  enum Piece {
    RomanWall,
    RomanTower,
    RomanCatapult,
    GaulWall,
    GaulTower,
    GaulCatapult
  }

  /**
   * The board.
   */
  Piece[][] board;

  /**
   * murusgallicus.base.Board constructor that converts a FEN string to its board representation.
   * @param fen The FEN representation of the board
   */
  Board(String fen) {
    board = new Piece[7][8];
    setBoard(fen);
  }

  /**
   * Set board array from FEN string.
   * @param fen THE FEN string that should be stored into the board
   */
  private void setBoard(String fen) {
    int row = 0;
    int column = 0;
    int fenLength = fen.length();

    for (int i = 0; i < fenLength; i++) {
      char currentChar = fen.charAt(i);
      if (currentChar == '/') {
        column = 0;
        row++;
      }
      else if (Character.isDigit(currentChar)) {
        column += Character.getNumericValue(currentChar);
        column--;
      }
      else
        setPieceFromChar(currentChar, row, column++);
    }
  }

  /**
   * The actual move generator.
   * @param player The player, whose turn it is to play('r' for Romans, 'g' for Gauls)
   * @return A list containing all the available moves
   */
  ArrayList<Move> generateMoves(char player) {
    if (player == 'r') {
      return generateRomanMoves();
    } else if (player == 'g') {
      return generateGaulMoves();
    } else {
      throw new IllegalArgumentException("Player must be either 'r' for Romans or 'g' for Gauls.");
    }
  }

  /**
   * The move generator for the Roman player.
   * @return A list containing all the available moves for the roman player
   */
  private ArrayList<Move> generateRomanMoves() {
    ArrayList<Move> moves = new ArrayList<>();
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[0].length; j++) {
        if (board[i][j] == Piece.RomanTower)
          moves.addAll(generateRomanTowerMoves());
        else if (board[i][j] == Piece.RomanCatapult)
          moves.addAll(generateRomanCatapultMoves());
      }
    }
    return moves;
  }

  /**
   * The move generator for the Gaul player.
   * @return A list containing all the available moves for the gaul player
   */
  private ArrayList<Move> generateGaulMoves() {
    ArrayList<Move> moves = new ArrayList<>();
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[0].length; j++) {
        if (board[i][j] == Piece.GaulTower)
          moves.addAll(generateGaulTowerMoves());
        else if (board[i][j] == Piece.GaulCatapult)
          moves.addAll(generateGaulCatapultMoves());
      }
    }
    return moves;
  }

  private ArrayList<Move> generateGaulTowerMoves() {
    return null;
  }

  private void checkGaulCatapultMove(int row, int col, int rowOff, int colOff, ArrayList<Move> moves) {
    if (board[row + rowOff][col + colOff ] == NULL || isRomanPiece(row + rowOff,col + colOff)) {
      Move move = new Move(squareToString(row,col), squareToString(row + rowOff,col + colOff), 1);
      moves.add(move);
    }
  }

  private ArrayList<Move> generateGaulCatapultMoves(int row, int col) {
    ArrayList<Move> moves = new ArrayList<>();
    if(col > 2) { // throw left
      checkGaulCatapultMove(row, col, 0, -2, moves);
      checkGaulCatapultMove(row, col, 0, -3, moves);

      if(row > 2) { // throw left-forward
        checkGaulCatapultMove(row, col, -2, -2, moves);
        checkGaulCatapultMove(row, col, -3, -3, moves);
      }
    }

    if(row > 2) { // throw forward
      checkGaulCatapultMove(row, col, -2, 0, moves);
      checkGaulCatapultMove(row, col, -3, 0, moves);
    }

    if(col < 5) { // throw right
      checkGaulCatapultMove(row, col, 0, 2, moves);
      checkGaulCatapultMove(row, col, 0, 3, moves);


      if(row > 2) { // throw right-forward
        checkGaulCatapultMove(row, col, -2, 2, moves);
        checkGaulCatapultMove(row, col, -3, 3, moves);
      }
    }
    return moves;
  }

  private ArrayList<Move> generateRomanTowerMoves() {
    return null;
  }

  private void checkRomanCatapultMove(int row, int col, int rowOff, int colOff, ArrayList<Move> moves) {
    if (board[row + rowOff][col + colOff ] == NULL || isGaulPiece(row + rowOff,col + colOff)) {
      Move move = new Move(squareToString(row,col), squareToString(row + rowOff,col + colOff), 1);
      moves.add(move);
    }
  }

  private ArrayList<Move> generateRomanCatapultMoves(int row, int col) {
    ArrayList<Move> moves = new ArrayList<>();
    if (col > 2) { // throw left
      checkRomanCatapultMove(row, col, 0, -2, moves);
      checkRomanCatapultMove(row, col, 0, -3, moves);

      if (row < 5) { // throw left-forward
        checkRomanCatapultMove(row, col, 2, -2, moves);
        checkRomanCatapultMove(row, col, 3, -3, moves);
      }
    }

    if (row < 4) { // throw forward
      checkRomanCatapultMove(row, col, 2, 0, moves);
      checkRomanCatapultMove(row, col, 3, 0, moves);
    }

    if(col < 5) { // throw right
      checkGaulCatapultMove(row, col, 0, 2, moves);
      checkGaulCatapultMove(row, col, 0, 3, moves);

      if(row < 4) { // throw right-forward
        checkGaulCatapultMove(row, col, 2, 2, moves);
        checkGaulCatapultMove(row, col, 3, 3, moves);
      }


    }
  }

  /**
   * Convert the board to a FEN string.
   * @return The FEN representation of the board
   */
  @Override
  public String toString() {
    StringBuilder fenBuilder = new StringBuilder();
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[0].length; j++) {
        if (board[i][j] == null) {
          j += countEmptySquaresAndUpdateFen(i, j, fenBuilder);
          j--;
        } else {
          fenBuilder.append(getCharFromPiece(board[i][j]));
        }
      }
      fenBuilder.append('/');
    }
    fenBuilder.deleteCharAt(fenBuilder.length() - 1);
    return fenBuilder.toString();
  }

  /**
   * This method gets called when en empty square is reached on the board. What this does, is counts
   * the next empty squares and returns that number(This needs to be done, so that the number of
   * empty squares gets appended to the FEN String).
   * @param row The row index of the first empty square
   * @param column The column index of the first empty square
   * @param fenBuilder The StringBuilder, which the number has to be appended to
   * @return The number of adjacent empty squares
   */
  private int countEmptySquaresAndUpdateFen(int row, int column, StringBuilder fenBuilder) {
    int emptySquares = 0;
    while (column < 8 && board[row][column++] == null) {
      emptySquares++;
    }
    fenBuilder.append(emptySquares);
    return emptySquares;
  }

  /**
   * Get the appropriate FEN character according to the piece enum.
   * @param piece The piece to convert
   * @return The FEN character that represent the piece type
   * @throws IllegalArgumentException When the piece is of an unknown type
   */
  private char getCharFromPiece(Piece piece) throws IllegalArgumentException {
    switch (piece) {
      case RomanWall:
        return 'W';
      case RomanTower:
        return 'T';
      case RomanCatapult:
        return 'C';
      case GaulWall:
        return 'w';
      case GaulTower:
        return 't';
      case GaulCatapult:
        return 'c';
      default:
        throw new IllegalArgumentException("The character has to be a valid FEN character");
    }
  }

  /**
   * Set the appropriate piece enum type according to the FEN character.
   * @param fen_char The FEN character to be converted into a piece
   * @param row The row index of the piece
   * @param column The column index of the piece
   */
  private void setPieceFromChar(char fen_char, int row, int column) {
    switch (fen_char) {
      case 'W':
        board[row][column] = Piece.RomanWall;
        break;
      case 'T':
        board[row][column] = Piece.RomanTower;
        break;
      case 'C':
        board[row][column] = Piece.RomanCatapult;
        break;
      case 'w':
        board[row][column] = Piece.GaulWall;
        break;
      case 't':
        board[row][column] = Piece.GaulTower;
        break;
      case 'c':
        board[row][column] = Piece.GaulCatapult;
        break;
    }
  }

  private boolean isRomanPiece(int row, int col) {
    if(board[row][col] == Piece.RomanCatapult || board[row][col] == Piece.RomanTower
            || board[row][col] == Piece.RomanWall)
      return true;
  }

  private boolean isGaulPiece(int row, int col) {
    if(board[row][col] == Piece.GaulCatapult || board[row][col] == Piece.GaulTower
            || board[row][col] == Piece.GaulWall)
      return true;
  }
}
