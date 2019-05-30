package murusgallicus.core;

import murusgallicus.core.Board.Square;

/**
 * A class to represent the moves
 */
public class Move {

  /**
   * The source square of the moving piece
   */
  private Square sourceSquare;

  /**
   * The destination square of the moving piece
   */
  private Square destinationSquare;

  /**
   * The number of pieces moved
   */
  private int numberOfPiecesMoved;

  /**
   * A constructor that sets all of the instance variables to the passed values.s
   */
  public Move(Square sourceSquare, Square destinationSquare, int numberOfPiecesMoved) {
    this.sourceSquare = sourceSquare;
    this.destinationSquare = destinationSquare;
    this.numberOfPiecesMoved = numberOfPiecesMoved;
  }

  /**
   * Getter for the source square.
   * @return The source square
   */
  Square getSourceSquare() {
    return sourceSquare;
  }

  /**
   * Getter for the destination square.
   * @return The destination square
   */
  Square getDestinationSquare() {
    return destinationSquare;
  }

  /**
   * Getter for the number of moved pieces.
   * @return The number of moved pieces
   */
  int getNumberOfPiecesMoved() {
    return numberOfPiecesMoved;
  }

  /**
   * The move to string converter that adheres to all the rules defined on GitLab.
   * @return The string representation of the move
   */
  @Override
  public String toString() {
    if (numberOfPiecesMoved == -1) return this.sourceSquare + "-" + this.destinationSquare;
    else return this.sourceSquare + "-" + this.destinationSquare + "-" + this.numberOfPiecesMoved;
  }
}
