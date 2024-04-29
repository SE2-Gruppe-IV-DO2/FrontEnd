package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
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
    }

    public void backButtonClicked() {
        lobbyActivity.changeToStartActivity();
    }

    public void createGameButtonClicked() {lobbyActivity.changeToGameActivity();}

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
}
