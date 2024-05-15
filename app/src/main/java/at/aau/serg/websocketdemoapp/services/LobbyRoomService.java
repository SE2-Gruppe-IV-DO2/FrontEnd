package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.activities.LobbyRoom;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class LobbyRoomService {
    private final LobbyRoom lobbyActivity;
    private StompHandler stompHandler;
    private final TextView participants;
    private final DataHandler dataHandler;
    private final TextView lobbyCode;

    public LobbyRoomService(Context context, LobbyRoom activity) {
        dataHandler = DataHandler.getInstance(context);
        stompHandler = StompHandler.getInstance();
        this.lobbyActivity = activity;
        participants = lobbyActivity.findViewById(R.id.participants);
        lobbyCode = lobbyActivity.findViewById(R.id.lobbyCode);

        initPlayerJoinedLobbySubscription();
        initGameStartSubscription();
    }

    public void backButtonClicked() {
        lobbyActivity.changeToMainActivity();
    }

    public void startButtonClicked() {this.startGame();}

    private void setPlayerName() {
        participants.append(dataHandler.getPlayerName() + "\n");
    }

    public void setLobbyCode() {
        lobbyCode.setText(dataHandler.getLobbyCode());
    }

    public void onCreation() {
        setPlayerName();
        setLobbyCode();
    }

    public void setStompHandler(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }

    public void initGameStartSubscription() {
        this.stompHandler.initGameStartSubscription(this.lobbyActivity);
    }

    public void initPlayerJoinedLobbySubscription() {
        Log.d("JOIN", "boin bound");
        this.stompHandler.subscribeForPlayerJoinedLobbyEvent(serverResponse -> {
            participants.append(serverResponse + "\n");
            Log.d("JOIN", "serverResponse:" + serverResponse);

        });
    }

    public void startGame() {
        // TODO: Remove this! Fügt 2 virtuelle Spieler zur Lobby um starten zu können
        //stompHandler.joinLobby(dataHandler.getLobbyCode(), "Test1", "test1", callback -> {
        //});
        //stompHandler.joinLobby(dataHandler.getLobbyCode(), "Test2", "test2", callback -> {
        //});
        this.stompHandler.startGameForLobby(this.dataHandler.getLobbyCode());
    }
}
