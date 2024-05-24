package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import at.aau.serg.websocketdemoapp.helper.Card;
import lombok.Data;

@Data
public class GameData {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Card> cardList = new ArrayList<>();
    private List<Card> cardsPlayed = new ArrayList<>();

    public Card findCardByCardName(String cardName) {
        Card card = null;
        for (Card c : cardList) {
            if(c.toString().equals(cardName)) {
                card = c;
            }
        }
        return card;
    }
}
