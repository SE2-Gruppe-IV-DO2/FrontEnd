package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import at.aau.serg.websocketdemoapp.helper.CardType;
import lombok.Data;

@Data
public class CardPlayRequest {
    @JsonProperty("lobbyCode")
    private String lobbyCode;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("cardTye")
    private CardType cardType;
    @JsonProperty("color")
    private String color;
    @JsonProperty("value")
    private Integer value;
}
