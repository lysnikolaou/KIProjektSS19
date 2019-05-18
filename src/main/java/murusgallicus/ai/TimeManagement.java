package murusgallicus.ai;

class TimeManagement {

  private static boolean panic = false;

  private static int movesLeft = 30;

  static int calculateAllowedTime(int numberOfMovesPlayed, long timeLeft) {
    panic = timeLeft <= 40000 && panic;

    if (panic) return (int) timeLeft / movesLeft--;

    if (numberOfMovesPlayed < 5) return 1000;
    else return 3000;
  }
}
