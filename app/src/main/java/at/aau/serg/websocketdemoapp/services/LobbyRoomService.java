package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import at.aau.serg.websocketdemoapp.R;
import at.aau.serg.websocketdemoapp.activities.Lobbyroom;
import at.aau.serg.websocketdemoapp.networking.StompHandler;

public class LobbyRoomService {

    SharedPreferences sharedPreferences;
    Lobbyroom lobbyActivity;
    StompHandler stompHandler = new StompHandler("ws://10.0.2.2:8080/websocket-example-broker");
    TextView participants;

    public LobbyRoomService(Context context, Lobbyroom activity) {
        sharedPreferences = context.getSharedPreferences("druids_data", Context.MODE_PRIVATE);
        this.lobbyActivity = activity;
        participants = lobbyActivity.findViewById(R.id.participants);
    }

    public void backButtonClicked() {
        lobbyActivity.changeToStartActivity();
    }

    private void setPlayerName() {
        participants.append(sharedPreferences.getString("playerName", "") + "\n");
    }

    public Runnable setLobbyCode(String code) {
        TextView lobbyCode = lobbyActivity.findViewById(R.id.lobbyCode);
        lobbyCode.setText(code);
        return null;
    }

    private String readPlayerName() {
       return sharedPreferences.getString("playerName", "");
    }

    public void onCreation() {
        stompHandler.connectToServer();
        new Thread(() -> {
            Long time = System.currentTimeMillis()/1000;
            stompHandler.createLobby(time.toString(), readPlayerName(), code -> {
                lobbyActivity.runOnUiThread(setLobbyCode(code));
            });
        }).start();
        setPlayerName();
    }

    public void setStompHandler(StompHandler stompHandler) {
        this.stompHandler = stompHandler;
    }
}
