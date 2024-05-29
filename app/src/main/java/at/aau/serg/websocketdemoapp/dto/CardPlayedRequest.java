package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import at.aau.serg.websocketdemoapp.helper.CardType;
import lombok.Data;

@Data
public class CardPlayedRequest {
    @JsonProperty("cardType")
    private CardType cardType;
    @JsonProperty("color")
    private String color;
    @JsonProperty("value")
    private String value;
}
