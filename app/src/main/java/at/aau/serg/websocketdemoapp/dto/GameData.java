package at.aau.serg.websocketdemoapp.dto;

import com.google.gson.Gson;

import java.util.List;

import lombok.Data;

@Data
public class GameData {
    private List<Card> cardList;
    private List<Card> cardsPlayed;

    // todo add all fields

    public static class Card {
        String imgPath;
        int value;
        String color;
    }

    public void parseJsonString(String jsonString) {
        Gson gson = new Gson();
        GameData gameData = gson.fromJson(jsonString, GameData.class);
        this.cardList = gameData.getCardList();
        this.cardsPlayed = gameData.getCardsPlayed();
    }
}


