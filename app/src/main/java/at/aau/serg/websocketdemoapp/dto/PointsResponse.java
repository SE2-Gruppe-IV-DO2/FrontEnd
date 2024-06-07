package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class PointsResponse {
    @JsonProperty("pointsMap")
    Map<String, HashMap<Integer, Integer>> playerPoints;

    public PointsResponse() {
        this.playerPoints = new HashMap<>();
    }
}
