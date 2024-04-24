package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;
import android.widget.EditText;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.activities.JoinLobby;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class JoinLobbyService {
    private final JoinLobby joinLobbyActivity;
    private final DataHandler dataHandler;
    private static final String TAG = "JoinLobby";
    private final EditText lobbyField;
    private final StompHandler stompHandler;

    public JoinLobbyService(Context context, JoinLobby joinLobby) {
        stompHandler = StompHandler.getInstance();
        dataHandler = DataHandler.getInstance(context);
        this.joinLobbyActivity = joinLobby;
        lobbyField = joinLobbyActivity.findViewById(R.id.enterLobbyCode);
    }

    public void backButtonClicked() {
        joinLobbyActivity.changeToStartActivity();
    }

    public void joinLobbyWithIDClicked() {
        dataHandler.setLobbyCode(lobbyField.getText().toString());
        new Thread(() -> stompHandler.joinLobby(dataHandler.getLobbyCode(), dataHandler.getPlayerID(), dataHandler.getPlayerName(), callback -> {
            dataHandler.setLobbyCode(callback);
            Log.d(TAG, callback);
        })).start();
        joinLobbyActivity.changeToLobbyRoomActivity();
    }
}
