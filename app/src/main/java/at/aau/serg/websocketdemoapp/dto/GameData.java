package at.aau.serg.websocketdemoapp.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class GameData {
    private List<Card> cardList;
    private List<Card> cardsPlayed;

    // todo add all fields

    public static class Card {
        String imgPath;
        int value;
        // ...
    }
}


