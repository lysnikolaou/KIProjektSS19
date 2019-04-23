package murusgallicus.core;

public class Move {

  /**
   * The source square of the moving piece
   */
  private String sourceSquare;

  /**
   * The destination square of the moving piece
   */
  private String destinationSquare;

  /**
   * The number of pieces moved
   */
  private int numberOfPiecesMoved;

  /**
   * A constructor that sets all of the instance variables to the passed values.s
   */
  public Move(String sourceSquare, String destinationSquare, int numberOfPiecesMoved) {
    this.sourceSquare = sourceSquare;
    this.destinationSquare = destinationSquare;
    this.numberOfPiecesMoved = numberOfPiecesMoved;
  }

  public String getSourceSquare() {
    return sourceSquare;
  }

  public String getDestinationSquare() {
    return destinationSquare;
  }

  public int getNumberOfPiecesMoved() {
    return numberOfPiecesMoved;
  }
}
