package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetPlayersInLobbyRequest {
    @JsonProperty("lobbyCode")
    private String lobbyCode;
}
