package at.aau.serg.websocketdemoapp;

import android.content.Context;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import at.aau.serg.websocketdemoapp.activities.PointsView;
import at.aau.serg.websocketdemoapp.services.PointsViewService;

class PointsViewServiceTest {
    private PointsViewService pointsViewService;

    @Mock
    Context context;
    @Mock
    PointsView pointsView;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pointsViewService = new PointsViewService(pointsView, context);
    }

    @AfterEach
    void tearDown() {
        pointsViewService = null;
    }

    @Test
    void testGetPlayerPoints() {
        Assertions.assertEquals(0, pointsViewService.getPlayerPoints().size());
    }
}
