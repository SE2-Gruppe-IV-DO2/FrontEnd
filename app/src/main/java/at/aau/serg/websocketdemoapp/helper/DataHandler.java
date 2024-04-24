package at.aau.serg.websocketdemoapp.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class DataHandler {
    private final SharedPreferences sharedPreferences;
    private static DataHandler instance;

    private DataHandler(Context context) {
        sharedPreferences = context.getSharedPreferences("data_druids", Context.MODE_PRIVATE);
    }

    public static DataHandler getInstance(Context context) {
        if(instance == null) {
            instance = new DataHandler(context);
        }
        return instance;
    }

    public void setPlayerName(String playerName) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("playerName", playerName);
        editor.apply();
    }

    public String getPlayerName() {
        return sharedPreferences.getString("playerName", "");
    }

    public void setPlayerID(String playerID) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("playerID", playerID);
        editor.apply();
    }

    public String getPlayerID() {
        return sharedPreferences.getString("playerID", "");
    }

    public void setLobbyCode(String lobbyCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("lobbyCode", lobbyCode);
        editor.apply();
    }

    public String getLobbyCode() {
        return sharedPreferences.getString("lobbyCode", "");
    }
}
