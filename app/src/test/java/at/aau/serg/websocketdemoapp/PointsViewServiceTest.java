package at.aau.serg.websocketdemoapp;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import at.aau.serg.websocketdemoapp.services.PointsViewService;

class PointsViewServiceTest {
    private PointsViewService pointsViewService;

    @BeforeEach
    void setUp() {
        pointsViewService = new PointsViewService();
    }

    @AfterEach
    void tearDown() {
        pointsViewService = null;
    }

    @Test
    void testGetSumArrayNull() {
        assertNull(pointsViewService.getSumArray());
    }

    @Test
    void testCalcSum() {
        Map<String, HashMap<Integer, Integer>> testMap = pointsViewService.getPlayerPoints();
        testMap.put("Test 1", new HashMap<>());
        testMap.put("Test 2", new HashMap<>());
        testMap.put("Test 3", new HashMap<>());

        Objects.requireNonNull(testMap.get("Test 1")).put(1, 35);
        Objects.requireNonNull(testMap.get("Test 2")).put(1, -3);
        Objects.requireNonNull(testMap.get("Test 3")).put(1, 18);
        pointsViewService.calcSum();
        int[] result = {-3, 18, 35};

        Assertions.assertArrayEquals(result, pointsViewService.getSumArray());
    }

    @Test
    void testGetPlayerPoints() {
        Assertions.assertEquals(0, pointsViewService.getPlayerPoints().size());
    }
}
