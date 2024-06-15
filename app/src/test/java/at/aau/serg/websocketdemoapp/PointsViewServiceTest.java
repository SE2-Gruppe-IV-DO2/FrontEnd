package at.aau.serg.websocketdemoapp;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.content.SharedPreferences;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import at.aau.serg.websocketdemoapp.activities.PointsView;
import at.aau.serg.websocketdemoapp.dto.PointsResponse;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import at.aau.serg.websocketdemoapp.services.PointsViewService;

class PointsViewServiceTest {
    private PointsViewService pointsViewService;

    @Mock
    Context context;
    @Mock
    PointsView pointsView;
    @Mock
    SharedPreferences sharedPreferences;
    @Mock
    DataHandler dataHandler;
    @Mock
    StompHandler stompHandler;
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences);

        DataHandler.setInstance(dataHandler);
        StompHandler.setInstance(stompHandler);
        pointsViewService = new PointsViewService(pointsView, context);
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
        pointsViewService = null;
        objectMapper = null;
    }

    @Test
    void testGetPlayerPoints() {
        Assertions.assertEquals(0, pointsViewService.getPlayerPoints().size());
    }

    @Test
    void testFetchPointsData() throws JsonProcessingException {
        pointsViewService.fetchPointsBoard();

        verify(stompHandler, times(2)).getPoints(any(), any());


        PointsResponse pointsResponse = new PointsResponse();
        pointsResponse.setPlayerPoints(new HashMap<>());
        String sampleJson = objectMapper.writeValueAsString(pointsResponse);

        pointsViewService.processPointData(sampleJson);

        Assertions.assertEquals(0, pointsViewService.getPlayerPoints().size());
    }
}
