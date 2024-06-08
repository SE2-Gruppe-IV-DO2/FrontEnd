package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CardPlayRequest {
    @JsonProperty("lobbyCode")
    private String lobbyCode;
    @JsonProperty("userID")
    private String userID;
    @JsonProperty("name")
    private String name;
    @JsonProperty("color")
    private String color;
    @JsonProperty("value")
    private String value;
}
