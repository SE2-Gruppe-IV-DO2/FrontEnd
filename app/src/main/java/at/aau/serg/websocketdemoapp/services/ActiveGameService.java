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

    public ActiveGameService(Context context, ActiveGame activeGame) {
        dataHandler = DataHandler.getInstance(context);
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();
    }

    public void getData() {
        new Thread(() -> stompHandler.dealNewRound(dataHandler.getLobbyCode(), callback -> {
            dataHandler.setGameData(callback);
            Log.d(TAG, callback);
        })).start();
        activeGame.refreshActiveGame(dataHandler.getGameData());
    }

}
