package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import lombok.Data;
import lombok.Getter;

public class ActiveGameService {
    private final DataHandler dataHandler;
    private final ActiveGame activeGame;
    private final StompHandler stompHandler;
    private static final String TAG = "Stomp";

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
        stompHandler.dealNewRound(dataHandler.getLobbyCode(), dataHandler.getPlayerID(), callback -> {
            dataHandler.setGameData(callback);
            Log.d(TAG, callback);
        });
        activeGame.refreshActiveGame(dataHandler.getGameData());
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
}
