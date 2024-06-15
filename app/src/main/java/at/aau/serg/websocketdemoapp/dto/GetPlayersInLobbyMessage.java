package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class GetPlayersInLobbyMessage {
    @JsonProperty("lobbyCode")
    private String lobbyCode;

    @JsonProperty("playerNames")
    private List<String> playerNames;
}
