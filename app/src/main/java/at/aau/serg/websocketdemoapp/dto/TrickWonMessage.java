package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TrickWonMessage {
    @JsonProperty("winningPlayerId")
    private String winningPlayerId;

    @JsonProperty("winningPlayerName")
    private String winningPlayerName;
}