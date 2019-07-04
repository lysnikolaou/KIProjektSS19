package murusgallicus.ai;
import murusgallicus.core.Board;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;


/**
 * The class the implement the search for the best move, using variants of the Minimax algorithm
 * with alpha-beta cutoffs.
 */
public class MiniMax {
  private static class Pair<X, Y> {
    public Pair(X first, Y second) {
      this.first = first;
      this.second = second;
    }
    public X first;
    public Y second;
  }

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
      return minimaxWithTimeConstraint(board, player, allocatedTime);
    } else {
      return minimaxWithDepthConstraint(board, player);
    }
  }

  /**
   * Run the minimax algorithm until a certain depth is reached. This method mostly serves testing
   * and performance evaluating purposes.
   * @param board The current board
   * @param player The player whose turn it is to play
   */
  private static String minimaxWithDepthConstraint(Board board, int player) {
    String bestMove = null;
    long before = System.currentTimeMillis();
    for (int depth = 0; depth <= maxDepth; depth++) {
      bestMove = alphaBeta(board, board, board, depth, player).second;
    }
    long after = System.currentTimeMillis();
    System.out.println("FEN: " + board.toString());
    System.out.println("Nodes: " + nodes);
    System.out.println("Time elapsed: " + (after - before));
    return bestMove;
  }

  /**
   * Run the minimax algorithm for a predefined period of time, no matter what depth is reached in
   * that interval. This is needed for real tournament play.
   * @param board The current board
   * @param player The player whose turn it is to move
   * @param allocatedTime The allocated time for the move
   */
  private static String minimaxWithTimeConstraint(Board board, int player, long allocatedTime) {
    String bestMove;
    long timeElapsed = 0;
    while (true) {
      maxDepth = depth;
      long before = System.currentTimeMillis();
      bestMove = alphaBeta(board, board, board, depth, player).second;
      long after = System.currentTimeMillis();

      timeElapsed += (after - before);
      if (timeElapsed + 4*(after - before) > allocatedTime) break;
      depth++;
    }
    maxDepth = -1;
    return bestMove;
  }

  private static Pair<Integer, Integer> predict(int[] first, int[] second) {
    String firstString = Arrays.toString(first);
    String secondString = Arrays.toString(second);
    String result = null;
    try {
      Process p = Runtime.getRuntime().exec("python3 ../../../../../scripts.model.py predict " +
              firstString + " " + secondString);
      BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
      result = input.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (result.startsWith("[1")) return new Pair<>(1, 0);
    else return new Pair<>(0, 1);
  }

  private static Pair<Pair<Integer, Integer>, String> alphaBeta(Board board, Board alphaPos, Board betaPos, int depth, int player) {
    if (depth == 0) {
      return (player == 0) ? new Pair<>(predict(board.bitify(), alphaPos.bitify()), null) :
              new Pair<>(predict(board.bitify(), betaPos.bitify()), null);
    }

    if (player == 0) {
      String bestMove = null;
      String[] moves = board.generateMoves();
      for (String move: moves) {
        board.executeMove(move);
        Pair<Pair<Integer, Integer>, String> rv = alphaBeta(board, alphaPos, betaPos, depth-1, 1);
        if (rv.first.first == 1) {
          bestMove = move;
          alphaPos = board;
        }
        if (predict(betaPos.bitify(), board.bitify()).second == 1) break;
      }
      return new Pair<>(null, bestMove);
    } else {
      String bestMove = null;
      String[] moves = board.generateMoves();
      for (String move: moves) {
        board.executeMove(move);
        Pair<Pair<Integer, Integer>, String> rv = alphaBeta(board, alphaPos, betaPos, depth-1, 0);
        if (rv.first.first == 1) {
          bestMove = move;
          betaPos = board;
        }
        if (predict(betaPos.bitify(), board.bitify()).first == 1) break;
      }
      return new Pair<>(null, bestMove);
    }
  }
}
