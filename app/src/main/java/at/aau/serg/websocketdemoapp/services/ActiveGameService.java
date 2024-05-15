package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import at.aau.serg.websocketdemoapp.activities.ActiveGame;
import at.aau.serg.websocketdemoapp.dto.CardPlayRequest;
import at.aau.serg.websocketdemoapp.dto.GameData;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class ActiveGameService {
    private final DataHandler dataHandler;
    private final ActiveGame activeGame;
    private GameData gameData;

    StompHandler stompHandler;
    private static final String TAG = "Stomp";

    public ActiveGameService(Context context, ActiveGame activeGame) {
        dataHandler = DataHandler.getInstance(context);
        this.activeGame = activeGame;
        stompHandler = StompHandler.getInstance();
        gameData = new GameData();
    }

    public void getData() {
        stompHandler.dealNewRound(dataHandler.getLobbyCode(), dataHandler.getPlayerID(), callback -> {
            Log.d("Handcards", callback);
            activeGame.runOnUiThread(() -> activeGame.refreshActiveGame(callback));
        });
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
