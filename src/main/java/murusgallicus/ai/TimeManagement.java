package murusgallicus.ai;

class TimeManagement {

    private static final int firstEasyMoves = 5;

    private static final int midGameMoves = 10;

    private static boolean panicMode = false;
    private static int movesSincePanic = 1;

    static long computeTimeAllocatedForMove(long timeLeft, int numberOfMovesPlayed) {
        if (timeLeft < 30000L) {
            panicMode = true;
        }

        if (panicMode) {
            return timeLeft / (20 - movesSincePanic++);
        }

        int outOfOpeningMoves = Math.max(0, numberOfMovesPlayed - firstEasyMoves);
        int nMoves = Math.min(outOfOpeningMoves, midGameMoves);
        double factor = 2 - nMoves / 10;
        double target = timeLeft / 45;
        return (long) (factor * target);
    }
}
