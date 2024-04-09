package at.aau.serg.websocketdemoapp.services;

import android.content.Context;
import android.content.SharedPreferences;
import at.aau.serg.websocketdemoapp.activities.JoinLobby;

public class JoinLobbyService {
    SharedPreferences sharedPreferences;
    JoinLobby joinLobbyActivity;

    public JoinLobbyService(Context context, JoinLobby joinLobby) {
        this.sharedPreferences = context.getSharedPreferences("druids_data", Context.MODE_PRIVATE);
        this.joinLobbyActivity = joinLobby;
    }

    public void backButtonClicked() {
        joinLobbyActivity.changeToStartActivity();
    }
}
