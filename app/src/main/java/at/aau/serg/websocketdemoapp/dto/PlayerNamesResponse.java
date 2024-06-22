package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

@Data
public class PlayerNamesResponse {
    @JsonProperty("playerNames")
    List<String> playerNames;
}
