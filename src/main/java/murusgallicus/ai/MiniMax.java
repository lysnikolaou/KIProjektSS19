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
   * The maximal depth for the current call of the minimax function.
   */
  static int maxDepth = -1;
  /**
   * The best move is stored here, when the MiniMax algorithm runs.
   */
  private static String bestMove;
  /**
   * Flag to indicate if cutOffs are in order.
   */
  static boolean cutOffs = true;

  /**
   * Gets the optimal move for the given position, according the MiniMax search.
   * @param board The current state of the board
   * @param numberOfMovesPlayed The number of moves that have been played in the game so far
   * @return The string representation of the optimal move
   */
  public static String getOptimalMove(Board board, int player, long timeLeft, int numberOfMovesPlayed) {
    return minimax(board, player, TimeManagement.computeTimeAllocatedForMove(timeLeft, numberOfMovesPlayed));
  }

  /**
   * Run the minimax search in order to find the optimal move
   * @param board The current state of the board
   * @param allocatedTime The number of ms, when the method should be ready
   * @return The string representation of the optimal move
   */
  private static String minimax(Board board, int player, long allocatedTime) {
    depth = 0;
    nodes = 0;
    if (maxDepth == -1) {
      minimaxWithTimeConstraint(board, player, allocatedTime);
    } else {
      minimaxWithDepthConstraint(board, player);
    }

    return bestMove;
  }

  private static void minimaxWithDepthConstraint(Board board, int player) {
    long before = System.currentTimeMillis();
    pvSearch(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, board, player);
    long after = System.currentTimeMillis();
    System.out.println("FEN: " + board.toString());
    System.out.println("Nodes: " + nodes);
    System.out.println("Time elapsed: " + (after - before));
  }

  private static void minimaxWithTimeConstraint(Board board, int player, long allocatedTime) {
    depth = 0;
    long timeElapsed = 0;
    while (true) {
      long before = System.currentTimeMillis();
      pvSearch(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, board, player);
      long after = System.currentTimeMillis();

      timeElapsed += (after - before);
      if (timeElapsed + 4*(after - before) > allocatedTime) break;
      depth++;
    }
  }

  private static int pvSearch(int alpha, int beta, int depth, Board board, int player) {
    if (depth == 0) return board.getRating();
    boolean searchPV = true;

    String[] moves = board.generateMoves();
    sortPV(moves);
    if (moves.length == 0) return board.getRating();

    for (String move : moves) {
      String currentBoard = board.toString();
      board.executeMove(move);

      int score;
      if (searchPV) {
        score = -pvSearch(-beta, -alpha, depth - 1, board, player);
      } else {
        score = -zwSearch(-alpha, depth - 1, board);
        if (score > alpha) score = -pvSearch(-beta, -alpha, depth - 1, board, player);
      }

      board.setBoard(currentBoard);

      if (score >= beta) return beta;
      if (score > alpha) {
        alpha = score;
        searchPV = false;
        if (player == 0 && board.getPlayerToMove() == 'r' || player == 1 && board.getPlayerToMove() == 'g') {
          bestMove = move;
        }
      }
    }

    return alpha;
  }

  private static void sortPV(String[] moves) {
    if (bestMove == null) return;

    for (int i = 0; i < moves.length; i++) {
      if (moves[i].equals(bestMove)) {
        bringMoveForward(moves, i);
        return;
      }
    }

  }

  private static void bringMoveForward(String[] moves, int i) {
    String tmp = moves[0];
    moves[0] = moves[i];
    moves[i] = tmp;
  }

  private static int zwSearch(int beta, int depth, Board board) {
    if (depth == 0) return board.getRating();

    String[] moves = board.generateMoves();
    if (moves.length == 0) return board.getRating();

    for (String move: moves) {
      String currentBoard = board.toString();
      board.executeMove(move);

      int score = -zwSearch(1-beta, depth-1, board);

      board.setBoard(currentBoard);

      if (score >= beta) return beta;
    }

    return beta-1;
  }

}
