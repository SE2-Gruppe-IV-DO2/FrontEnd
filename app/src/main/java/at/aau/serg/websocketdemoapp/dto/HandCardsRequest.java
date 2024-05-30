package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

import at.aau.serg.websocketdemoapp.helper.Card;
import lombok.Data;

@Data
public class HandCardsRequest {
    @JsonProperty("playerID")
    String playerID;
    @JsonProperty("handCards")
    List<Card> handCards;
}