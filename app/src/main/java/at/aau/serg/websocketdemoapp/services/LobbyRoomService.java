package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import at.aau.serg.websocketdemoapp.activities.Lobbyroom;

public class LobbyRoomService {

    SharedPreferences sharedPreferences;
    Lobbyroom lobbyActivity;

    public LobbyRoomService(Context context, Lobbyroom activity) {
        sharedPreferences = context.getSharedPreferences("druids_data", Context.MODE_PRIVATE);
        this.lobbyActivity = activity;
    }

    public void backButtonClicked() {
        lobbyActivity.changeToStartActivity();
    }

    public void setPlayerName(TextView textView) {
        textView.setText(readPlayerName());
    }

    private String readPlayerName() {
       return sharedPreferences.getString("playerName", "");
    }
}
