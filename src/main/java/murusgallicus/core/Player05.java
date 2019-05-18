package murusgallicus.core;

import murusgallicus.ai.MiniMax;
import versus.interfaces.Player;

public class Player05 implements Player {

  private int numberOfMovesPlayer;

  @Override
  public boolean acceptGame(String game) {
    return game.equals("Murus Gallicus");
  }

  @Override
  public String getPlayerName() {
    return "Player05";
  }

  @Override
  public String requestMove(String representation, int player, long timeLeft, long additionalTime) {
    return MiniMax.getOptimalMove(new Board(representation), player, numberOfMovesPlayer++, timeLeft);
  }
}
