package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import at.aau.serg.websocketdemoapp.helper.CardType;
import lombok.Data;

@Data
public class CardPlayedRequest {
    @JsonProperty("cardType")
    CardType cardType;
    @JsonProperty("color")
    String color;
    @JsonProperty("value")
    Integer value;
}
