package at.aau.serg.websocketdemoapp.dto;

import lombok.Data;

@Data
public class CardPlayRequest {
    private String lobbyCode;
    private String playerID;
    private String color;
    private Integer value;
}
