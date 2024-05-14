package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class ActiveGameService {
    private final DataHandler dataHandler;
    private final ActiveGame activeGame;
    StompHandler stompHandler;
    private static final String TAG = "Stomp";
    private boolean isCurrentlyActivePlayer = false;

    public ActiveGameService(Context context, ActiveGame activeGame) {
        dataHandler = DataHandler.getInstance(context);
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();

        subscribeForPlayerChangedEvent(dataHandler.getPlayerID());
    }

    public void getData() {
        stompHandler.dealNewRound(dataHandler.getLobbyCode(), dataHandler.getPlayerID(), callback -> {
            dataHandler.setGameData(callback);
            Log.d(TAG, callback);
        });
        activeGame.refreshActiveGame(dataHandler.getGameData());
    }

    private void subscribeForPlayerChangedEvent(String playerId) {
        stompHandler.subscribeForPlayerChangedEvent(serverResponse -> {
            if (playerId.equals(serverResponse)) {
                Log.d("SUBSCRIBE", "IS ACTIVE PLAYER");
                isCurrentlyActivePlayer = true;
                activeGame.updateActivePlayerInformation(dataHandler.getPlayerName());
            }
            else {
                Log.d("SUBSCRIBE", "NOT LONGER ACTIVE PLAYER");
                isCurrentlyActivePlayer = false;
                activeGame.updateActivePlayerInformation("OTHER_PLAYER_NAME");
            }
        });
    }

}
