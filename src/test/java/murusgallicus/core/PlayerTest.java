package murusgallicus.core;

import murusgallicus.ai.MiniMax;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {

    @Test
    void testRequestMove() {
        MiniMax.maxDepth = 4;
        Player05 player = new Player05();
        String repr = "5ww1/2w1wwW1/w1w1wWT1/2wt3W/1wWwwT2/2Tw1WT1/T7 g";
        Board board = new Board(repr);
        String[] moves = board.generateMoves();
        List<String> moveList = Arrays.asList(moves);
        String optimalMove = player.requestMove(repr, 1, 120000L, 0L);
        System.out.println(optimalMove);
        MiniMax.maxDepth = -1;
        assertTrue(moveList.contains(optimalMove));
    }
}
