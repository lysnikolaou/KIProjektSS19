package murusgallicus.ai;

/**
 * The time management class. Here the allocated time gets calculated, according
 * to the moves played so far, and the time left for the player.
 *
 * Details:
 *
 * 1. For the first 5 moves, we allocate 1 second per move
 * 2. For all the subsequent moves, we allocate 3 seconds per move, but
 * 3. When timeLeft reaches 40 seconds, we go into panic mode, when the allocated time is computed dynamically,
 *    allowing for 30 more moves in these 40 seconds.
 */
class TimeManagement {

  /**
   * Panic mode, when the allocated time gets dynamically calculated
   */
  private static boolean panic = false;

  /**
   * When the panic modes gets enabled, we allow enough time for 30 more moves.
   */
  private static int movesLeft = 30;

  /**
   * The method that calculates the allocated time for a specific move.
   * @param numberOfMovesPlayed The number of moves played in the game
   * @param timeLeft The time the player has in its disposal altogether
   * @return The allocated time
   */
  static int calculateAllowedTime(int numberOfMovesPlayed, long timeLeft) {
    panic = timeLeft <= 40000 && panic;

    if (panic) return (int) timeLeft / movesLeft--;

    if (numberOfMovesPlayed < 5) return 1000;
    else return 3000;
  }
}
