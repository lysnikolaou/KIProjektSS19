package murusgallicus.ai;

import murusgallicus.core.Board;

public class MiniMax {

  /**
   * The number of nodes that are searched during the MiniMax algorithm.
   */
  static int nodes;
  /**
   * The depth of the generated search tree.
   */
  static int depth;
  /**
   * The best move is stored here, when the MiniMax algorithm runs.
   */
  private static String bestMove;

  /**
   * Gets the optimal move for the given position, according the MiniMax search.
   * @param board The current state of the board
   * @param numberOfMovesPlayed The number of moves that have been played in the game so far
   * @return The string representation of the optimal move
   */
  public static String getOptimalMove(Board board, int player, int numberOfMovesPlayed) {
    return minimax(board, player, (numberOfMovesPlayed < 20) ? 1000 : 2000);
  }

  /**
   * Run the minimax search in order to find the optimal move
   * @param board The current state of the board
   * @param allocatedTime The number of ms, when the method should be ready
   * @return The string representation of the optimal move
   */
  private static String minimax(Board board, int player, int allocatedTime) {
    depth = 0;
    nodes = 0;
    long timeElapsed = 0;
    while (true) {
      long before = System.currentTimeMillis();

      if (board.getPlayerToMove() == 'r') max(depth, board, Integer.MAX_VALUE, player);
      else min(depth, board, Integer.MIN_VALUE, player);

      long after = System.currentTimeMillis();

      timeElapsed += (after - before);
      if (timeElapsed + 4*(after - before) > allocatedTime) break;
      depth++;
    }
    return bestMove;
  }

  /**
   * The method for the max player
   */
  private static int max(int depth, Board board, int beta, int player) {
    if (depth == 0) return board.getRating();

    String[] moves = board.generateMoves();
    if (moves.length == 0) return board.getRating();

    int alpha = Integer.MIN_VALUE;
    int bestScore = Integer.MIN_VALUE;
    for (String move: moves) {
      nodes++;
      String boardNow = board.toString();
      board.executeMove(move);
      alpha = Math.max(alpha, min(depth-1, board, alpha, player));
      if (alpha > bestScore && player == 0) bestMove = move;
      board.setBoard(boardNow);

      if (alpha >= beta) break;
    }
    return alpha;
  }

  /**
   * The method for the min player
   */
  private static int min(int depth, Board board, int alpha, int player) {
    if (depth == 0) return board.getRating();

    String[] moves = board.generateMoves();
    if (moves.length == 0) return board.getRating();

    int beta = Integer.MAX_VALUE;
    int bestScore = Integer.MAX_VALUE;
    for (String move: moves) {
      nodes++;
      String boardNow = board.toString();
      board.executeMove(move);
      beta = Math.min(beta, max(depth - 1, board, beta, player));
      if (beta < bestScore && player == 1) bestMove = move;
      board.setBoard(boardNow);

      if (alpha >= beta) break;
    }
    return beta;
  }

}
