package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PointsRequest {
    @JsonProperty("lobbyCode")
    String lobbyCode;
}
