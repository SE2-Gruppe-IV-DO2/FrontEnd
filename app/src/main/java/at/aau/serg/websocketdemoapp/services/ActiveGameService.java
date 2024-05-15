package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.dto.CardPlayRequest;
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


    private boolean isCurrentlyActivePlayer = false;

    public ActiveGameService(Context context, ActiveGame activeGame, GameData gameData) {
        dataHandler = DataHandler.getInstance(context);
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();
        this.gameData = gameData;

        subscribeForPlayerChangedEvent();
    }

    public ActiveGameService(ActiveGame activeGame, DataHandler dataHandler, GameData gameData) {
        this.dataHandler = dataHandler;
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();
        this.gameData = gameData;

        subscribeForPlayerChangedEvent();
    }

    @Override
    public void onCardFling(String cardName) {
        Card card = gameData.findCardByCardName(cardName);
        gameData.getCardsPlayed().add(card);
        if (gameData.getCardList().remove(card)) {
            Log.d("REMOVE CARD", "CARD REMOVED SUCCESSFULLY");
        }
        Log.d("FLINGED", card.toString());
        activeGame.refreshActiveGame();
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
        stompHandler.subscribeForPlayerChangedEvent(serverResponse -> {
            setActivePlayer(serverResponse);
        });
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

    public void playCard(String color, int value) {
        CardPlayRequest playCardRequest = new CardPlayRequest();
        playCardRequest.setLobbyCode(dataHandler.getLobbyCode());
        playCardRequest.setPlayerID(dataHandler.getPlayerID());
        playCardRequest.setColor(color);
        playCardRequest.setValue(value);

        String jsonPayload = new Gson().toJson(playCardRequest);
        stompHandler.playCard(jsonPayload);
    }
}
