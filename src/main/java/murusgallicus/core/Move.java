package murusgallicus.core;

class Move {

  /**
   * The source square of the moving piece
   */
  String sourceSquare;

  /**
   * The destination square of the moving piece
   */
  String destinationSquare;

  /**
   * The number of pieces moved
   */
  int numberOfPiecesMoved;

  /**
   * A constructor that sets all of the instance variables to the passed values.s
   */
  Move(String sourceSquare, String destinationSquare, int numberOfPiecesMoved) {
    this.sourceSquare = sourceSquare;
    this.destinationSquare = destinationSquare;
    this.numberOfPiecesMoved = numberOfPiecesMoved;
  }
}
