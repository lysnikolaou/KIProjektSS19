package murusgallicus.core;

import murusgallicus.ai.MiniMax;
import versus.interfaces.Player;

/**
 * The player class
 */
public class Player05 implements Player {

  /**
   * The number of played moves until this point in the game
   */
  private int numberOfMovesPlayed;

  /**
   * Make sure that the player can play the game.
   * @param game The game that the player needs to be able to play
   * @return true, if the player can play the game, false otherwise
   */
  @Override
  public boolean acceptGame(String game) {
    return game.equals("Murus Gallicus");
  }

  /**
   * Getter for the player name.
   * @return The player name
   */
  @Override
  public String getPlayerName() {
    return "Player05";
  }

  /**
   * The method that returns the move that the player wants to play. This gets called by the Game
   * class of the Versus system.
   * @param representation The FEN representation of the board
   * @param player The player to move, 0 for romans, 1 for gauls
   * @param timeLeft The time left in milliseconds
   * @param additionalTime The additional time per move in milliseconds
   * @return A string representation of the move played
   */
  @Override
  public String requestMove(String representation, int player, long timeLeft, long additionalTime) {
    return MiniMax.getOptimalMove(new Board(representation), player, numberOfMovesPlayed++, timeLeft);
  }
}
