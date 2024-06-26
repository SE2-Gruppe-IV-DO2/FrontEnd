package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;

import at.aau.serg.websocketdemoapp.helper.Card;
import lombok.Data;

@Data
public class PlayerTrickResponse {
    @JsonProperty("playerTricks")
    HashMap<String, List<Card>> playerTricks;
}
