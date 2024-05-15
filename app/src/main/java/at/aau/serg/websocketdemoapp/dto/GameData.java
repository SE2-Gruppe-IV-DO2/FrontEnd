package at.aau.serg.websocketdemoapp.dto;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
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
        try {
            JavaType cardListType = objectMapper.getTypeFactory().constructCollectionType(List.class, Card.class);
            this.cardList = objectMapper.readValue(jsonString, cardListType);
        } catch (JsonProcessingException e) {
            Log.e("Parse Error", String.valueOf(e));
        }
    }
}


