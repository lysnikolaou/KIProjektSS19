package murusgallicus.ai;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TimeManagementTest {

    @Test
    void computeTimeAllocatedForMove() {
        for (int i = 0; i < 70; i++) {
            long timeLeft = 120000L - i*1000;
            long allocatedTime = TimeManagement.computeTimeAllocatedForMove(timeLeft, i);
            System.out.printf("Time Left: %d\nMoves played: %d\nAllocated Time: %d\n\n", timeLeft, i, allocatedTime);
            assertTrue(allocatedTime < 10000);
        }
    }
}
