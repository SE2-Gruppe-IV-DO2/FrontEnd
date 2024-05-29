package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.widget.TextView;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.activities.LobbyRoom;
import at.aau.serg.websocketdemoapp.helper.DataHandler;
import at.aau.serg.websocketdemoapp.networking.StompHandler;
import lombok.Getter;
import lombok.Setter;

public class LobbyRoomService {
    private final LobbyRoom lobbyActivity;
    @Setter
    private StompHandler stompHandler;

    @Getter
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
        addPlayerNameToLobby(dataHandler.getPlayerName());
    }

    public void setLobbyCode() {
        lobbyCode.setText(dataHandler.getLobbyCode());
    }

    public void onCreation() {
        setPlayerName();
        setLobbyCode();
    }

    public void initGameStartSubscription() {
        this.stompHandler.initGameStartSubscription(this.lobbyActivity);
    }

    public void addPlayerNameToLobby(String playerName) {
        participants.append(playerName + "\n");
    }

    public void initPlayerJoinedLobbySubscription() {
        this.stompHandler.subscribeForPlayerJoinedLobbyEvent(this::addPlayerNameToLobby);
    }

    public void startGame() {
        this.stompHandler.startGameForLobby(this.dataHandler.getLobbyCode());
    }
}
