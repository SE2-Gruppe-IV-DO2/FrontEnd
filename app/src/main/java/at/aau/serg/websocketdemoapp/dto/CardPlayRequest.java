package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardPlayRequest {
    @JsonProperty("lobbyCode")
    private String lobbyCode;
    @JsonProperty("userID")
    private String playerID;
    @JsonProperty("color")
    private String color;
    @JsonProperty("value")
    private Integer value;
}
