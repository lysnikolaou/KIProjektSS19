package murusgallicus.core;

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

  Piece[][] board;

  /**
   * murusgallicus.base.Board constructor that converts a FEN string to its board representation.
   * @param fen The FEN representation of the board.
   */
  Board(String fen) {
    board = new Piece[7][8];
    setBoard(fen);
  }

  void setBoard(String fen) {
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

  private int countEmptySquaresAndUpdateFen(int row, int column, StringBuilder fenBuilder) {
    int emptySquares = 0;
    while (column < 8 && board[row][column++] == null) {
      emptySquares++;
    }
    fenBuilder.append(emptySquares);
    return emptySquares;
  }

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
}
