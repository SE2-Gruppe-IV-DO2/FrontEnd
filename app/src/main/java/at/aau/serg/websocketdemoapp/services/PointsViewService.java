package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import at.aau.serg.websocketdemoapp.activities.PointsView;
import at.aau.serg.websocketdemoapp.dto.PointsResponse;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import lombok.Getter;

public class PointsViewService {
    @Getter
    private Map<String, HashMap<Integer, Integer>> playerPoints;
    private final StompHandler stompHandler = StompHandler.getInstance();
    private final DataHandler dataHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PointsView pointsView;

    public PointsViewService(PointsView pointsView, Context context) {
        this.pointsView = pointsView;
        playerPoints = new HashMap<>();
        dataHandler = DataHandler.getInstance(context);
        fetchPointsBoard();
    }

    public void fetchPointsBoard() {
        new Thread(() -> stompHandler.getPoints(dataHandler.getLobbyCode(), this::processPointData)).start();
    }

    public void processPointData(String data) {
        pointsView.runOnUiThread(() -> {
            Log.d("Point Response", data);
            PointsResponse pointsResponse;
            try {
                pointsResponse = objectMapper.readValue(data, PointsResponse.class);
            } catch (JsonProcessingException e) {
                throw new JsonParsingException("JSON Parse Exception", e);
            }
            playerPoints = pointsResponse.getPlayerPoints();
            pointsView.updateUI();
        });
    }
}
