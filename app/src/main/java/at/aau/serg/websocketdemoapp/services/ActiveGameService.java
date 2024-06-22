package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.dto.ActivePlayerMessage;
import at.aau.serg.websocketdemoapp.dto.CardPlayRequest;
import at.aau.serg.websocketdemoapp.dto.CardPlayedRequest;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.dto.TrickWonMessage;
import at.aau.serg.websocketdemoapp.helper.Card;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.FlingListener;
import at.aau.serg.websocketdemoapp.helper.JsonParsingException;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import lombok.Setter;

public class ActiveGameService implements FlingListener {
    private final DataHandler dataHandler;
    private ActiveGame activeGame;
    private final StompHandler stompHandler;
    private static final String TAG = "DealRound";
    private final GameData gameData;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Setter
    private boolean isCurrentlyActivePlayer = false;
    @Setter
    private boolean preventCardFling = false;
    private static ActiveGameService instance = null;

    private ActiveGameService(Context context, ActiveGame activeGame) {
        dataHandler = DataHandler.getInstance(context);
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();
        this.gameData = GameData.getInstance();

        subscribeForPlayerChangedEvent();
        subscribeForPlayCardEvent();
        subscribeForRoundEndEvent();
        subscribeForPlayerWonTrickEvent();
    }

    public static ActiveGameService getInstance(Context context, ActiveGame activeGame) {
        if (instance == null) {
            instance = new ActiveGameService(context, activeGame);
        }
        return instance;
    }

    public void updateActiveGame(ActiveGame activeGame) {
        this.activeGame = activeGame;
    }

    @Override
    public void onCardFling(String cardName) {
        if (!isCurrentlyActivePlayer() || preventCardFling) return;
        Card card = gameData.findCardByCardName(cardName);
        playCard(card.getName(), card.getColor(), card.getValue());
        if (gameData.getCardList().remove(card)) {
            Log.d("REMOVE CARD", "CARD REMOVED SUCCESSFULLY");
        }
        Log.d("FLING", card.toString());
        activeGame.runOnUiThread(activeGame::refreshActiveGame);
    }

    private void subscribeForPlayerChangedEvent() {
        stompHandler.subscribeForPlayerChangedEvent(dataHandler.getLobbyCode(), this::setActivePlayer);
    }

    public void setActivePlayer(String data) {
        ActivePlayerMessage activePlayerMessage;
        try {
            activePlayerMessage = objectMapper.readValue(data, ActivePlayerMessage.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("JSON PARSE ERROR", e);
        }
        if (dataHandler.getPlayerID().equals(activePlayerMessage.getActivePlayerId())) {
            isCurrentlyActivePlayer = true;
            activeGame.updateActivePlayerInformation(dataHandler.getPlayerName());
        } else {
            isCurrentlyActivePlayer = false;
            activeGame.updateActivePlayerInformation(activePlayerMessage.getActivePlayerName());
        }
    }

    public boolean isCurrentlyActivePlayer() {
        return isCurrentlyActivePlayer;
    }

    private void subscribeForPlayCardEvent() {
        stompHandler.subscribeForPlayCard(dataHandler.getLobbyCode(), this::handlePlayCardResponse);
    }

    private void subscribeForPlayerWonTrickEvent() {
        stompHandler.subscribeForPlayerWonTrickEvent(dataHandler.getLobbyCode(), response -> {
            handleTrickWon(response);
            gameData.getCardsPlayed().clear();
        });
    }

    public void handlePlayCardResponse(String playCardJSON) {
        CardPlayedRequest cardPlayedRequest;
            try {
                cardPlayedRequest = objectMapper.readValue(playCardJSON, CardPlayedRequest.class);
            } catch (JsonProcessingException e) {
                throw new JsonParsingException("JSON PARSE ERROR", e);
            }
            Card c = new Card();
            c.setCardType(cardPlayedRequest.getCardType());
            c.setValue(Integer.valueOf(cardPlayedRequest.getValue()));
            c.setColor(cardPlayedRequest.getColor());
            c.createImagePath();
            gameData.getCardsPlayed().add(c);
            activeGame.runOnUiThread(() -> activeGame.displayCardsPlayed());
    }

    public void handleTrickWon(String trickWonJson) {
        TrickWonMessage trickWonMessage;
        try {
            trickWonMessage = objectMapper.readValue(trickWonJson, TrickWonMessage.class);
        } catch (JsonProcessingException e) {
            throw new JsonParsingException("JSON PARSE ERROR", e);
        }
        String playerWonMessage = "Trick was won by player: " + trickWonMessage.getWinningPlayerName();
        activeGame.runOnUiThread(() -> {
                activeGame.showPlayerWonTrickMessage(playerWonMessage);
                activeGame.clearPlayedCards();
        });
    }

    public void playCard(String name, String color, int value) {
        Log.d("PLAY CARD", "Attempting to play card: " + name + ", " + color + ", " + value);
        CardPlayRequest playCardRequest = new CardPlayRequest();
        playCardRequest.setLobbyCode(dataHandler.getLobbyCode());
        playCardRequest.setUserID(dataHandler.getPlayerID());
        playCardRequest.setName(name);
        playCardRequest.setColor(color.replace("color_", ""));
        playCardRequest.setValue(String.valueOf(value));

        String jsonPayload = new Gson().toJson(playCardRequest);
        stompHandler.playCard(jsonPayload);
        Log.d("PLAY CARD", "Attempting to play card: " + name + ", " + color + ", " + value);
    }

    public void handleRoundEnd() {
        gameData.getCardsPlayed().clear();
        gameData.getCardList().clear();

        activeGame.showCheatingAccusationView();

        activeGame.getData();
    }

    private void subscribeForRoundEndEvent() {
        stompHandler.subscribeToRoundEndEvent(dataHandler.getLobbyCode(), response -> handleRoundEnd());
    }
}
