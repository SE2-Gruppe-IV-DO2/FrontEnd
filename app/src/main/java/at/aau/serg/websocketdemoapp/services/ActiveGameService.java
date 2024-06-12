package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.dto.CardPlayRequest;
import at.aau.serg.websocketdemoapp.dto.CardPlayedRequest;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.dto.TrickWonMessage;
import at.aau.serg.websocketdemoapp.helper.Card;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.helper.FlingListener;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import lombok.Setter;

public class ActiveGameService implements FlingListener {
    private final DataHandler dataHandler;
    private final ActiveGame activeGame;
    private final StompHandler stompHandler;
    private static final String TAG = "DealRound";
    private final GameData gameData;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private boolean isCurrentlyActivePlayer = false;
    @Setter
    private boolean preventCardFling = false;

    public ActiveGameService(Context context, ActiveGame activeGame, GameData gameData) {
        dataHandler = DataHandler.getInstance(context);
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();
        this.gameData = gameData;

        subscribeForPlayerChangedEvent();
        subscribeForPlayCardEvent();
        subscribeForRoundEndEvent();
        subscribeForPlayerWonTrickEvent();
    }

    @Override
    public void onCardFling(String cardName) {
        if (!isCurrentlyActivePlayer() || preventCardFling) return;
        Card card = gameData.findCardByCardName(cardName);
        Log.d("CARD FOUND", card.getColor() + card.getValue());
        playCard(card.getName(), card.getColor(), card.getValue());
        if (gameData.getCardList().remove(card)) {
            Log.d("REMOVE CARD", "CARD REMOVED SUCCESSFULLY");
        }
        Log.d("FLING", card.toString());
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
            handlePlayCardResponse(response);
        });
    }

    private void subscribeForPlayerWonTrickEvent() {
        stompHandler.subscribeForPlayerWonTrickEvent(response -> {
            handleTrickWon(response);
            gameData.getCardsPlayed().clear();
        });
    }

    public void handlePlayCardResponse(String playCardJSON) {
        activeGame.runOnUiThread(() -> {
            try {
                CardPlayedRequest cardPlayedRequest = objectMapper.readValue(playCardJSON, CardPlayedRequest.class);
                Log.d(TAG, "Card played: " + cardPlayedRequest);
                Card c = new Card();
                c.setCardType(cardPlayedRequest.getCardType());
                c.setValue(Integer.valueOf(cardPlayedRequest.getValue()));
                c.setColor(cardPlayedRequest.getColor());
                c.createImagePath();
                gameData.getCardsPlayed().add(c);
                Log.d("CARD PLAYED", "Added card to played list: " + c);
                activeGame.displayCardsPlayed();
            } catch (JsonProcessingException e) {
                Log.e(TAG, "Error parsing play card response", e);
            }
        });
    }

    public void handleTrickWon(String trickWonJson) {
        activeGame.runOnUiThread(() -> {
            try {
                TrickWonMessage trickWonMessage = objectMapper.readValue(trickWonJson, TrickWonMessage.class);
                String playerWonMessage = "Trick was won by player: " + trickWonMessage.getWinningPlayerName();
                activeGame.showPlayerWonTrickMessage(playerWonMessage);
            } catch (JsonProcessingException e) {
                throw new IllegalArgumentException("Wrong message type!");
            }
        });
    }

    public void playCard(String name, String color, int value) {
        CardPlayRequest playCardRequest = new CardPlayRequest();
        playCardRequest.setLobbyCode(dataHandler.getLobbyCode());
        playCardRequest.setUserID(dataHandler.getPlayerID());
        playCardRequest.setName(name);
        playCardRequest.setColor(color.replace("color_", ""));
        playCardRequest.setValue(String.valueOf(value));

        String jsonPayload = new Gson().toJson(playCardRequest);
        stompHandler.playCard(jsonPayload);
    }

    public void handleRoundEnd() {
        gameData.getCardsPlayed().clear();
        gameData.getCardList().clear();
        activeGame.getData();
    }

    private void subscribeForRoundEndEvent() {
        stompHandler.subscribeToRoundEndEvent(dataHandler.getLobbyCode(), response -> handleRoundEnd());
    }
}
