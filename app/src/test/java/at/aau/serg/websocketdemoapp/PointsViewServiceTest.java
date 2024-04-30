package at.aau.serg.websocketdemoapp;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import at.aau.serg.websocketdemoapp.services.PointsViewService;

class PointsViewServiceTest {
    private PointsViewService pointsViewService;

    @BeforeEach
    void setUp() {
        pointsViewService = new PointsViewService(3);
    }

    @AfterEach
    void tearDown() {
        pointsViewService = null;
    }

    @Test
    void testSetPointsAndCalcSum() {
        pointsViewService.setPoints(1, 1, 10);
        pointsViewService.setPoints(1, 2, 15);
        pointsViewService.setPoints(2, 1, 5);

        int[][] pointsArray = pointsViewService.getPointsArray();
        int[] sumArray = pointsViewService.getSumArray();

        assertEquals(10, pointsArray[0][0]);
        assertEquals(15, pointsArray[0][1]);
        assertEquals(5, pointsArray[1][0]);

        assertEquals(15, sumArray[0]);
        assertEquals(15, sumArray[1]);
        assertEquals(0, sumArray[2]);
    }

    @Test
    void testZeroPoints() {
        int[][] pointsArray = pointsViewService.getPointsArray();
        int[] sumArray = pointsViewService.getSumArray();

        for (int[] roundPoints : pointsArray) {
            for (int points : roundPoints) {
                assertEquals(0, points);
            }
        }

        for (int sum : sumArray) {
            assertEquals(0, sum);
        }
    }

    @Test
    void testInvalidRoundPlayer() {
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> pointsViewService.setPoints(6, 2, 20));
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> pointsViewService.setPoints(3, 4, 20));
    }
}
