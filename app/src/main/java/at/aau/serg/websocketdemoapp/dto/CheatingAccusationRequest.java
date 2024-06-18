package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CheatingAccusationRequest {
    @JsonProperty("userID")
    private String userID;

    @JsonProperty("lobbyCode")
    String lobbyCode;

    @JsonProperty("accusedUserId")
    private String accusedUserId;

    @JsonProperty("correctAccusation")
    private boolean correctAccusation;
}
