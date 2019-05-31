package murusgallicus.ai;

import murusgallicus.core.Board;

import java.util.Arrays;

/**
 * The class the implement the search for the best move, using variants of the Minimax algorithm
 * with alpha-beta cutoffs.
 */
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
  public static int maxDepth = -1;

  /**
   * The best move is stored here, when the MiniMax algorithm runs.
   */
  private static String[] bestMoveList = new String[40];

  /**
   * Flag to indicate if cutOffs are in order.
   */
  static boolean cutOffs = true;

  /**
   * Gets the optimal move for the given position, according the MiniMax search.
   * @param board The current state of the board
   * @param player The player whose turn it is to play
   * @param numberOfMovesPlayed The number of moves that have been played in the game so far
   * @param timeLeft The time left for the player in milliseconds
   * @return The string representation of the optimal move
   */
  public static String getOptimalMove(Board board, int player, long timeLeft, int numberOfMovesPlayed) {
    return minimax(board, player, TimeManagement.computeTimeAllocatedForMove(timeLeft, numberOfMovesPlayed));
  }

  /**
   * Run the minimax search in order to find the optimal move.
   * @param board The current state of the board
   * @param player The player whose turn it is to move
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

    String bestMove = bestMoveList[0];
    bestMoveList = new String[40];
    return bestMove;
  }

  /**
   * Run the minimax algorithm until a certain depth is reached. This method mostly serves testing
   * and performance evaluating purposes.
   * @param board The current board
   * @param player The player whose turn it is to play
   */
  private static void minimaxWithDepthConstraint(Board board, int player) {
    long before = System.currentTimeMillis();
    for (int depth = 0; depth <= maxDepth; depth++) {
      pvSearch(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, board, player);
    }
    long after = System.currentTimeMillis();
    System.out.println("FEN: " + board.toString());
    System.out.println("Nodes: " + nodes);
    System.out.println("Time elapsed: " + (after - before));
  }

  /**
   * Run the minimax algorithm for a predefined period of time, no matter what depth is reached in
   * that interval. This is needed for real tournament play.
   * @param board The current board
   * @param player The player whose turn it is to move
   * @param allocatedTime The allocated time for the move
   */
  private static void minimaxWithTimeConstraint(Board board, int player, long allocatedTime) {
    long timeElapsed = 0;
    while (true) {
      maxDepth = depth;
      long before = System.currentTimeMillis();
      pvSearch(Integer.MIN_VALUE, Integer.MAX_VALUE, depth, board, player);
      long after = System.currentTimeMillis();

      timeElapsed += (after - before);
      if (timeElapsed + 4*(after - before) > allocatedTime) break;
      depth++;
    }
    maxDepth = -1;
  }

  private static int pvSearch(int alpha, int beta, int depth, Board board, int player) {
    if (depth == 0) return board.getRating();

    String[] moves = board.generateMoves();
    sortPV(moves);
    if (moves.length == 0) return board.getRating();

    nodes++;
    String currentBoard = board.toString();
    board.executeMove(moves[0]);
    int bestScore = -pvSearch(-beta, -alpha, depth - 1, board, player);
    board.setBoard(currentBoard);
    if (bestScore > alpha) {
      if (bestScore >= beta) {
        return bestScore;
      }
      alpha = bestScore;
    }
    bestMoveList[maxDepth-depth] = moves[0];

    for (int i = 1; i < moves.length; i++) {
      nodes++;
      currentBoard = board.toString();
      board.executeMove(moves[i]);

      int score = -pvSearch(-alpha-1, -alpha, depth - 1, board, player);
      if (score > alpha && score < beta) {
        score = -pvSearch(-beta, -alpha, depth - 1, board, player);
        if (score > alpha) {
          bestMoveList[maxDepth-depth] = moves[i];
          alpha = score;
        }
      }

      board.setBoard(currentBoard);

      if (score > bestScore) {
        if (score >= beta) {
          return score;
        }
        bestScore = score;
      }
    }

    return bestScore;
  }

  private static void sortPV(String[] moves) {
    if (bestMoveList.length == 0) return;

    for (int i = 0; i < moves.length; i++) {
      if (Arrays.asList(bestMoveList).contains(moves[i])) {
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
}
