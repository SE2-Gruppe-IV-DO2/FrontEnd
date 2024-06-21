package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

import at.aau.serg.websocketdemoapp.helper.CardType;
import lombok.Data;

@Data
public class PlayerTrickResponse {
    @JsonProperty("playerTricks")
    HashMap<String, Map<CardType, Integer>> playerTricks;
}
