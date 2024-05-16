package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.dto.CardPlayRequest;
import at.aau.serg.websocketdemoapp.dto.CardPlayedRequest;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.helper.Card;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.FlingListener;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class ActiveGameService implements FlingListener {
    private final DataHandler dataHandler;
    private final ActiveGame activeGame;
    private final StompHandler stompHandler;
    private static final String TAG = "DealRound";
    private final GameData gameData;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean isCurrentlyActivePlayer = false;

    public ActiveGameService(Context context, ActiveGame activeGame, GameData gameData) {
        dataHandler = DataHandler.getInstance(context);
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();
        this.gameData = gameData;

        subscribeForPlayerChangedEvent();
        subscribeForPlayCardEvent();
    }

    public ActiveGameService(ActiveGame activeGame, DataHandler dataHandler, GameData gameData) {
        this.dataHandler = dataHandler;
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();
        this.gameData = gameData;

        subscribeForPlayerChangedEvent();
        subscribeForPlayCardEvent();
    }

    @Override
    public void onCardFling(String cardName) {
        Card card = gameData.findCardByCardName(cardName);
        Log.d("CARD FOUND", card.getColor() + card.getValue());
        //playCard(card.getColor(), card.getValue());
        if (gameData.getCardList().remove(card)) {
            Log.d("REMOVE CARD", "CARD REMOVED SUCCESSFULLY");
        }
        gameData.getCardsPlayed().add(card);
        Log.d("FLINGED", card.toString());
        activeGame.refreshActiveGame();
        activeGame.displayCardsPlayed();
    }

    public void getData() {
        try {
            stompHandler.dealNewRound(dataHandler.getLobbyCode(), dataHandler.getPlayerID(), dataHandler::setGameData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        activeGame.refreshActiveGame();
    }

    private void subscribeForPlayerChangedEvent() {
        stompHandler.subscribeForPlayerChangedEvent(this::setActivePlayer);
    }

    public void setActivePlayer(String activePlayerId) {
        if (dataHandler.getPlayerID().equals(activePlayerId)) {
            isCurrentlyActivePlayer = true;
            activeGame.updateActivePlayerInformation(dataHandler.getPlayerName());
        }
        else {
            isCurrentlyActivePlayer = false;
            activeGame.updateActivePlayerInformation("OTHER_PLAYER_NAME");
        }
    }

    public boolean isCurrentlyActivePlayer() {
        return isCurrentlyActivePlayer;
    }

    private void subscribeForPlayCardEvent() {
        stompHandler.subscribeForPlayCard(response -> {
            Log.d("RECEIVED CARD", response);
            handlePlayCardResponse(response);
        });
    }

    private void handlePlayCardResponse(String playCardJSON) {
        activeGame.runOnUiThread(() -> {
            Log.d(TAG, "Handling playCard response");
            CardPlayedRequest cardPlayedRequest = null;
            try {
                cardPlayedRequest = objectMapper.readValue(playCardJSON, CardPlayedRequest.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            Log.d("CARD PLAYED", cardPlayedRequest.toString());
            Card c = new Card();
            c.setValue(cardPlayedRequest.getValue());
            c.setColor(cardPlayedRequest.getColor());
            c.setImgPath("card_" + cardPlayedRequest.getColor() + cardPlayedRequest.getValue());
            gameData.getCardsPlayed().add(c);
            Log.d(TAG, c.toString());
            activeGame.displayCardsPlayed();
        });
    }

    public void playCard(String color, int value) {
        CardPlayRequest playCardRequest = new CardPlayRequest();
        playCardRequest.setLobbyCode(dataHandler.getLobbyCode());
        playCardRequest.setUserID(dataHandler.getPlayerID());
        playCardRequest.setColor(color);
        Integer i = value;
        playCardRequest.setValue(i);

        String jsonPayload = new Gson().toJson(playCardRequest);
        stompHandler.playCard(jsonPayload);
    }
}
