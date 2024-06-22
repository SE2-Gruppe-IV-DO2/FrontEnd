package at.aau.serg.websocketdemoapp.dto;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import at.aau.serg.websocketdemoapp.helper.Card;
import at.aau.serg.websocketdemoapp.helper.CardType;
import lombok.Data;

@Data
public class GameData {
    private final ObjectMapper objectMapper;
    private List<Card> cardList;
    private List<Card> cardsPlayed;
    private List<String> playerNames;
    private HashMap<String, Map<CardType, Integer>> playerTricks;
    private static GameData instance;

    private GameData() {
        objectMapper = new ObjectMapper();
        cardList = new ArrayList<>();
        cardsPlayed = new ArrayList<>();
    }

    public static void setInstance(GameData gameData) {
        GameData.instance = gameData;
    }

    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }
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
