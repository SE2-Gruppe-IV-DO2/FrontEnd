package at.aau.serg.websocketdemoapp.services;

import android.content.Context;

import com.google.gson.Gson;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.dto.CardPlayRequest;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class ActiveGameService {
    private final DataHandler dataHandler;
    private final ActiveGame activeGame;
    private final StompHandler stompHandler;
    private static final String TAG = "DealRound";


    private boolean isCurrentlyActivePlayer = false;

    public ActiveGameService(Context context, ActiveGame activeGame) {
        dataHandler = DataHandler.getInstance(context);
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();

        subscribeForPlayerChangedEvent();
    }

    public ActiveGameService(ActiveGame activeGame, DataHandler dataHandler) {
        this.dataHandler = dataHandler;
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();

        subscribeForPlayerChangedEvent();
    }

    public void getData() {
        try {
            stompHandler.dealNewRound(dataHandler.getLobbyCode(), dataHandler.getPlayerID(), callback -> {
                dataHandler.setGameData(callback);
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //activeGame.refreshActiveGame(dataHandler.getGameData());
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
