package at.aau.serg.websocketdemoapp.dto;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

import at.aau.serg.websocketdemoapp.helper.Card;
import lombok.Data;

@Data
public class GameData {
    private ObjectMapper objectMapper;
    private List<Card> cardList;
    private List<Card> cardsPlayed;

    public GameData() {
        objectMapper = new ObjectMapper();
        cardsPlayed = new ArrayList<>();
    }

    public void parseJsonString(String jsonString) {
        HandCardsRequest handCardsRequest;
        try {
            handCardsRequest = objectMapper.readValue(jsonString, HandCardsRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if (handCardsRequest != null) {
            cardList = handCardsRequest.getHandCards();
        } else {
            Log.d("JSON PARSE", "Passed String was empty");
        }
    }
}


