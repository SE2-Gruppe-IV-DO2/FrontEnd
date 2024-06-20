package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class GetPlayersInLobbyMessage {
    @JsonProperty("lobbyCode")
    private String lobbyCode;

    @JsonProperty("playerNames")
    private List<String> playerNames;

    @JsonProperty("playerNamesAndIds")
    private Map<String, String> playerNamesAndIds;
}
